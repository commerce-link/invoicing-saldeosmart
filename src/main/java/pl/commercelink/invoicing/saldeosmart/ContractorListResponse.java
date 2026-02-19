package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.FIELD)
class ContractorListResponse {

    @XmlElement(name = "STATUS")
    String status;

    @XmlElement(name = "ERROR_CODE")
    String errorCode;

    @XmlElement(name = "ERROR_MESSAGE")
    String errorMessage;

    @XmlElementWrapper(name = "CONTRACTORS")
    @XmlElement(name = "CONTRACTOR")
    List<Contractor> contractors;

    @XmlAccessorType(XmlAccessType.FIELD)
    static class Contractor {

        @XmlElement(name = "CONTRACTOR_ID")
        String contractorId;

        @XmlElement(name = "CONTRACTOR_PROGRAM_ID")
        String contractorProgramId;

        @XmlElement(name = "SHORT_NAME")
        String shortName;

        @XmlElement(name = "FULL_NAME")
        String fullName;

        @XmlElement(name = "VAT_NUMBER")
        String vatNumber;

        @XmlElement(name = "STREET")
        String street;

        @XmlElement(name = "POSTCODE")
        String postcode;

        @XmlElement(name = "CITY")
        String city;

        @XmlElement(name = "COUNTRY_ISO3166A2")
        String country;

        @XmlElement(name = "INACTIVE")
        String inactive;
    }
}
