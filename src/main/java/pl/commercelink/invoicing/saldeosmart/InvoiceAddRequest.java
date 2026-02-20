package pl.commercelink.invoicing.saldeosmart;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "ROOT")
@XmlAccessorType(XmlAccessType.FIELD)
class InvoiceAddRequest {

    @XmlElement(name = "INVOICE")
    Invoice invoice;

    InvoiceAddRequest() {}

    @XmlAccessorType(XmlAccessType.FIELD)
    static class Invoice {

        @XmlElement(name = "ISSUE_DATE")
        String issueDate;

        @XmlElement(name = "SALE_DATE")
        String saleDate;

        @XmlElement(name = "DUE_DATE")
        String dueDate;

        @XmlElement(name = "CALCULATED_FROM_GROSS")
        Boolean calculatedFromGross;

        @XmlElement(name = "IS_MPP")
        Boolean isMpp;

        @XmlElement(name = "PURCHASER_CONTRACTOR_ID")
        String purchaserContractorId;

        @XmlElement(name = "CURRENCY_ISO4217")
        String currency;

        @XmlElement(name = "PAYMENT_TYPE")
        String paymentType;

        @XmlElement(name = "FOOTER")
        String footer;

        @XmlElementWrapper(name = "INVOICE_ITEMS")
        @XmlElement(name = "INVOICE_ITEM")
        List<InvoiceItem> items;

        @XmlElement(name = "INVOICE_PAYMENTS")
        InvoicePayments payments;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class InvoiceItem {

        @XmlElement(name = "NAME")
        String name;

        @XmlElement(name = "AMOUNT")
        String amount;

        @XmlElement(name = "UNIT")
        String unit;

        @XmlElement(name = "UNIT_VALUE")
        String unitValue;

        @XmlElement(name = "RATE")
        String rate;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class InvoicePayments {

        @XmlElement(name = "PAYMENT_AMOUNT")
        String paymentAmount;

        @XmlElement(name = "PAYMENT_DATE")
        String paymentDate;
    }
}
