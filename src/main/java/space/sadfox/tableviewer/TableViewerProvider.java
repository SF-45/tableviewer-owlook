package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.ModuleHasNoConfiguration;
import space.sadfox.owlook.moduleapi.OwlookModule;
import space.sadfox.owlook.moduleapi.Workspace;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;

public class TableViewerProvider implements OwlookModule, Workspace {
	
	private TableViewerController ui;

	@Override
	public String getShortModuleDescription() {
		return "Displaying tabular data and performing actions with them";
	}

	@Override
	public String getModuleDescription() {
		return "Displaying tabular data and performing actions with them";
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
