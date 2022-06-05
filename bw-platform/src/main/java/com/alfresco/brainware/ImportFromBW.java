package com.alfresco.brainware;

import com.alfresco.brainware.types.DocumentType;
import org.alfresco.repo.action.ParameterDefinitionImpl;
import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ImportFromBW extends ActionExecuterAbstractBase {

    private static Log logger = LogFactory.getLog(ImportFromBW.class);
    public static final String PARAM_DEST_FOLDER = "destination-folder";
    public static final String PARAM_SOURCE_FOLDER = "source-folder";
    private final String BW_QNAME = "http://www.alfresco.com/model/brainware/1.0";

    /**
     * The Alfresco Service Registry that gives access to all public content services in Alfresco.
     */
    private Unmarshaller jaxbUnmarshaller;
    private FileFolderService fileFolderService;
    private MimetypeService mimetypeService;
    private NodeService nodeService;

    public void setServiceRegistry(ServiceRegistry serviceRegistry) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(DocumentType.class);
        jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        fileFolderService = serviceRegistry.getFileFolderService();
        mimetypeService = serviceRegistry.getMimetypeService();
        nodeService = serviceRegistry.getNodeService();
//        serviceRegistry.getSearchService().

    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> paramList) {
        for (String s : new String[]{PARAM_SOURCE_FOLDER, PARAM_DEST_FOLDER}) {
            paramList.add(
                new ParameterDefinitionImpl(s, DataTypeDefinition.NODE_REF, true, getParamDisplayLabel(s))
            );
        }
    }

    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef) {

        NodeRef destinationParent = (NodeRef)action.getParameterValue(PARAM_DEST_FOLDER);
        NodeRef sourceParent = (NodeRef)action.getParameterValue(PARAM_SOURCE_FOLDER);

        if (fileFolderService.exists(sourceParent)) {
            List<FileInfo> files = fileFolderService.listFiles(sourceParent);

            files.forEach(f -> logger.info(f.getName() + " - " + f.getType()) );
            List<DocumentType> xmlDocuments =
                    files.stream().filter(isXML).map(extractProperties).collect(Collectors.toList());

            Map<String,NodeRef> documents =
                    files.stream().filter(isNotXML).collect(Collectors.toMap(FileInfo::getName,FileInfo::getNodeRef));

//            xmlDocuments.forEach(d -> {
//                logger.info(d.getInvHeader().toString());
//                logger.info(documents.get(d.getDocPages().getDocPage().get(0).getImportedFileName()));
//            });

            xmlDocuments.forEach( d -> {
                NodeRef currentNodeRef = documents.get(d.getDocPages().getDocPage().get(0).getImportedFileName());
                QName typeQName = QName.createQName(BW_QNAME, d.getInvHeader().getDocumentType());
                QName billToNameQName = QName.createQName(BW_QNAME,"BillToName");
                QName currencyQName = QName.createQName(BW_QNAME,"currency");
                QName invDateQName = QName.createQName(BW_QNAME,"InvDate");
                QName TotalQName = QName.createQName(BW_QNAME,"Total");

                nodeService.setType(currentNodeRef, typeQName);
                nodeService.setProperty(currentNodeRef, billToNameQName, d.getInvHeader().getBillToName());
                nodeService.setProperty(currentNodeRef, currencyQName, d.getInvHeader().getCurrency());
                nodeService.setProperty(currentNodeRef, invDateQName, d.getInvHeader().getInvDate());
                nodeService.setProperty(currentNodeRef, TotalQName, d.getInvHeader().getTotal());

            });


        }

    }

    private Predicate<FileInfo> isXML = f -> mimetypeService.guessMimetype(f.getName()).equals("text/xml");

    private Predicate<FileInfo> isNotXML = f -> !mimetypeService.guessMimetype(f.getName()).equals("text/xml");

    private Function<FileInfo, DocumentType> extractProperties = f -> {

        try {
            DocumentType documentType = (DocumentType) jaxbUnmarshaller.unmarshal(
                    fileFolderService.getReader(f.getNodeRef()).getContentInputStream());

            return documentType;
        }
        catch (JAXBException e) {
            logger.error("Conversion error:", e);
            return new DocumentType();
        }
    };

//    private Function<FileInfo, Map<String,NodeRef>> getNodes = f -> {
//
//    }

//    private Consumer<DocumentType> moveDocs = d -> {
//
//        String fileName = d.getDocPages().getDocPage().get(0).getImportedFileName();
//
//
//
//    }

}
