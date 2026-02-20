package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.BillingParty;

import java.util.List;
import java.util.Map;

class ContractorService {

    private final SaldeoSmartHttpClient httpClient;
    private final String companyProgramId;

    ContractorService(SaldeoSmartHttpClient httpClient, String companyProgramId) {
        this.httpClient = httpClient;
        this.companyProgramId = companyProgramId;
    }

    BillingParty fetchById(String billingPartyId) {
        return fetchContractors().stream()
                .filter(c -> billingPartyId.equals(c.contractorId))
                .findFirst()
                .map(this::toBillingParty)
                .orElse(null);
    }

    BillingParty fetchByShortcut(String shortcut) {
        return fetchContractors().stream()
                .filter(c -> shortcut.equals(c.shortName))
                .findFirst()
                .map(this::toBillingParty)
                .orElse(null);
    }

    private List<Contractor> fetchContractors() {
        ContractorListResponse response = httpClient.get(
                "/api/xml/1.23/contractor/list",
                Map.of("company_program_id", companyProgramId),
                ContractorListResponse.class
        );
        if (!"OK".equals(response.status)) {
            throw new RuntimeException("contractor.list failed: " + response.errorCode + " " + response.errorMessage);
        }
        return response.contractors != null ? response.contractors : List.of();
    }

    String findOrCreate(BillingParty billingParty) {
        ContractorMergeRequest.ContractorData data = new ContractorMergeRequest.ContractorData();
        data.contractorProgramId = billingParty.id();
        data.shortName = billingParty.hasShortcut() ? billingParty.shortcut() : billingParty.id();
        data.fullName = billingParty.hasTaxId()
                ? billingParty.company()
                : (billingParty.name() + " " + billingParty.surname());
        data.vatNumber = billingParty.taxNo();
        data.street = billingParty.streetAndNumber();
        data.postcode = billingParty.postalCode();
        data.city = billingParty.city();
        data.country = billingParty.country();
        data.customer = true;

        ContractorMergeRequest request = ContractorMergeRequest.of(data);
        ContractorMergeResponse response = httpClient.post(
                "/api/xml/1.0/contractor/merge",
                Map.of("company_program_id", companyProgramId),
                request,
                ContractorMergeResponse.class
        );
        if (!"OK".equals(response.status)) {
            throw new RuntimeException("contractor.merge failed: " + response.errorCode + " " + response.errorMessage);
        }
        if (response.contractors == null || response.contractors.isEmpty()) {
            throw new RuntimeException("contractor.merge returned no results");
        }
        ContractorMergeResponse.ContractorResult result = response.contractors.getFirst();
        if ("NOT_VALID".equals(result.status) || "CONFLICT".equals(result.status)) {
            throw new RuntimeException("contractor.merge failed for contractor: " + result.status + " " + result.statusMessage);
        }
        return result.contractorId;
    }

    private BillingParty toBillingParty(Contractor contractor) {
        return ContractorMapper.toBillingParty(contractor);
    }
}
