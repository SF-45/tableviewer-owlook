package space.sadfox.tableviewer.ui.filter;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilterController;
import space.sadfox.dataccess.filter.TableDataFilterDao;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.FileNamePicker;
import space.sadfox.tableviewer.ui.base.FilterToggleButton;
import space.sadfox.tableviewer.ui.base.ToolTabBase;

public class FiltersTab extends ToolTabBase {
	
	private ToggleGroup toggleGroup;


	public FiltersTab(TableViewerTab tableViewerTab) {
		super(tableViewerTab, "Filters");
		toggleGroup = new ToggleGroup();
		getTableViewerDao().getFilters().forEach(this::addFilter);
		getTableViewer().tableDataFiltersProperty().addListener((ListChangeListener<String>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(filterFileName -> {
						int ind = getTableViewer().getTableDataFilters().indexOf(filterFileName);
						addFilter(ind, getTableViewerDao().getFilter(filterFileName));
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(filterFileName -> {
						deleteFilter(filterFileName);
					});
				}
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		getButtonList().setContextMenu(contextMenu);
		this.tabPaneProperty().addListener((property, oldValue, newValue) -> {
			getButtonList().setContextMenuHideProperty(newValue.focusedProperty());
		});
		
		MenuItem createFilter = new MenuItem("Create Filter");
		createFilter.setOnAction(event -> {
			try {
				
				EntityLoader loader = new EntityLoader();
				
				try {
					TableDataFilter newFilter = loader.createEntity(TableDataFilter.class);
					newFilter.setTitle("New Filter");
					getTableViewer().getTableDataFilters().add(newFilter.getFileName());
					editFilter(newFilter);
				} catch (JAXBException e) {
					ErrorLogger.registerException(e);
				}
				
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(createFilter);
		
		MenuItem open = new MenuItem("Open Filter");
		open.setOnAction(event -> {
			try {
				OpenEntityDialog<TableDataFilter> openDialog = new OpenEntityDialog<>(TableDataFilter.class, getTableViewerDao().getFilters());
				openDialog.setModality(Modality.APPLICATION_MODAL);
				openDialog.showAndWait();
				if (openDialog.isOpened()) {
					getTableViewer().getTableDataFilters().add(openDialog.getOpenned().getFileName());
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(open);

	}
	
	private void addFilter(int ind, TableDataFilter filter) {
		if (filter == null) return;
		FilterToggleButton button = new FilterToggleButton(filter, getTableViewer());
		button.setToggleGroup(toggleGroup);
		button.setOnAction(event -> {
			try {
				getTableViewerTab().getTableDataViewTable()
						.setItems(FXCollections.observableArrayList(getTableViewerDao().getFilterDao(filter).getDataEntities()));
			} catch (JAXBException e) {
				ErrorLogger.registerException(e);
			}
		});
		if (getButtonList().getChildren().size() == 0) {
			button.fire();
		}
		
		ContextMenu contextMenu = new ContextMenu();
		button.setContextMenu(contextMenu);
		
		MenuItem edit = new MenuItem("Edit Filter");
		edit.setOnAction(event -> {
			editFilter(filter);
		});
		contextMenu.getItems().add(edit);
		
		MenuItem close = new MenuItem("Close Filter");
		close.setOnAction(event -> {
			getTableViewer().getTableDataFilters().remove(filter.getFileName());
		});
		contextMenu.getItems().add(close);
		
		MenuItem delete = new MenuItem("Delete Filter");
		delete.setOnAction(event -> {
			EntityLoader loader = new EntityLoader();
			if (loader.deleteEntity(filter)) {
				getTableViewer().getTableDataFilters().remove(filter.getFileName());
			}
		});
		contextMenu.getItems().add(delete);
		
		
		
		if (ind < 0) getButtonList().getChildren().add(button);
		else getButtonList().getChildren().add(ind, button);
	}
	private void addFilter(TableDataFilter filter) {
		addFilter(-1, filter);
	}
	
	private void deleteFilter(String filterFileName) {
		var btnList = getButtonList().getChildren();
		for (int i = 0; i < btnList.size(); i++) {
			Node node = btnList.get(i);
			if (node instanceof FilterToggleButton) {
				FilterToggleButton filterButton = (FilterToggleButton) node;
				if (filterButton.getFilter().getFileName().equals(filterFileName)) {
					btnList.remove(i);
					return;
				}
			}
		}
	}
	
	private void editFilter(TableDataFilter filter) {
		try {
			var controller = new TableDataFilterController(filter, getTableViewerDao().getTableData());
			controller.show();
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
	}
	
	public void reloadData() {
		var toggle = toggleGroup.getSelectedToggle();
		if (toggle instanceof FilterToggleButton) {
			FilterToggleButton button = (FilterToggleButton) toggle;
			button.fire();
		}
	}
	

}
