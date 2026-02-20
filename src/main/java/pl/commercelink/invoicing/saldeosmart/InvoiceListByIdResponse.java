package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "RESPONSE")
@XmlAccessorType(XmlAccessType.FIELD)
class InvoiceListByIdResponse {

    @XmlElement(name = "STATUS")
    String status;

    @XmlElement(name = "ERROR_CODE")
    String errorCode;

    @XmlElement(name = "ERROR_MESSAGE")
    String errorMessage;

    @XmlElementWrapper(name = "INVOICES")
    @XmlElement(name = "INVOICE")
    List<Invoice> invoices;

    @XmlElementWrapper(name = "CONTRACTORS")
    @XmlElement(name = "CONTRACTOR")
    List<Contractor> contractors;

    @XmlAccessorType(XmlAccessType.FIELD)
    static class Invoice {

        @XmlElement(name = "INVOICE_ID")
        String invoiceId;

        @XmlElement(name = "NUMBER")
        String number;

        @XmlElement(name = "SUM")
        String sum;

        @XmlElement(name = "SOURCE")
        String source;

        @XmlElement(name = "CURRENCY_ISO4217")
        String currency;

        @XmlElement(name = "CURRENCY_RATE")
        String currencyRate;

        @XmlElement(name = "IS_INVOICE_PAID")
        String isInvoicePaid;

        @XmlElement(name = "PAYMENT_DATE")
        String paymentDate;

        @XmlElement(name = "CONTRACTOR")
        InvoiceContractor contractor;

        @XmlElementWrapper(name = "VAT_REGISTRIES")
        @XmlElement(name = "VAT_REGISTRY")
        List<VatRegistry> vatRegistries;

        @XmlElementWrapper(name = "INVOICE_ITEMS")
        @XmlElement(name = "INVOICE_ITEM")
        List<InvoiceItem> invoiceItems;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class InvoiceContractor {

        @XmlElement(name = "CONTRACTOR_ID")
        String contractorId;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class VatRegistry {

        @XmlElement(name = "NETTO")
        String netto;

        @XmlElement(name = "RATE")
        String rate;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class InvoiceItem {

        @XmlElement(name = "NAME")
        String name;

        @XmlElement(name = "AMOUNT")
        String amount;

        @XmlElement(name = "UNIT_VALUE")
        String unitValue;

        @XmlElement(name = "NETTO")
        String netto;

        @XmlElement(name = "GROSS")
        String gross;

        @XmlElement(name = "RATE")
        String rate;
    }

}
