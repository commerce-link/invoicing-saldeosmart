package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
class Contractor {

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
