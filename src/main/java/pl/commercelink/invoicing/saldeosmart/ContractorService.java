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

    private List<ContractorListResponse.Contractor> fetchContractors() {
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

    private BillingParty toBillingParty(ContractorListResponse.Contractor contractor) {
        boolean hasVat = contractor.vatNumber != null && !contractor.vatNumber.isBlank();
        if (hasVat) {
            return BillingParty.company(
                    contractor.contractorId,
                    contractor.fullName,
                    contractor.street,
                    contractor.postcode,
                    contractor.city,
                    contractor.country,
                    contractor.vatNumber,
                    contractor.shortName
            );
        }
        String[] nameParts = splitName(contractor.fullName);
        return BillingParty.individual(
                contractor.contractorId,
                nameParts[0],
                nameParts[1],
                contractor.street,
                contractor.postcode,
                contractor.city,
                contractor.country,
                contractor.shortName
        );
    }

    private static String[] splitName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return new String[]{null, null};
        }
        int lastSpace = fullName.trim().lastIndexOf(' ');
        if (lastSpace < 0) {
            return new String[]{fullName.trim(), null};
        }
        return new String[]{
                fullName.substring(0, lastSpace).trim(),
                fullName.substring(lastSpace + 1).trim()
        };
    }
}
