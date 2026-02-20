package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.*;

import java.util.List;

public class SaldeoSmartInvoicingProvider implements InvoicingProvider {

    private final ContractorService contractorService;
    private final CompanyService companyService;
    private final InvoiceService invoiceService;

    public SaldeoSmartInvoicingProvider(String apiUrl, String username, String apiToken, String companyProgramId) {
        SaldeoSmartHttpClient httpClient = new SaldeoSmartHttpClient(apiUrl, username, apiToken);
        this.contractorService = new ContractorService(httpClient, companyProgramId);
        this.companyService = new CompanyService(httpClient, companyProgramId);
        this.invoiceService = new InvoiceService(httpClient, companyProgramId);
    }

    @Override
    public Invoice createInvoice(InvoiceRequest request) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Invoice fetchInvoiceById(String invoiceId, InvoiceDirection direction) {
        return invoiceService.fetchById(invoiceId, direction);
    }

    @Override
    public List<Invoice> fetchInvoicesByOrderId(String orderId, InvoiceDirection direction) {
        return List.of();
    }

    @Override
    public byte[] fetchInvoicePdf(String invoiceId) {
        return invoiceService.fetchPdf(invoiceId);
    }

    @Override
    public BillingParty fetchCostCenterById(String costCenterId) {
        return companyService.fetchCostCenter();
    }

    @Override
    public BillingParty fetchBillingPartyById(String billingPartyId) {
        return contractorService.fetchById(billingPartyId);
    }

    @Override
    public BillingParty fetchBillingPartyByShortcut(String billingPartyShortcut) {
        return contractorService.fetchByShortcut(billingPartyShortcut);
    }
}
