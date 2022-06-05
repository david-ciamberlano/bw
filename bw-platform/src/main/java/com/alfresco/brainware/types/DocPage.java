package com.alfresco.brainware.types;

import javax.xml.bind.annotation.XmlAttribute;

public class DocPage {

    private String ImportedFileName;

    public String getImportedFileName() {
        return ImportedFileName;
    }

    @XmlAttribute(name = "ImportedFileName")
    public void setImportedFileName(String importedFileName) {
        ImportedFileName = importedFileName;
    }
}
