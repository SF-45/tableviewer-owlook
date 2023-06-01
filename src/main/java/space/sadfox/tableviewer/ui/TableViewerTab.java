package space.sadfox.tableviewer.ui;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataController;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableViewForTableData;
import space.sadfox.owlook.jaxb.EntityChangeListener;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.TableViewerEditController;
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
		this.setContent(getTableDataViewTable());
		initializ();
	}

	private void initializ() {
		rightToolTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightToolTabPane.setSide(Side.TOP);
		
		EntityChangeListener<TableData.Change> tableDataChangeListener = change -> {
			if (change.wasDataUpdate()) {
				reloadCurrentData();
			}
		};
		
		getTableViewer().addEntityChangeListener((EntityChangeListener<TableViewer.Change>)change -> {
			if (change.wasTableDataChange()) {
				try {
					change.getOldTableData().removeEntityChangeListener(tableDataChangeListener);
				} catch (Nullable e) {}
				change.getNewTableData().addEntityChangeListener(tableDataChangeListener);
				reloadCurrentData();
			}
		});
		
		try {
			getTableViewerDao().getTableData();
		} catch (Nullable e) {

		}

		if (filtersTab.getSelectedTableDataFilter() != null) {
			TableDataFilter selectFilter = filtersTab.getSelectedTableDataFilter();
			this.textProperty()
					.bind(Bindings.concat(tableViewer.titleProperty(), "[", selectFilter.titleProperty(), "]"));
		} else {
			this.textProperty().bind(tableViewer.titleProperty());
		}
		filtersTab.selectedTableDataFilterProperty().addListener((property, oldValue, newValue) -> {
			this.textProperty().unbind();
			if (newValue != null) {
				this.textProperty()
						.bind(Bindings.concat(tableViewer.titleProperty(), "[", newValue.titleProperty(), "]"));
			} else {

				this.textProperty().bind(tableViewer.titleProperty());
			}
		});
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
			menu.textProperty().bindBidirectional(getTableViewer().titleProperty());

			Menu tableDataMenu = new Menu("Data");
			menu.getItems().add(tableDataMenu);

			MenuItem editTableData = new MenuItem("Edit Table Data");
			editTableData.setOnAction(event -> {
				try {
					new TableDataController(getTableViewerDao().getTableData()).show();
				} catch (IOException e) {
					ErrorLogger.registerException(e);
				} catch (Nullable e) {
				}
			});
			tableDataMenu.getItems().add(editTableData);

			MenuItem reloadData = new MenuItem("Reload");
			reloadData.setOnAction(event -> {
				try {
					getTableViewerDao().getTableDataDao().loadData();
				} catch (Nullable e) {
				}
			});
			tableDataMenu.getItems().add(reloadData);

			MenuItem editTableViewer = new MenuItem("Properties");
			editTableViewer.setOnAction(event -> {
				try {
					new TableViewerEditController(getTableViewer()).show();
				} catch (IOException e) {
					ErrorLogger.registerException(e);
				}
			});
			menu.getItems().add(editTableViewer);
		}
		return menu;
	}
	
	public void reloadCurrentData() {
		filtersTab.reloadData();
	}

}
