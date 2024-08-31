package space.sadfox.tableviewer.ui.filter;

import java.io.IOException;
import java.util.Arrays;
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
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilters;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlLoader.DeleteFlag;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.owlery.OwleryOpenDialog;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;

public class FiltersTab extends ButtonList {

  private ToggleGroup toggleGroup;
  private TableViewerTab tableViewerTab;

  private ObjectProperty<Owl<TableDataFilter>> selectedTableDataFilter;

  public FiltersTab(TableViewerTab tableViewerTab) {

    this.tableViewerTab = tableViewerTab;
    toggleGroup = new ToggleGroup();

    getTableViewerTab().getTableViewer().entity().getTableDataFilters().forEach(this::addFilter);
    getTableViewerTab().getTableViewer().entity().getTableDataFilters()
        .addListener((ListChangeListener<Owl<TableDataFilter>>) change -> {
          while (change.next()) {
            if (change.wasAdded()) {
              change.getAddedSubList().forEach(filter -> {
                int ind = getTableViewerTab().getTableViewer().entity().getTableDataFilters()
                    .indexOf(filter);
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
      Owl<TableDataFilter> newFilter = TableDataFilters.createTableDataFilter();
      if (newFilter == null)
        return;
      getTableViewerTab().getTableViewer().entity().getTableDataFilters().add(newFilter);
      editFilter(newFilter);
    });
    contextMenu.getItems().add(createFilter);

    MenuItem open = new MenuItem("Open Filter");
    open.setOnAction(event -> {
      try {
        OwleryOpenDialog<TableDataFilter> openDialog =
            new OwleryOpenDialog<>(TableDataFilter.class);
        openDialog.setSelectionModel(SelectionMode.MULTIPLE);
        openDialog.setAlredyOpenedOwls(
            getTableViewerTab().getTableViewer().entity().getTableDataFilters());
        openDialog.showAndWait(Modality.APPLICATION_MODAL);
        if (openDialog.isOpened()) {
          getTableViewerTab().getTableViewer().entity().getTableDataFilters()
              .addAll(openDialog.getOpenedOwls());
        }
      } catch (ReflectiveOperationException e) {
        e.printStackTrace();
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

  private void addFilter(int ind, Owl<TableDataFilter> filter) {
    FilterToggleButton button =
        new FilterToggleButton(filter, getTableViewerTab().getTableViewer());
    button.setToggleGroup(toggleGroup);
    button.setOnAction(event -> {
      OwlReference<TableData> tableDataRef =
          getTableViewerTab().getTableViewer().entity().getTableDataRef();
      if (tableDataRef.isPresent()) {
        try {
          DataEntity[] dataEntities = TableDataFilters.getDataEntities(filter, tableDataRef.get());
          getTableViewerTab().getTableDataViewTable()
              .setItems(FXCollections.observableArrayList(dataEntities));
        } catch (JAXBException e) {
          Owlook.registerException(e);
        }
      }
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
        Owl<TableDataFilter> newFilter = OwlLoader.INSTANCE.duplicateOwl(filter);
        getTableViewerTab().getTableViewer().entity().getTableDataFilters().add(newFilter);
        editFilter(newFilter);
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(duplicate);

    MenuItem close = new MenuItem("Close Filter");
    close.setOnAction(event -> {
      getTableViewerTab().getTableViewer().entity().getTableDataFilters().remove(filter);
    });
    contextMenu.getItems().add(close);

    MenuItem delete = new MenuItem("Delete Filter");
    delete.setOnAction(event -> {
      var tableViewer = getTableViewerTab().getTableViewer();
      try {
        OwlLoader.INSTANCE.deleteOwl(filter, Arrays.asList(tableViewer),
            DeleteFlag.NO_DEPENDENCIES);
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(delete);

    if (ind < 0)
      getChildren().add(button);
    else
      getChildren().add(ind, button);
  }

  private void addFilter(Owl<TableDataFilter> filter) {
    addFilter(-1, filter);
  }

  private void deleteFilter(Owl<TableDataFilter> filter) {
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

  public Owl<TableDataFilter> getSelectedTableDataFilter() {
    return selectedTableDataFilterProperty().get();
  }

  private void setSelectedTableDataFilter(Owl<TableDataFilter> tableDataFilter) {
    writableSelectedTableDataFilterProperty().set(tableDataFilter);
  }

  private ObjectProperty<Owl<TableDataFilter>> writableSelectedTableDataFilterProperty() {
    if (selectedTableDataFilter == null) {
      selectedTableDataFilter = new SimpleObjectProperty<>();
    }
    return selectedTableDataFilter;
  }

  public ReadOnlyObjectProperty<Owl<TableDataFilter>> selectedTableDataFilterProperty() {
    return writableSelectedTableDataFilterProperty();
  }

  private void editFilter(Owl<TableDataFilter> tableDataFilter) {
    try {
      OwlReference<TableData> tableDataRef =
          getTableViewerTab().getTableViewer().entity().getTableDataRef();
      if (tableDataRef.isPresent()) {
        tableDataFilter.entity().getController(tableDataRef.get()).show();
      } else {
        tableDataFilter.entity().getController().show();
      }
    } catch (IOException e) {
      Owlook.registerException(e);
    }
  }
}
