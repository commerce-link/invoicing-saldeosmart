package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.*;

import java.time.LocalDate;
import java.util.List;

public class SaldeoSmartInvoicingProvider implements InvoicingProvider {

    private final SaldeoSmartHttpClient httpClient;
    private final String companyProgramId;

    public SaldeoSmartInvoicingProvider(String apiUrl, String username, String apiToken, String companyProgramId) {
        this.httpClient = new SaldeoSmartHttpClient(apiUrl, username, apiToken);
        this.companyProgramId = companyProgramId;
    }

    @Override
    public Invoice createInvoice(InvoiceRequest request) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Invoice fetchInvoiceById(String invoiceId, InvoiceDirection direction) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Invoice fetchInvoiceByNumber(String invoiceNumber, InvoiceDirection direction) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Invoice fetchInvoiceWithPositions(String invoiceId, InvoiceDirection direction) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public List<Invoice> fetchInvoices(LocalDate dateFrom, LocalDate dateTo, InvoiceDirection direction) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public byte[] fetchInvoicePdf(String invoiceId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BillingParty fetchCostCenterById(String costCenterId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BillingParty fetchBillingPartyById(String billingPartyId) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public BillingParty fetchBillingPartyByShortcut(String billingPartyShortcut) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
