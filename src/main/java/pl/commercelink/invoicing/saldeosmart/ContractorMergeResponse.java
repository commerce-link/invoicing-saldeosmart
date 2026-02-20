package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.FIELD)
class ContractorMergeResponse {

    @XmlElement(name = "STATUS")
    String status;

    @XmlElement(name = "ERROR_CODE")
    String errorCode;

    @XmlElement(name = "ERROR_MESSAGE")
    String errorMessage;

    @XmlElementWrapper(name = "RESULTS")
    @XmlElement(name = "CONTRACTOR")
    List<ContractorResult> contractors;

    @XmlAccessorType(XmlAccessType.FIELD)
    static class ContractorResult {

        @XmlElement(name = "CONTRACTOR_PROGRAM_ID")
        String contractorProgramId;

        @XmlElement(name = "STATUS")
        String status;

        @XmlElement(name = "STATUS_MESSAGE")
        String statusMessage;

        @XmlElement(name = "CONTRACTOR_ID")
        String contractorId;
    }
}
