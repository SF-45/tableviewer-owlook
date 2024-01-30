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
import javafx.scene.control.ToggleGroup;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilters;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Logger;
import space.sadfox.owlook.utils.Nullable;
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
    getTableViewerTab().getTableViewer().entity().tableDataFiltersProperty()
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
      newFilter.head().setTitle("New Filter");
      getTableViewerTab().getTableViewer().entity().getTableDataFilters().add(newFilter);
      editFilter(newFilter);
    });
    contextMenu.getItems().add(createFilter);

    // MenuItem open = new MenuItem("Open Filter");
    // open.setOnAction(event -> {
    // try {
    // OpenEntityDialog<TableDataFilter> openDialog = new OpenEntityDialog<>(TableDataFilter.class,
    // SelectionMode.MULTIPLE, getTableViewerTab().getTableViewer().getTableDataFilters());
    //
    // openDialog.setModality(Modality.APPLICATION_MODAL);
    // openDialog.showAndWait();
    // if (openDialog.isOpened()) {
    // openDialog.getOpenned()
    // .forEach(f -> getTableViewerTab().getTableViewer().getTableDataFilters().add(f));
    // }
    // } catch (IOException e) {
    // OwlLogger.registerException(1, e);
    // }
    // });
    // contextMenu.getItems().add(open);
    // TODO: Сделвать диалог открытия Совы

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
      try {
        DataEntity[] dataEntities = TableDataFilters.getDataEntities(filter,
            getTableViewerTab().getTableViewer().entity().getTableDataSafe());
        getTableViewerTab().getTableDataViewTable()
            .setItems(FXCollections.observableArrayList(dataEntities));
      } catch (JAXBException e) {
        Logger.registerException(1, e);
      } catch (Nullable e) {
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
        Logger.registerException(1, e);
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
      if (TableDataFilters.deleteTableDataFilter(filter)) {
        getTableViewerTab().getTableViewer().entity().getTableDataFilters().remove(filter);
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
      tableDataFilter.entity()
          .getController(getTableViewerTab().getTableViewer().entity().getTableDataSafe()).show();
    } catch (IOException e) {
      Logger.registerException(1, e);
    } catch (Nullable e) {
      try {
        tableDataFilter.entity().getController().show();
      } catch (IOException e1) {
        Logger.registerException(1, e1);
      }
    }
  }

}
