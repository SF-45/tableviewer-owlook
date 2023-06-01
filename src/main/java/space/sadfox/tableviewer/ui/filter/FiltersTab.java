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
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilterController;
import space.sadfox.dataccess.filter.TableDataFilterDao;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.tools.EntityManager;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;
import space.sadfox.tableviewer.ui.base.FilterToggleButton;

public class FiltersTab extends Tab {

	private ToggleGroup toggleGroup;
	private TableViewerTab tableViewerTab;
	private ButtonList buttonList;
	
	private ObjectProperty<TableDataFilter> selectedTableDataFilter;

	public FiltersTab(TableViewerTab tableViewerTab) {
		super("Filters");
		
		
		this.tableViewerTab = tableViewerTab;
		toggleGroup = new ToggleGroup();
		
		getTableViewerTab().getTableViewerDao().getFilters().forEach(this::addFilter);
		getTableViewerTab().getTableViewer().tableDataFiltersProperty().addListener((ListChangeListener<String>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(filterFileName -> {
						int ind = getTableViewerTab().getTableViewer().getTableDataFilters().indexOf(filterFileName);
						try {
							TableDataFilter newTableDataFilter = TableDataFilterDao.loadTableDataFilter(filterFileName);
							if (newTableDataFilter != null) {
								addFilter(ind, newTableDataFilter);
							}
						} catch (IOException | JAXBException e) {
							ErrorLogger.registerException(e);
						}
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
			TableDataFilter newFilter = TableDataFilterDao.createTableDataFilter();
			if (newFilter == null) return;
			newFilter.setTitle("New Filter");
			getTableViewerTab().getTableViewerDao().addFilter(newFilter);
			editFilter(newFilter);
		});
		contextMenu.getItems().add(createFilter);

		MenuItem open = new MenuItem("Open Filter");
		open.setOnAction(event -> {
			try {
				OpenEntityDialog<TableDataFilter> openDialog = new OpenEntityDialog<>(
						TableDataFilter.class,
						SelectionMode.MULTIPLE,
						getTableViewerTab().getTableViewerDao().getFilters()
						);
				
				openDialog.setModality(Modality.APPLICATION_MODAL);
				openDialog.showAndWait();
				if (openDialog.isOpened()) {
					openDialog.getOpenned().forEach(f -> getTableViewerTab().getTableViewerDao().addFilter(f));
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
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
				getTableViewerTab().getTableDataViewTable().setItems(
						FXCollections.observableArrayList(getTableViewerTab().getTableViewerDao().getFilterDao(filter).getDataEntities()));
				
			} catch (JAXBException e) {
				ErrorLogger.registerException(e);
			} catch (Nullable e) {}
			setSelectedTableDataFilter(filter);
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
		
		MenuItem duplicate = new MenuItem("Duplicate Filter");
		duplicate.setOnAction(event -> {
			try {
				TableDataFilter newFilter = EntityLoader.INSTANCE.duplicateEntity(filter);
				getTableViewerTab().getTableViewerDao().addFilter(newFilter);
				editFilter(newFilter);
			} catch (JAXBException | IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(duplicate);

		MenuItem close = new MenuItem("Close Filter");
		close.setOnAction(event -> {
			getTableViewerTab().getTableViewerDao().removeFilter(filter);
		});
		contextMenu.getItems().add(close);

		MenuItem delete = new MenuItem("Delete Filter");
		delete.setOnAction(event -> {
			if (TableDataFilterDao.deleteTableDataFiter(filter)) {
				getTableViewerTab().getTableViewerDao().removeFilter(filter);
			}
		});
		contextMenu.getItems().add(delete);

		if (ind < 0)
			getButtonList().getChildren().add(button);
		else
			getButtonList().getChildren().add(ind, button);
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

	private ButtonList getButtonList() {
		if (buttonList == null) {
			buttonList = new ButtonList();
			this.setContent(buttonList);
		}
		return buttonList;
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
			tableDataFilter.getConfigController(getTableViewerTab().getTableViewerDao().getTableData()).show();
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		} catch (Nullable e) {
			try {
				tableDataFilter.getConfigController().show();
			} catch (IOException e1) {
				ErrorLogger.registerException(e1);
			}
		}
	}

}
