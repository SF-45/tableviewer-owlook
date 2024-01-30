package space.sadfox.tableviewer.ui.action;

import java.io.IOException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Tooltip;
import space.sadfox.dataccess.action.Action;
import space.sadfox.dataccess.action.ActionEntities;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionProviderNotFound;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.Logger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class ActionButton extends Button {

  private ActionDecorator actionDecorator;
  private TableViewerTab parent;

  public ActionButton(ActionDecorator actionDecorator, TableViewerTab parent) {
    this.actionDecorator = actionDecorator;
    this.parent = parent;

    this.setText(getActionOwl().head().getTitle());
    getActionOwl().head().titleProperty().bindBidirectional(this.textProperty());

    Tooltip tooltip = new Tooltip();
    tooltip.textProperty().bind(getActionOwl().entity().descriptionProperty());
    this.setTooltip(tooltip);

    TableViewSelectionModel<DataEntity> selection =
        parent.getTableDataViewTable().getSelectionModel();
    this.setOnAction(event -> {
      if (selection.isEmpty())
        return;

      try {
        getAction().run(selection.getSelectedItems().toArray(new DataEntity[0]));
      } catch (ActionProviderNotFound e) {
        MessageBox messageBox = new MessageBox(AlertType.WARNING);
        messageBox.setTitle("Action provider not found");
        messageBox.setHeaderText("Action provider not found");
        messageBox.showAndWait();
      }
    });

    ContextMenu contextMenu = new ContextMenu();
    this.setContextMenu(contextMenu);

    MenuItem edit = new MenuItem("Edit Action");
    edit.setOnAction(event -> {
      try {
        new EditActionController(actionDecorator, parent).show();
      } catch (IOException e) {
        Logger.registerException(1, e);
      }
    });
    contextMenu.getItems().add(edit);

    MenuItem duplicate = new MenuItem("Duplicate Action");
    duplicate.setOnAction(event -> {
      try {
        Owl<ActionEntity> newActionOwl = OwlLoader.INSTANCE.duplicateOwl(getActionOwl());
        ActionDecorator newActionDecorator =
            new ActionDecorator(newActionOwl, getActionDecorator().getTags());
        parent.getTableViewer().entity().getActionDecorators().add(newActionDecorator);
        new EditActionController(newActionDecorator, parent).show();
      } catch (Exception e) {
        Logger.registerException(1, e);
      }
    });
    contextMenu.getItems().add(duplicate);

    MenuItem close = new MenuItem("Close Action");
    close.setOnAction(event -> {
      parent.getTableViewer().entity().getActionDecorators().remove(actionDecorator);
    });
    contextMenu.getItems().add(close);

    MenuItem delete = new MenuItem("Delete Filter");
    delete.setOnAction(event -> {
      if (ActionEntities.deleteActionEntity(getActionOwl())) {
        parent.getTableViewer().entity().getActionDecorators().remove(actionDecorator);
      }
    });
    contextMenu.getItems().add(delete);
  }

  public ActionDecorator getActionDecorator() {
    return actionDecorator;
  }

  public Owl<ActionEntity> getActionOwl() {
    return getActionDecorator().getActionOwl();
  }

  public Action getAction() throws ActionProviderNotFound {
    try {
      return ActionEntities.createAction(getActionOwl(),
          parent.getTableViewer().entity().getTableDataSafe());
    } catch (Nullable e) {
      return ActionEntities.createAction(getActionOwl());
    }

  }

}
