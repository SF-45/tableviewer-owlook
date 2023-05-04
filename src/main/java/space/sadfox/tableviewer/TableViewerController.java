package space.sadfox.tableviewer;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class TableViewerController extends Controller {
	
    @FXML
    private BorderPane leftToolPane;

    @FXML
    private MenuItem openIndex;

    @FXML
    private BorderPane rightToolPane;

    @FXML
    private Button searchButton;

    @FXML
    private TextField searchField;

    @FXML
    private MenuItem settings;

    @FXML
    private TabPane tableTabPane;
    
    @FXML
    private Menu tablesMenu;
    
    @FXML
    private MenuBar menuBar;

	public TableViewerController() throws IOException {
		super(TableViewer.class.getResource("fxml/main-scene.fxml"));
		
		getStage().setTitle("OwlookTV");
		
		
		
		EntityLoader loader = new EntityLoader();
		for (TableViewer tableViewer : loader.loadAllEntities(TableViewer.class)) {
			
			MenuItem tableMenuItem = new MenuItem(tableViewer.getTitle());
			tableMenuItem.setOnAction(event -> {
				TableViewerTab tableViewerTab = new TableViewerTab(tableViewer);
				
				tableViewerTab.setOnSelectionChanged(tabEvent -> {
					if (tableViewerTab.isSelected()) {
						leftToolPane.setCenter(tableViewerTab.getLeftToolsNode());
						rightToolPane.setCenter(tableViewerTab.getRightToolsNode());
						removeTVMenu();
						menuBar.getMenus().add(tableViewerTab.getMenu());
					}
				});
				tableTabPane.getTabs().add(tableViewerTab);
				tableTabPane.getSelectionModel().select(tableViewerTab);
			});
			tablesMenu.getItems().add(tableMenuItem);
			
			
		}
		tableTabPane.getTabs().addListener((InvalidationListener) listner -> {
			if (tableTabPane.getTabs().size() == 0) {
				leftToolPane.setCenter(null);
				rightToolPane.setCenter(null);
				removeTVMenu();
			}
		});
	}
	
	private void removeTVMenu() {
		menuBar.getMenus().remove(4, menuBar.getMenus().size());
	}

}
