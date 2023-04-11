package space.sadfox.wstableviewer;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.owlook.moduleapi.WorkspaceAPI;
import space.sadfox.owlook.moduleapi.WorkspaceUI;
import space.sadfox.owlook.utils.ErrorLogger;

public class TableViewerProvider extends Module implements WorkspaceAPI {

	@Override
	public String getName() {
		return "TableViewer";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getVersion() {
		return "0.0.1";
	}

	@Override
	public WorkspaceUI getWorkspaceUI(JAXBEntity entity) throws JAXBException {
		if (entity instanceof TableViewer) {
			try {
				return new TableViewerUI((TableViewer) entity);
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		}
		throw new JAXBException("Entity does not belong to this module: " + entity.getPath());
	}

	@Override
	public Class<? extends JAXBEntity> getEntityClass() {
		return TableViewer.class;
	}



}
