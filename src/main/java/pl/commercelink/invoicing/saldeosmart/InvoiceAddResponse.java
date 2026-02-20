package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.FIELD)
class InvoiceAddResponse {

    @XmlElement(name = "STATUS")
    String status;

    @XmlElement(name = "ERROR_CODE")
    String errorCode;

    @XmlElement(name = "ERROR_MESSAGE")
    String errorMessage;

    @XmlElement(name = "INVOICE")
    Invoice invoice;

    @XmlAccessorType(XmlAccessType.FIELD)
    static class Invoice {

        @XmlElement(name = "INVOICE_ID")
        String invoiceId;
    }
}
