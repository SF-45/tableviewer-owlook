import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.wstableviewer.TableViewerProvider;

module space.sadfox.wstableviewer {
	
	exports space.sadfox.wstableviewer;
	
	requires transitive space.sadfox.owlook;
	requires space.sadfox.xmldataccess;
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	
	
	provides Module with TableViewerProvider;
	
	opens space.sadfox.wstableviewer to jakarta.xml.bind;
	opens space.sadfox.wstableviewer.ui.filter to javafx.fxml;
	opens space.sadfox.wstableviewer.ui.base to javafx.fxml;
	opens space.sadfox.wstableviewer.ui.view to javafx.fxml;
	
}