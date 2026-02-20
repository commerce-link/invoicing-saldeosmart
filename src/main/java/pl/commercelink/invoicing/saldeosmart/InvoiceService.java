package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class InvoiceService {

    private final SaldeoSmartHttpClient httpClient;
    private final String companyProgramId;

    InvoiceService(SaldeoSmartHttpClient httpClient, String companyProgramId) {
        this.httpClient = httpClient;
        this.companyProgramId = companyProgramId;
    }

    Invoice create(InvoiceRequest request, String contractorId) {
        InvoiceAddRequest addRequest = buildAddRequest(request, contractorId);
        InvoiceAddResponse response = httpClient.post(
                "/api/xml/3.0/invoice/add",
                Map.of("company_program_id", companyProgramId),
                addRequest,
                InvoiceAddResponse.class
        );
        if (response.status != null && !"OK".equals(response.status)) {
            throw new RuntimeException("invoice.add failed: " + response.errorCode + " " + response.errorMessage);
        }
        if (response.invoice == null || response.invoice.invoiceId == null) {
            throw new RuntimeException("invoice.add returned no invoice ID");
        }
        return fetchById(response.invoice.invoiceId, InvoiceDirection.Sale);
    }

    Invoice fetchById(String invoiceId, InvoiceDirection direction) {
        InvoiceListByIdResponse response = fetchRawResponse(invoiceId);
        InvoiceListByIdResponse.Invoice invoice = findInvoice(response, invoiceId);
        if (invoice == null) {
            return null;
        }
        return toInvoice(invoice, response.contractors, direction);
    }

    byte[] fetchPdf(String invoiceId) {
        InvoiceListByIdResponse response = fetchRawResponse(invoiceId);
        InvoiceListByIdResponse.Invoice invoice = findInvoice(response, invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found: " + invoiceId);
        }
        if (invoice.source == null || invoice.source.isBlank()) {
            throw new RuntimeException("No PDF source for invoice: " + invoiceId);
        }
        return httpClient.downloadBytes(invoice.source);
    }

    private InvoiceListByIdResponse fetchRawResponse(String invoiceId) {
        InvoiceListByIdRequest request = InvoiceListByIdRequest.of(invoiceId);
        InvoiceListByIdResponse response = httpClient.post(
                "/api/xml/3.0/invoice/listbyid",
                Map.of("company_program_id", companyProgramId),
                request,
                InvoiceListByIdResponse.class
        );
        if (!"OK".equals(response.status)) {
            throw new RuntimeException("invoice.listbyid failed: " + response.errorCode + " " + response.errorMessage);
        }
        return response;
    }

    private InvoiceListByIdResponse.Invoice findInvoice(InvoiceListByIdResponse response, String invoiceId) {
        if (response.invoices == null || response.invoices.isEmpty()) {
            return null;
        }
        return response.invoices.stream()
                .filter(i -> invoiceId.equals(i.invoiceId))
                .findFirst()
                .orElse(null);
    }

    private Invoice toInvoice(InvoiceListByIdResponse.Invoice inv,
                              List<Contractor> contractors,
                              InvoiceDirection direction) {
        double gross = parseDouble(inv.sum);
        double net = sumNetFromVatRegistries(inv.vatRegistries);
        String currency = inv.currency != null && !inv.currency.isBlank()
                ? inv.currency : Price.DEFAULT_CURRENCY;
        double exchangeRate = parseDouble(inv.currencyRate);
        if (exchangeRate <= 0) exchangeRate = 1.0;
        boolean paid = "true".equalsIgnoreCase(inv.isInvoicePaid);
        LocalDate paymentDate = inv.paymentDate != null && !inv.paymentDate.isBlank()
                ? LocalDate.parse(inv.paymentDate) : null;

        BillingParty contractor = findContractor(inv.contractor, contractors);
        BillingParty seller = direction == InvoiceDirection.Purchase ? contractor : null;
        BillingParty buyer = direction == InvoiceDirection.Sale ? contractor : null;

        return new Invoice(
                inv.invoiceId,
                inv.number,
                null,
                new Price(net, gross, currency),
                inv.source,
                currency,
                exchangeRate,
                paid,
                paymentDate,
                toPositions(inv.invoiceItems, currency, exchangeRate),
                seller,
                buyer
        );
    }

    private double sumNetFromVatRegistries(List<InvoiceListByIdResponse.VatRegistry> registries) {
        if (registries == null || registries.isEmpty()) return 0.0;
        return registries.stream()
                .mapToDouble(r -> parseDouble(r.netto))
                .sum();
    }

    private BillingParty findContractor(InvoiceListByIdResponse.InvoiceContractor invoiceContractor,
                                        List<Contractor> contractors) {
        if (invoiceContractor == null || invoiceContractor.contractorId == null) return null;
        if (contractors == null) return null;
        return contractors.stream()
                .filter(c -> invoiceContractor.contractorId.equals(c.contractorId))
                .findFirst()
                .map(this::toBillingParty)
                .orElse(null);
    }

    private BillingParty toBillingParty(Contractor contractor) {
        return ContractorMapper.toBillingParty(contractor);
    }

    private List<InvoicePosition> toPositions(List<InvoiceListByIdResponse.InvoiceItem> items,
                                              String currency, double exchangeRate) {
        if (items == null || items.isEmpty()) return List.of();
        boolean convertToPln = !"PLN".equalsIgnoreCase(currency);
        return items.stream()
                .map(item -> toPosition(item, convertToPln ? exchangeRate : 1.0))
                .toList();
    }

    private InvoicePosition toPosition(InvoiceListByIdResponse.InvoiceItem item, double rate) {
        int qty = (int) parseDouble(item.amount);
        double unitNet = parseDouble(item.unitValue) * rate;
        double totalGross = parseDouble(item.gross);
        double unitGross = qty > 0 ? (totalGross / qty) * rate : 0;
        Price price = new Price(unitNet, unitGross);
        return new InvoicePosition(null, item.name, qty, price);
    }

    private InvoiceAddRequest buildAddRequest(InvoiceRequest request, String contractorId) {
        double totalGross = request.positions().stream()
                .mapToDouble(p -> p.price().grossValue() * p.qty())
                .sum();
        boolean mpp = SplitPaymentPolicy.isRequired(
                request.billingParty(), totalGross, request.splitPaymentsEnabled());

        InvoiceAddRequest.Invoice invoice = new InvoiceAddRequest.Invoice();
        invoice.issueDate = LocalDate.now().toString();
        invoice.saleDate = request.sellDate() != null ? request.sellDate().toString() : LocalDate.now().toString();
        invoice.dueDate = LocalDate.now().plusDays(request.paymentTerms()).toString();
        invoice.calculatedFromGross = true;
        invoice.isMpp = mpp;
        invoice.purchaserContractorId = contractorId;
        invoice.currency = "PLN";
        invoice.paymentType = mpp ? "SPLIT_PAYMENT" : "TRANSFER";
        invoice.footer = request.orderId();
        invoice.items = request.positions().stream()
                .map(this::toAddItem)
                .toList();

        if (request.paidAmount() > 0) {
            InvoiceAddRequest.InvoicePayments payments = new InvoiceAddRequest.InvoicePayments();
            payments.paymentAmount = String.format(Locale.US, "%.2f", request.paidAmount());
            payments.paymentDate = LocalDate.now().toString();
            invoice.payments = payments;
        }

        InvoiceAddRequest addRequest = new InvoiceAddRequest();
        addRequest.invoice = invoice;
        return addRequest;
    }

    private InvoiceAddRequest.InvoiceItem toAddItem(InvoicePosition position) {
        InvoiceAddRequest.InvoiceItem item = new InvoiceAddRequest.InvoiceItem();
        item.name = position.name();
        item.amount = String.valueOf(position.qty());
        item.unit = "szt";
        item.unitValue = String.format(Locale.US, "%.2f", position.price().grossValue());
        item.rate = String.valueOf(position.price().vatRatePercent());
        return item;
    }

    private static double parseDouble(String value) {
        if (value == null || value.isBlank()) return 0.0;
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
