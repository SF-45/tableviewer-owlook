package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.owlook.moduleapi.ModuleHasNoConfiguration;
import space.sadfox.owlook.moduleapi.Workspace;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;

public class TableViewerProvider implements Module, Workspace {
	
	private TableViewerController ui;

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return "table-viewer";
	}

	@Override
	public String getModuleDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleVersion() {
		// TODO Auto-generated method stub
		return "0.01";
	}

	@Override
	public String getWorkspaceName() {
		// TODO Auto-generated method stub
		return "Table Viever";
	}

	@Override
	public String getWorkspaceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Controller getController() {
		if (ui == null) {
			try {
				ui = new TableViewerController();
			} catch (IOException e) {
				OwlLogger.registerException(1, e);
			}
		}
		
		return ui;
	}

	@Override
	public List<Class<? extends JAXBEntity>> getJaxbEntities() throws Nullable {
		return Arrays.asList(TableViewer.class);
	}

	@Override
	public void initModule() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<? extends JAXBEntity> getConfigTarget() throws ModuleHasNoConfiguration {
		throw new ModuleHasNoConfiguration();
	}

	
	


}
