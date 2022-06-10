package com.alfresco.brainware.domain;

import org.alfresco.service.namespace.QName;

public class Mapping {

    QName mapTo;
    String type;

    public Mapping(QName mapTo, String type) {
        this.mapTo = mapTo;
        this.type = type;
    }

    public QName getMapTo() {
        return mapTo;
    }

    public String getType() {
        return type;
    }


}
