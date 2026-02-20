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
                .orElseThrow(() -> new RuntimeException("Contractor not found: " + billingPartyId));
    }

    BillingParty fetchByShortcut(String shortcut) {
        return fetchContractors().stream()
                .filter(c -> shortcut.equals(c.shortName))
                .findFirst()
                .map(this::toBillingParty)
                .orElseThrow(() -> new RuntimeException("Contractor not found by shortcut: " + shortcut));
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

    private BillingParty toBillingParty(Contractor contractor) {
        return ContractorMapper.toBillingParty(contractor);
    }
}
