package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.FIELD)
class CompanyListResponse {

    @XmlElement(name = "STATUS")
    String status;

    @XmlElement(name = "ERROR_CODE")
    String errorCode;

    @XmlElement(name = "ERROR_MESSAGE")
    String errorMessage;

    @XmlElementWrapper(name = "COMPANIES")
    @XmlElement(name = "COMPANY")
    List<Company> companies;

    @XmlAccessorType(XmlAccessType.FIELD)
    static class Company {

        @XmlElement(name = "COMPANY_PROGRAM_ID")
        String companyProgramId;

        @XmlElement(name = "COMPANY_ID")
        String companyId;

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
    }
}
