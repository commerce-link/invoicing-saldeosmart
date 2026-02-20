package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.FIELD)
class ContractorMergeRequest {

    @XmlElementWrapper(name = "CONTRACTORS")
    @XmlElement(name = "CONTRACTOR")
    List<ContractorData> contractors;

    ContractorMergeRequest() {}

    static ContractorMergeRequest of(ContractorData contractor) {
        ContractorMergeRequest request = new ContractorMergeRequest();
        request.contractors = List.of(contractor);
        return request;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class ContractorData {

        @XmlElement(name = "CONTRACTOR_PROGRAM_ID")
        String contractorProgramId;

        @XmlElement(name = "SHORT_NAME")
        String shortName;

        @XmlElement(name = "FULL_NAME")
        String fullName;

        @XmlElement(name = "CUSTOMER")
        Boolean customer;

        @XmlElement(name = "VAT_NUMBER")
        String vatNumber;

        @XmlElement(name = "CITY")
        String city;

        @XmlElement(name = "POSTCODE")
        String postcode;

        @XmlElement(name = "STREET")
        String street;

        @XmlElement(name = "COUNTRY_ISO3166A2")
        String country;
    }
}
