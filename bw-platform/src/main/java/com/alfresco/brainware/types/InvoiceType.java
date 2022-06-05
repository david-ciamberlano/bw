package com.alfresco.brainware.types;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
import java.util.Date;

public class InvoiceType {

    private String billToName;
    private String currency;
    private String documentType;
    private Date invDate;
//    private Integer InvNo;
//    private String InvoiceType;
//    private String POType;
    private BigDecimal Total;
//    private BigDecimal Tax;
//    private String VendorName;
//    private Integer VendorID;


    public String getBillToName() {
        return billToName;
    }

    @XmlElement(name = "BillToName")
    public void setBillToName(String billToName) {
        this.billToName = billToName;
    }

    public String getCurrency() {
        return currency;
    }

    @XmlElement(name = "Currency")
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDocumentType() {
        return documentType;
    }

    @XmlElement(name = "DocumentType")
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }


    public Date getInvDate() {
        return invDate;
    }

    @XmlElement(name = "InvDate")
    public void setInvDate(Date invDate) {
        this.invDate = invDate;
    }
//
//    @XmlElement(name = "x")
//    public void setInvNo(Integer invNo) {
//        InvNo = invNo;
//    }
//
//    @XmlElement(name = "x")
//    public void setInvoiceType(String invoiceType) {
//        InvoiceType = invoiceType;
//    }
//
//    @XmlElement(name = "x")
//    public void setPOType(String POType) {
//        this.POType = POType;
//    }
//


    public BigDecimal getTotal() {
        return Total;
    }

    @XmlElement(name = "Total")
    public void setTotal(BigDecimal total) {
        Total = total;
    }
//
//    @XmlElement(name = "x")
//    public void setTax(BigDecimal tax) {
//        Tax = tax;
//    }
//
//    @XmlElement(name = "x")
//    public void setVendorName(String vendorName) {
//        VendorName = vendorName;
//    }
//
//    @XmlElement(name = "x")
//    public void setVendorID(Integer vendorID) {
//        VendorID = vendorID;
//    }


    @Override
    public String toString() {
        return "InvoiceType{" +
                "BillToName='" + billToName + '\'' +
                ", Currency='" + currency + '\'' +
                ", DocumentType='" + documentType + '\'' +
                '}';
    }
}
