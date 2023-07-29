package space.sadfox.tableviewer.ui.filter;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilters;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.EntityLoader;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;

public class FiltersTab extends ButtonList {

	private ToggleGroup toggleGroup;
	private TableViewerTab tableViewerTab;
	
	private ObjectProperty<TableDataFilter> selectedTableDataFilter;

	public FiltersTab(TableViewerTab tableViewerTab) {
		
		this.tableViewerTab = tableViewerTab;
		toggleGroup = new ToggleGroup();
		
		getTableViewerTab().getTableViewer().getTableDataFilters().forEach(this::addFilter);
		getTableViewerTab().getTableViewer().tableDataFiltersProperty().addListener((ListChangeListener<TableDataFilter>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(filter -> {
						int ind = getTableViewerTab().getTableViewer().getTableDataFilters().indexOf(filter);
						addFilter(ind, filter);
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(filter -> {
						deleteFilter(filter);
					});
				}
			}
		});

		ContextMenu contextMenu = new ContextMenu();
		setContextMenu(contextMenu);

		MenuItem createFilter = new MenuItem("Create Filter");
		createFilter.setOnAction(event -> {
			TableDataFilter newFilter = TableDataFilters.createTableDataFilter();
			if (newFilter == null) return;
			newFilter.setTitle("New Filter");
			getTableViewerTab().getTableViewer().getTableDataFilters().add(newFilter);
			editFilter(newFilter);
		});
		contextMenu.getItems().add(createFilter);

		MenuItem open = new MenuItem("Open Filter");
		open.setOnAction(event -> {
			try {
				OpenEntityDialog<TableDataFilter> openDialog = new OpenEntityDialog<>(
						TableDataFilter.class,
						SelectionMode.MULTIPLE,
						getTableViewerTab().getTableViewer().getTableDataFilters()
						);
				
				openDialog.setModality(Modality.APPLICATION_MODAL);
				openDialog.showAndWait();
				if (openDialog.isOpened()) {
					openDialog.getOpenned().forEach(f -> getTableViewerTab().getTableViewer().getTableDataFilters().add(f));
				}
			} catch (IOException e) {
				OwlLogger.registerException(1, e);
			}
		});
		contextMenu.getItems().add(open);

	}

	public void reloadData() {
		var toggle = toggleGroup.getSelectedToggle();
		if (toggle instanceof FilterToggleButton) {
			FilterToggleButton button = (FilterToggleButton) toggle;
			button.fire();
		}
	}
	
	

	public TableViewerTab getTableViewerTab() {
		return tableViewerTab;
	}

	private void addFilter(int ind, TableDataFilter filter) {
		FilterToggleButton button = new FilterToggleButton(filter, getTableViewerTab().getTableViewer());
		button.setToggleGroup(toggleGroup);
		button.setOnAction(event -> {
			try {
			DataEntity[] dataEntities = TableDataFilters.getDataEntities(filter, getTableViewerTab().getTableData());
			getTableViewerTab().getTableDataViewTable().setItems(FXCollections.observableArrayList(dataEntities));
			} catch (JAXBException e) {
				OwlLogger.registerException(1, e);
			} catch (Nullable e) {}
			setSelectedTableDataFilter(filter);
		});
		if (getChildren().size() == 0) {
			button.fire();
		}

		ContextMenu contextMenu = new ContextMenu();
		button.setContextMenu(contextMenu);

		MenuItem edit = new MenuItem("Edit Filter");
		edit.setOnAction(event -> {
			editFilter(filter);
		});
		contextMenu.getItems().add(edit);
		
		MenuItem duplicate = new MenuItem("Duplicate Filter");
		duplicate.setOnAction(event -> {
			try {
				TableDataFilter newFilter = EntityLoader.INSTANCE.duplicateEntity(filter);
				getTableViewerTab().getTableViewer().getTableDataFilters().add(filter);
				editFilter(newFilter);
			} catch (JAXBException | IOException e) {
				OwlLogger.registerException(1, e);
			}
		});
		contextMenu.getItems().add(duplicate);

		MenuItem close = new MenuItem("Close Filter");
		close.setOnAction(event -> {
			getTableViewerTab().getTableViewer().getTableDataFilters().remove(filter);
		});
		contextMenu.getItems().add(close);

		MenuItem delete = new MenuItem("Delete Filter");
		delete.setOnAction(event -> {
			if (TableDataFilters.deleteTableDataFiter(filter)) {
				getTableViewerTab().getTableViewer().getTableDataFilters().remove(filter);
			}
		});
		contextMenu.getItems().add(delete);

		if (ind < 0)
			getChildren().add(button);
		else
			getChildren().add(ind, button);
	}

	private void addFilter(TableDataFilter filter) {
		addFilter(-1, filter);
	}

	private void deleteFilter(TableDataFilter filter) {
		var btnList = getChildren();
		for (int i = 0; i < btnList.size(); i++) {
			Node node = btnList.get(i);
			if (node instanceof FilterToggleButton) {
				FilterToggleButton filterButton = (FilterToggleButton) node;
				if (filterButton.getFilter().equals(filter)) {
					btnList.remove(i);
					return;
				}
			}
		}
	}
	
	public TableDataFilter getSelectedTableDataFilter() {
		return selectedTableDataFilterProperty().get();
	}
	
	private void setSelectedTableDataFilter(TableDataFilter tableDataFilter) {
		writableSelectedTableDataFilterProperty().set(tableDataFilter);
	}
	
	private ObjectProperty<TableDataFilter> writableSelectedTableDataFilterProperty() {
		if (selectedTableDataFilter == null) {
			selectedTableDataFilter = new SimpleObjectProperty<>();
		}
		return selectedTableDataFilter;
	}
	
	public ReadOnlyObjectProperty<TableDataFilter> selectedTableDataFilterProperty() {
		return writableSelectedTableDataFilterProperty();
	}
	
	private void editFilter(TableDataFilter tableDataFilter) {
		try {
			tableDataFilter.getConfigController(getTableViewerTab().getTableViewer().getTableDataSafe()).show();
		} catch (IOException e) {
			OwlLogger.registerException(1, e);
		} catch (Nullable e) {
			try {
				tableDataFilter.getConfigController().show();
			} catch (IOException e1) {
				OwlLogger.registerException(1, e1);
			}
		}
	}

}
