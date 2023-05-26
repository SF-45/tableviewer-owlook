package space.sadfox.tableviewer;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.EntityChangeListener;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;
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
	
	@FXML
    private Label searchHistory;

	public TableViewerController() throws IOException {
		super(TableViewer.class.getResource("fxml/main-scene.fxml"));

		getStage().setTitle("OwlookTV");

		MenuItem createTableViewer = new MenuItem("Create new");
		createTableViewer.setOnAction(event -> TableViewerDao.createTableViewer());
		tablesMenu.getItems().addAll(new SeparatorMenuItem(), createTableViewer);

		tableTabPane.getTabs().addListener((InvalidationListener) listner -> {
			if (tableTabPane.getTabs().size() == 0) {
				leftToolPane.setCenter(null);
				rightToolPane.setCenter(null);
				removeTVMenu();
			}
		});
		
		EntityLoader.INSTANCE.addCreateChangeListener(entity -> {
			if (entity.getClass().equals(TableViewer.class)) {
				createTableViewerMenuItem((TableViewer) entity);
			}
		});
		
		TableViewerDao.getTableViewers().forEach(this::createTableViewerMenuItem);
		
		
//		searchField.textProperty().addListener((property, oldValue, newValue) -> {
//			try {
//				getSelectedTableViewerTab().getTableDataViewTable().findAction(newValue);
//			} catch (Nullable e) {}
//		});
		
		searchField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case ENTER:
				try {
					getSelectedTableViewerTab().getTableDataViewTable().nextFind();
					searchField.setText("");
				} catch (Nullable e) {}
				break;
			default:
				break;
			}
		});
		
		searchButton.setOnAction(event -> {
			try {
				getSelectedTableViewerTab().getTableDataViewTable().nextFind();
				searchField.setText("");
			} catch (Nullable e) {}
		});
		
		
		
		
	}
	
	private TableViewerTab getSelectedTableViewerTab() throws Nullable {
		Tab tab =tableTabPane.getSelectionModel().getSelectedItem();
		if (tab instanceof TableViewerTab) {
			return (TableViewerTab) tab;
		}
		throw new Nullable();
	}

	private void createTableViewerMenuItem(TableViewer tableViewer) {
		MenuItem tableMenuItem = new MenuItem(tableViewer.getTitle());
		tableMenuItem.textProperty().bindBidirectional(tableViewer.titleProperty());
		tableMenuItem.setOnAction(event -> {
			TableViewerTab tableViewerTab = new TableViewerTab(tableViewer);
			
			tableViewer.addEntityChangeListener(change -> {
				if (change.wasRemoved()) {
					tableTabPane.getTabs().remove(tableViewerTab);
				}
			});

			tableViewerTab.setOnSelectionChanged(tabEvent -> {
				if (tableViewerTab.isSelected()) {
					leftToolPane.setCenter(tableViewerTab.getLeftToolsNode());
					rightToolPane.setCenter(tableViewerTab.getRightToolsNode());
					removeTVMenu();
					menuBar.getMenus().add(tableViewerTab.getMenu());
					searchHistory.textProperty().bind(tableViewerTab.getTableDataViewTable().searchTextHistoryProperty());
					searchField.setText(tableViewerTab.getTableDataViewTable().getCurrentSearchText());
					searchField.textProperty().bindBidirectional(tableViewerTab.getTableDataViewTable().currentSearchTextProperty());
				} else {
					searchField.textProperty().unbindBidirectional(tableViewerTab.getTableDataViewTable().currentSearchTextProperty());
				}
			});
			tableTabPane.getTabs().add(tableViewerTab);
			tableTabPane.getSelectionModel().select(tableViewerTab);
		});
		
		tableViewer.addEntityChangeListener(change -> {
			if (change.wasRemoved() && tablesMenu.getItems().contains(tableMenuItem)) {
				tablesMenu.getItems().remove(tableMenuItem);
			}
		});
		int pos = tablesMenu.getItems().size() - 2;
		tablesMenu.getItems().add(pos, tableMenuItem);
	}

	private void removeTVMenu() {
		menuBar.getMenus().remove(4, menuBar.getMenus().size());
	}

}
