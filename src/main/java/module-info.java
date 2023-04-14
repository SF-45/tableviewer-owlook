import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.owlook.moduleapi.Workspace;
import space.sadfox.tableviewer.TableViewerProvider;

module space.sadfox.tableviewer {
	
	exports space.sadfox.tableviewer;
	
	requires transitive space.sadfox.owlook;
	requires space.sadfox.dataccess;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	
	
	provides Module with TableViewerProvider;
	provides Workspace with TableViewerProvider;
	
	opens space.sadfox.tableviewer to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.tableviewer.ui.filter to javafx.fxml;
	opens space.sadfox.tableviewer.ui.base to javafx.fxml;
	opens space.sadfox.tableviewer.ui.view to javafx.fxml;
	
}