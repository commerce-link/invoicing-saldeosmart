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
}
