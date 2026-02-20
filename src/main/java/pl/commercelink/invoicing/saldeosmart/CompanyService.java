package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.BillingParty;

import java.util.List;
import java.util.Map;

class CompanyService {

    private final SaldeoSmartHttpClient httpClient;
    private final String companyProgramId;

    CompanyService(SaldeoSmartHttpClient httpClient, String companyProgramId) {
        this.httpClient = httpClient;
        this.companyProgramId = companyProgramId;
    }

    BillingParty fetchCostCenter() {
        CompanyListResponse response = httpClient.get(
                "/api/xml/1.17/company/list",
                Map.of("company_program_id", companyProgramId),
                CompanyListResponse.class
        );
        if (!"OK".equals(response.status)) {
            throw new RuntimeException("company.list failed: " + response.errorCode + " " + response.errorMessage);
        }
        List<CompanyListResponse.Company> companies = response.companies;
        if (companies == null || companies.isEmpty()) {
            return null;
        }
        return toBillingParty(companies.getFirst());
    }

    private BillingParty toBillingParty(CompanyListResponse.Company company) {
        return BillingParty.company(
                company.companyId,
                company.fullName,
                company.street,
                company.postcode,
                company.city,
                "PL",
                company.vatNumber,
                company.shortName
        );
    }
}
