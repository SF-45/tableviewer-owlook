import space.sadfox.owlook.api.Workspace;
import space.sadfox.owlook.base.moduleapi.OwlookModule;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerProvider;

module tableviewer {

  exports space.sadfox.tableviewer;
  exports space.sadfox.tableviewer.ui.base to org.glassfish.jaxb.core;

  requires transitive space.sadfox.owlook;
  requires transitive dataccess;
  // requires javafx.base;
  // requires javafx.controls;
  // requires javafx.graphics;
  // requires javafx.fxml;


  provides OwlookModule with TableViewerProvider;
  provides Workspace with TableViewerProvider;
  provides OwlEntity with TableViewer;

  opens space.sadfox.tableviewer to jakarta.xml.bind, javafx.fxml;
  opens space.sadfox.tableviewer.ui.filter to javafx.fxml;
  opens space.sadfox.tableviewer.ui.base to javafx.fxml;
  opens space.sadfox.tableviewer.ui.view to javafx.fxml;
  opens space.sadfox.tableviewer.ui.action to javafx.fxml;

}

