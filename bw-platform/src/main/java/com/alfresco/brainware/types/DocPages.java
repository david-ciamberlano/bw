package com.alfresco.brainware.types;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class DocPages {

    private Integer docPageCount;
    private List<DocPage> docPage;

    public Integer getDocPageCount() {
        return docPageCount;
    }

    @XmlAttribute(name = "DocPageCount")
    public void setDocPageCount(Integer docPageCount) {
        this.docPageCount = docPageCount;
    }

    public List<DocPage> getDocPage() {
        return docPage;
    }

    @XmlElement(name = "DocPage")
    public void setDocPage(List<DocPage> docPage) {
        this.docPage = docPage;
    }
}
