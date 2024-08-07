package space.sadfox.tableviewer;

import java.io.IOException;
import space.sadfox.owlook.api.Workspace;
import space.sadfox.owlook.base.jaxb.ObservedJAXBEntity;
import space.sadfox.owlook.base.moduleapi.ModuleHasNoConfiguration;
import space.sadfox.owlook.base.moduleapi.OwlookModule;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Owlook;

public class TableViewerProvider implements OwlookModule, Workspace {

  private TableViewerController ui;

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
  public Controller getController() {
    if (ui == null) {
      try {
        ui = new TableViewerController();
      } catch (IOException e) {
        Owlook.registerException(e);
      }
    }

    return ui;
  }

  // @Override
  // public List<Class<? extends JAXBEntity>> getOwlEntities() {
  // return Arrays.asList(TableViewer.class);
  // }

  @Override
  public void initModule() {

  }

  // @Override
  // public Class<? extends JAXBEntity> getConfigTarget() throws ModuleHasNoConfiguration {
  // throw new ModuleHasNoConfiguration();
  // }

  @Override
  public String getWorkspaceName() {
    return "Table Viewer";
  }

  @Override
  public String getWorkspaceDescriprion() {
    return "Displaying tabular data and performing actions with them";
  }

  @Override
  public Class<? extends ObservedJAXBEntity> getConfigTarget() throws ModuleHasNoConfiguration {
    throw new ModuleHasNoConfiguration();
  }



}
