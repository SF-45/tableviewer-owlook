package space.sadfox.tableviewer.ui.view;

import java.util.Arrays;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViews;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlLoader.DeleteFlag;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.owlery.OwleryOpenDialog;
import space.sadfox.owlook.ui.base.ControllerException;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;

public class ViewsTab extends ButtonList {

  private ToggleGroup toggleGroup;
  private TableViewerTab tableViewerTab;

  public ViewsTab(TableViewerTab tableViewerTab) {

    this.tableViewerTab = tableViewerTab;

    toggleGroup = new ToggleGroup();
    getTableViewerTab().getTableViewer().entity().getTableDataViews().forEach(this::addView);
    getTableViewerTab().getTableViewer().entity().getTableDataViews()
        .addListener((ListChangeListener<Owl<TableDataView>>) change -> {
          while (change.next()) {
            if (change.wasAdded()) {
              change.getAddedSubList().forEach(view -> {
                int ind = getTableViewerTab().getTableViewer().entity().getTableDataViews().indexOf(view);
                addView(ind, view);

              });
            }
            if (change.wasRemoved()) {
              change.getRemoved().forEach(view -> {
                deleteView(view);
              });
            }
          }
        });

    ContextMenu contextMenu = new ContextMenu();
    setContextMenu(contextMenu);

    MenuItem createView = new MenuItem("Create View");
    createView.setOnAction(event -> {
      Owl<TableDataView> newView = TableDataViews.createTableDataView();
      if (newView == null)
        return;
      newView.head().setTitle("New View");
      getTableViewerTab().getTableViewer().entity().getTableDataViews().add(newView);
      editView(newView);
    });
    contextMenu.getItems().add(createView);

    MenuItem open = new MenuItem("Open View");
    open.setOnAction(event -> {
      try {
        OwleryOpenDialog<TableDataView> openDialog = new OwleryOpenDialog<>(TableDataView.class);
        openDialog.setSelectionModel(SelectionMode.MULTIPLE);
        openDialog
            .setAlredyOpenedOwls(getTableViewerTab().getTableViewer().entity().getTableDataViews());
        openDialog.showAndWait(Modality.APPLICATION_MODAL);
        if (openDialog.isOpened()) {
          getTableViewerTab().getTableViewer().entity().getTableDataViews()
              .addAll(openDialog.getOpenedOwls());
        }

      } catch (ReflectiveOperationException e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(open);

  }

  private void addView(int ind, Owl<TableDataView> view) {
    if (view == null)
      return;

    ViewToggleButton button = new ViewToggleButton(view, getTableViewerTab().getTableViewer());
    button.setToggleGroup(toggleGroup);
    button.setOnAction(event -> {
      getTableViewerTab().getTableDataViewTable().setTableDataView(view);
    });
    if (getChildren().size() == 0) {
      button.fire();
    }
    ContextMenu contextMenu = new ContextMenu();
    button.setContextMenu(contextMenu);

    MenuItem edit = new MenuItem("Edit View");
    edit.setOnAction(event -> {
      editView(view);
    });
    contextMenu.getItems().add(edit);

    MenuItem duplicate = new MenuItem("Duplicate View");
    duplicate.setOnAction(event -> {
      try {
        Owl<TableDataView> newView = OwlLoader.INSTANCE.duplicateOwl(view);
        getTableViewerTab().getTableViewer().entity().getTableDataViews().add(newView);
        editView(newView);
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(duplicate);

    MenuItem close = new MenuItem("Close View");
    close.setOnAction(event -> {
      getTableViewerTab().getTableViewer().entity().getTableDataViews().remove(view);
    });
    contextMenu.getItems().add(close);

    MenuItem delete = new MenuItem("Delete View");
    delete.setOnAction(event -> {
      var tableViewer = getTableViewerTab().getTableViewer();
      try {
        OwlLoader.INSTANCE.deleteOwl(view, Arrays.asList(tableViewer), DeleteFlag.NO_DEPENDENCIES);
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

  private void addView(Owl<TableDataView> view) {
    addView(-1, view);
  }

  private void deleteView(Owl<TableDataView> view) {
    var btnList = getChildren();
    for (int i = 0; i < btnList.size(); i++) {
      Node node = btnList.get(i);
      if (node instanceof ViewToggleButton) {
        ViewToggleButton viewButton = (ViewToggleButton) node;
        if (viewButton.getView().equals(view)) {
          btnList.remove(i);
          return;
        }
      }
    }
  }

  private void editView(Owl<TableDataView> view) {
    try {
      OwlReference<TableData> tableDataRef = getTableViewerTab().getTableViewer().entity().getTableDataRef();
      if (tableDataRef.isPresent()) {
        view.entity().getController(tableDataRef.get()).show();
      } else {
        view.entity().getController().show();
      }
    } catch (ControllerException e) {
      Owlook.registerException(e);
    }
  }

  public TableViewerTab getTableViewerTab() {
    return tableViewerTab;
  }
}
