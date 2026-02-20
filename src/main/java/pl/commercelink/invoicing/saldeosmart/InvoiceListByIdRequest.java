package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.FIELD)
class InvoiceListByIdRequest {

    @XmlElementWrapper(name = "INVOICES")
    @XmlElement(name = "INVOICE_ID")
    List<String> invoiceIds;

    InvoiceListByIdRequest() {
    }

    static InvoiceListByIdRequest of(String invoiceId) {
        InvoiceListByIdRequest request = new InvoiceListByIdRequest();
        request.invoiceIds = List.of(invoiceId);
        return request;
    }
}
