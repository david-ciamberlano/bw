package com.alfresco.brainware;

import com.alfresco.brainware.domain.Mapping;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;


public class ImportInvoiceFromBW extends ActionExecuterAbstractBase {

    private static Log logger = LogFactory.getLog(ImportInvoiceFromBW.class);

    private final String BW_QNAME = "http://www.alfresco.com/model/brainware/1.0";
    private final NodeRef xmlMappingNodeRef = new NodeRef("workspace://SpacesStore/89661fdc-7fe3-4c0b-a6cf-7bd6600bb920");

    private FileFolderService fileFolderService;
    private MimetypeService mimetypeService;
    private NodeService nodeService;



    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
    }

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

            FileInfo currentDocument = fileFolderService.getFileInfo(actionedUponNodeRef);
            FileInfo xmlMapping = fileFolderService.getFileInfo(xmlMappingNodeRef);

            // get all the xml documents
            if (mimetypeService.guessMimetype(currentDocument.getName()).equals("text/xml")) {

                // get Properties from xml
                Map<String,String> xmlDocumentProperties = extractProperties(currentDocument);

                Map<String, Mapping> propertiesMapping = getMapping(xmlMapping);

                NodeRef currentNodeRef = new NodeRef("workspace://SpacesStore/"+xmlDocumentProperties.get("uid"));

                logger.info("-----------------");
                // find all the properties
                Map<QName, Serializable> properties = nodeService.getProperties(currentNodeRef);
                xmlDocumentProperties.forEach( (k,v) -> {

                    if(!k.equals("DocumentType") && !k.equals("uid")) {

                        Mapping mapping = propertiesMapping.get(k);
                        if (mapping != null) {
                            switch (mapping.getType()) {

                                case "currency":
                                    properties.put(mapping.getMapTo(), formatCurrency(v));
                                    break;

                                case "date":
                                    properties.put(mapping.getMapTo(), formatDate(v));
                                    break;

                                default:
                                    properties.put(mapping.getMapTo(), v);
                            }
                        }
                    }

                });

                QName typeQName = QName.createQName(BW_QNAME, xmlDocumentProperties.get("DocumentType"));
                nodeService.setType(currentNodeRef, typeQName);

                nodeService.setProperties(currentNodeRef, properties);

                try {
                    // delete the xml file
                    fileFolderService.delete(currentDocument.getNodeRef());
                }
                catch (Exception e) {
                    logger.error("Document not found: " + currentDocument.getName(), e);
                }


        }

    }

    private BigDecimal formatCurrency(String v) {
        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        ((DecimalFormat) format).setParseBigDecimal(true);
        try {
            return (BigDecimal)format.parse(v);
        }
        catch (ParseException e) {
            logger.error("Cannot Parse Currency: " + v, e);
            return new BigDecimal(0);
        }
    }

    private Date formatDate(String date) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US);
        try {
            return dateFormat.parse(date);
        }
        catch (ParseException e) {
            logger.error("Cannot Parse Currency: " + date, e);
            return null;
        }
    }

    private Map<String,String> extractProperties(FileInfo fileInfo) {

        Map<String,String> xmlprops = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(fileFolderService
                    .getReader(fileInfo.getNodeRef()).getContentInputStream());

            NodeList urn = document.getElementsByTagName("URN");
            String uuid = urn.item(0).getTextContent();

            //extract document uuid from name
            xmlprops.put("uid", uuid);

            NodeList invHeader = document.getElementsByTagName("InvHeader");
            NodeList properties = invHeader.item(0).getChildNodes();
            for (int i=0; i < properties.getLength(); i++) {
                if (properties.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    xmlprops.put(properties.item(i).getNodeName(), properties.item(i).getTextContent());
                }
            }



        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Conversion error:", e);
        }

        return xmlprops;
    };


    private Map<String, Mapping> getMapping(FileInfo xmlMapping) {

        Map<String, Mapping> mapping = new HashMap<>();


        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(fileFolderService
                    .getReader(xmlMapping.getNodeRef()).getContentInputStream());

            NodeList nameSpaceEl = document.getElementsByTagName("typeNamespace");
            String nameSpace = nameSpaceEl.item(0).getTextContent();

            NodeList keywords = document.getElementsByTagName("Keyword");

            for (int i=0; i<keywords.getLength(); i++) {
                Node keyword = keywords.item(i);
                if (keyword.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element)keyword;
                    String name = element.getElementsByTagName("name").item(0).getTextContent();
                    String mapTo = element.getElementsByTagName("mapTo").item(0).getTextContent();
                    String type = element.getElementsByTagName("KeywordType").item(0).getTextContent();

                    QName mapQName = QName.createQName(nameSpace,mapTo);
                    mapping.put(name, new Mapping(mapQName,type));
                }
            }
        }
        catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("Conversion error:", e);
        }

        return mapping;
    }


    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public MimetypeService getMimetypeService() {
        return mimetypeService;
    }

    public void setMimetypeService(MimetypeService mimetypeService) {
        this.mimetypeService = mimetypeService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }
}
