package space.sadfox.tableviewer.ui;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.dataccess.view.TableViewForTableData;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.filter.FiltersTab;
import space.sadfox.tableviewer.ui.view.ViewsTab;

public class TableViewerTab extends Tab {
	
	private TableViewer tableViewer;
	private TableViewerDao tableViewerDao;

	private CommandNode leftToolPane;
	private TabPane rightToolTabPane;
	private TableViewForTableData tableDataViewTable;


	public TableViewerTab(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		
		rightToolTabPane = new TabPane();
		rightToolTabPane.getTabs().add(new ViewsTab(this));
		rightToolTabPane.getTabs().add(new FiltersTab(this));
		leftToolPane = new CommandNode(this);
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
		return leftToolPane;
	}
	
	

}
