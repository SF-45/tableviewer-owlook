package space.sadfox.tableviewer.ui;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataController;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableViewForTableData;
import space.sadfox.owlook.base.jaxb.EntityChangeListener;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerEditController;
import space.sadfox.tableviewer.ui.action.ActionController;
import space.sadfox.tableviewer.ui.filter.FiltersTab;
import space.sadfox.tableviewer.ui.view.ViewsTab;

public class TableViewerTab extends Tab {

	private TableViewer tableViewer;

	private ActionController actionsNode;
	private ViewsTab viewsNode;
	private FiltersTab filtersNode;

	private Menu menu;

	private TableViewForTableData tableDataViewTable;

	public TableViewerTab(TableViewer tableViewer) {
		this.tableViewer = tableViewer;

		filtersNode = new FiltersTab(this);
		viewsNode = new ViewsTab(this);

		try {
			actionsNode = new ActionController(this);
		} catch (IOException e) {
			OwlLogger.registerException(1, e);
		}
		this.setContent(getTableDataViewTable());
		initializ();
	}

	private void initializ() {

		EntityChangeListener tableDataChangeListener = change -> {
			if (change instanceof TableData.Change) {
				TableData.Change tdchange = (TableData.Change) change;
				if (tdchange.wasDataUpdate()) {
					reloadCurrentData();
				}
				
			}
		};
		
		try {
			getTableViewer().getTableDataSafe().addEntityChangeListener(tableDataChangeListener);
		} catch (Nullable e) {
		}
		
		getTableViewer().tableDataProperty().addListener((property, oldValue, newValue) -> {
			if (oldValue != null) {
				oldValue.removeEntityChangeListener(tableDataChangeListener);
			}
			if (newValue != null ) {
				newValue.addEntityChangeListener(tableDataChangeListener);
			}
			reloadCurrentData();
		});

		

		if (filtersNode.getSelectedTableDataFilter() != null) {
			TableDataFilter selectFilter = filtersNode.getSelectedTableDataFilter();
			this.textProperty()
					.bind(Bindings.concat(tableViewer.titleProperty(), "[", selectFilter.titleProperty(), "]"));
		} else {
			this.textProperty().bind(tableViewer.titleProperty());
		}
		filtersNode.selectedTableDataFilterProperty().addListener((property, oldValue, newValue) -> {
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
			tableDataViewTable.getFindActionDelay().delayProperty().bind(getTableViewer().searchDelayProperty());
		}
		return tableDataViewTable;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}
	
	public TableData getTableData( ) throws Nullable {
		return getTableViewer().getTableDataSafe();
	}

	public Node getViewsNode() {
		return viewsNode;
	}

	public Node getFiltersNode() {
		return filtersNode;
	}

	public Node getActionsNode() {
		return actionsNode.getParent();
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
					new TableDataController(getTableViewer().getTableDataSafe()).show();
				} catch (IOException e) {
					OwlLogger.registerException(1, e);
				} catch (Nullable e) {
					MessageBox messageBox = new MessageBox(AlertType.INFORMATION);
					messageBox.setTitle("Table Data Not Set");
					messageBox.setHeaderText("Table Data Not Set");
					messageBox.showAndWait();
				}
			});
			tableDataMenu.getItems().add(editTableData);

			MenuItem reloadData = new MenuItem("Reload");
			reloadData.setOnAction(event -> {
				try {
					new TableDataDao(getTableViewer().getTableDataSafe()).loadData();
				} catch (Nullable e) {
					MessageBox messageBox = new MessageBox(AlertType.INFORMATION);
					messageBox.setTitle("Table Data Not Set");
					messageBox.setHeaderText("Table Data Not Set");
					messageBox.showAndWait();
				}
			});
			tableDataMenu.getItems().add(reloadData);

			MenuItem editTableViewer = new MenuItem("Properties");
			editTableViewer.setOnAction(event -> {
				try {
					new TableViewerEditController(getTableViewer()).show();
				} catch (IOException e) {
					OwlLogger.registerException(1, e);
				}
			});
			menu.getItems().add(editTableViewer);
		}
		return menu;
	}

	public void reloadCurrentData() {
		filtersNode.reloadData();
	}

}
