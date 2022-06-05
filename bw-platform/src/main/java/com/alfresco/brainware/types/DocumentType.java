package com.alfresco.brainware.types;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Document")
public class DocumentType {

    private DocPages docPages;
    private InvoiceType invHeader;

    public DocPages getDocPages() {
        return docPages;
    }

    @XmlElement(name = "DocPages")
    public void setDocPages(DocPages docPages) {
        this.docPages = docPages;
    }

    public InvoiceType getInvHeader() {
        return invHeader;
    }

    @XmlElement(name = "InvHeader")
    public void setInvHeader(InvoiceType invHeader) {
        this.invHeader = invHeader;
    }

}
