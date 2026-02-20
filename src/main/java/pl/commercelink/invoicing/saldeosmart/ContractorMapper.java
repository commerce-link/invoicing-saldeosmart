package pl.commercelink.invoicing.saldeosmart;

import pl.commercelink.invoicing.api.BillingParty;

class ContractorMapper {

    static BillingParty toBillingParty(Contractor contractor) {
        boolean hasVat = contractor.vatNumber != null && !contractor.vatNumber.isBlank();
        if (hasVat) {
            return BillingParty.company(
                    contractor.contractorId, contractor.fullName, contractor.street,
                    contractor.postcode, contractor.city, contractor.country,
                    contractor.vatNumber, contractor.shortName
            );
        }
        String[] nameParts = splitName(contractor.fullName);
        return BillingParty.individual(
                contractor.contractorId, nameParts[0], nameParts[1],
                contractor.street, contractor.postcode, contractor.city,
                contractor.country, contractor.shortName
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
