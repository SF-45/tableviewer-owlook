import space.sadfox.owlook.moduleapi.OwlookModule;
import space.sadfox.owlook.moduleapi.Workspace;
import space.sadfox.tableviewer.TableViewerProvider;

module space.sadfox.tableviewer {
	
	exports space.sadfox.tableviewer;
	exports space.sadfox.tableviewer.ui.base to org.glassfish.jaxb.core;
	
	requires transitive space.sadfox.owlook;
	requires transitive space.sadfox.dataccess;
//	requires javafx.base;
//	requires javafx.controls;
//	requires javafx.graphics;
//	requires javafx.fxml;
	
	
	provides OwlookModule with TableViewerProvider;
	provides Workspace with TableViewerProvider;
	
	opens space.sadfox.tableviewer to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.tableviewer.ui.filter to javafx.fxml;
	opens space.sadfox.tableviewer.ui.base to javafx.fxml;
	opens space.sadfox.tableviewer.ui.view to javafx.fxml;
	opens space.sadfox.tableviewer.ui.action to javafx.fxml;
	
}