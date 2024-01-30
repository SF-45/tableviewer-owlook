package space.sadfox.tableviewer.ui;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import space.sadfox.dataccess.dataccess.TableData.DataUpdateListener;
import space.sadfox.dataccess.dataccess.TableDataController;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableViewForTableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.Logger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.ui.action.ActionController;
import space.sadfox.tableviewer.ui.filter.FiltersTab;
import space.sadfox.tableviewer.ui.view.ViewsTab;

public class TableViewerTab extends Tab {

  private Owl<TableViewer> tableViewerOwl;

  private ActionController actionsNode;
  private ViewsTab viewsNode;
  private FiltersTab filtersNode;

  private Menu menu;

  private TableViewForTableData tableDataViewTable;

  public TableViewerTab(Owl<TableViewer> tableViewerOwl) {
    this.tableViewerOwl = tableViewerOwl;

    filtersNode = new FiltersTab(this);
    viewsNode = new ViewsTab(this);

    try {
      actionsNode = new ActionController(this);
    } catch (IOException e) {
      Logger.registerException(1, e);
    }
    this.setContent(getTableDataViewTable());
    initializ();
  }

  private void initializ() {

    DataUpdateListener tableDataUpdateListener = () -> {
      reloadCurrentData();
    };

    try {
      getTableViewer().entity().getTableDataSafe().entity()
          .addDataUpdateListener(tableDataUpdateListener);
    } catch (Nullable e) {
    }

    getTableViewer().entity().tableDataProperty().addListener((property, oldValue, newValue) -> {
      if (oldValue != null) {
        oldValue.entity().removeDataUpdateListener(tableDataUpdateListener);
      }
      if (newValue != null) {
        newValue.entity().addDataUpdateListener(tableDataUpdateListener);
      }
      reloadCurrentData();
    });



    if (filtersNode.getSelectedTableDataFilter() != null) {
      Owl<TableDataFilter> selectFilter = filtersNode.getSelectedTableDataFilter();
      this.textProperty().bind(Bindings.concat(tableViewerOwl.head().titleProperty(), "[",
          selectFilter.head().titleProperty(), "]"));
    } else {
      this.textProperty().bind(tableViewerOwl.head().titleProperty());
    }
    filtersNode.selectedTableDataFilterProperty().addListener((property, oldValue, newValue) -> {
      this.textProperty().unbind();
      if (newValue != null) {
        this.textProperty().bind(Bindings.concat(tableViewerOwl.head().titleProperty(), "[",
            newValue.head().titleProperty(), "]"));
      } else {

        this.textProperty().bind(tableViewerOwl.head().titleProperty());
      }
    });
  }

  public TableViewForTableData getTableDataViewTable() {
    if (tableDataViewTable == null) {
      tableDataViewTable = new TableViewForTableData();
      tableDataViewTable.getFindActionDelay().delayProperty()
          .bind(getTableViewer().entity().searchDelayProperty());
    }
    return tableDataViewTable;
  }

  public Owl<TableViewer> getTableViewer() {
    return tableViewerOwl;
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
      menu = new Menu(getTableViewer().head().getTitle());
      menu.textProperty().bindBidirectional(getTableViewer().head().titleProperty());

      Menu tableDataMenu = new Menu("Data");
      menu.getItems().add(tableDataMenu);

      MenuItem editTableData = new MenuItem("Edit Table Data");
      editTableData.setOnAction(event -> {
        try {
          new TableDataController(getTableViewer().entity().getTableDataSafe()).show();
        } catch (IOException e) {
          Logger.registerException(1, e);
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
          new TableDataDao(getTableViewer().entity().getTableDataSafe()).loadData();
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
          getTableViewer().entity().getController().show();
        } catch (Exception e) {
          Logger.registerException(1, e);
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
