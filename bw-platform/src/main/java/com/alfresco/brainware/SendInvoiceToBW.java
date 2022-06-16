package com.alfresco.brainware;

import org.alfresco.repo.action.executer.ActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public class SendInvoiceToBW extends ActionExecuterAbstractBase {

    private static Log logger = LogFactory.getLog(SendInvoiceToBW.class);
    private FileFolderService fileFolderService;
    private NodeService nodeService;
    private String exportFolder;

//    private final NodeRef exportFolder = new NodeRef("workspace://SpacesStore/b2798654-3cd2-44d8-bd2e-9996410a274a");

    @Override
    protected void executeImpl(Action action, NodeRef nodeRef) {

        FileInfo currentDocument = fileFolderService.getFileInfo(nodeRef);

        NodeRef exportFolderNodeRef = new NodeRef(exportFolder);
        try {
            String currentName = currentDocument.getName();
            int extensionPos = currentName.lastIndexOf('.');
            String extension = currentName.substring(extensionPos);
            fileFolderService.copy(currentDocument.getNodeRef(), exportFolderNodeRef, nodeRef.getId()+extension);

        } catch (FileNotFoundException e) {
            logger.error("Document not found: " + currentDocument.getName(), e);
        }

    }

    @Override
    protected void addParameterDefinitions(List<ParameterDefinition> list) {

    }

    public FileFolderService getFileFolderService() {
        return fileFolderService;
    }

    public void setFileFolderService(FileFolderService fileFolderService) {
        this.fileFolderService = fileFolderService;
    }

    public NodeService getNodeService() {
        return nodeService;
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    public void setExportFolder(String exportFolder) {
        this.exportFolder = exportFolder;
    }
}
