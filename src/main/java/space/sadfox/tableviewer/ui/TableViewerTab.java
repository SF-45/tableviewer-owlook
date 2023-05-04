package space.sadfox.tableviewer.ui;

import java.io.IOException;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import space.sadfox.dataccess.dataccess.TableDataController;
import space.sadfox.dataccess.view.TableViewForTableData;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.action.ActionController;
import space.sadfox.tableviewer.ui.filter.FiltersTab;
import space.sadfox.tableviewer.ui.view.ViewsTab;

public class TableViewerTab extends Tab {
	
	private TableViewer tableViewer;
	private TableViewerDao tableViewerDao;

	private ActionController leftToolPane;
	private TabPane rightToolTabPane;
	private Menu menu;
	
	private FiltersTab filtersTab;
	
	private TableViewForTableData tableDataViewTable;


	public TableViewerTab(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		
		filtersTab = new FiltersTab(this);
		
		rightToolTabPane = new TabPane();
		rightToolTabPane.getTabs().add(new ViewsTab(this));
		rightToolTabPane.getTabs().add(filtersTab);
		try {
			leftToolPane = new ActionController(this);
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
		this.setContent(tableDataViewTable);
		initializ();
	}

	private void initializ() {
		rightToolTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightToolTabPane.setSide(Side.TOP);
		this.setText(getTableViewer().getTitle());
		
		
	}
	
	public TableViewForTableData getTableDataViewTable() {
		if (tableDataViewTable == null) {
			tableDataViewTable = new TableViewForTableData();
		}
		return tableDataViewTable;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	public TableViewerDao getTableViewerDao() {
		if (tableViewerDao == null) {
			tableViewerDao = new TableViewerDao(getTableViewer());
		}
		
		return tableViewerDao;
	}

	public Node getRightToolsNode() {
		return rightToolTabPane;
	}

	public Node getLeftToolsNode() {
		return leftToolPane.getParent();
	}
	
	public Menu getMenu() {
		if (menu == null) {
			menu = new Menu(getTableViewer().getTitle());
			
			Menu tableDataMenu = new Menu("Data");
			menu.getItems().add(tableDataMenu);
			
			MenuItem editTableData = new MenuItem("Edit Table Data");
			editTableData.setOnAction(event -> {
				try {
					new TableDataController(getTableViewerDao().getTableData()).show();
				} catch (IOException e) {
					ErrorLogger.registerException(e);
				}
			});
			tableDataMenu.getItems().add(editTableData);
			
			MenuItem reloadData = new MenuItem("Reload");
			reloadData.setOnAction(event -> {
				getTableViewerDao().getTableDataDao().loadData();
				filtersTab.reloadData();
			});
			tableDataMenu.getItems().add(reloadData);
		}
		return menu;
	}
	
	

}
