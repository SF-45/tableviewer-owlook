package space.sadfox.tableviewer.ui.action;

import java.util.Arrays;
import java.util.Optional;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Tooltip;
import space.sadfox.dataccess.action.Action;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionProvider;
import space.sadfox.dataccess.action.ActionProviderNotFound;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlLoader.DeleteFlag;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.ui.base.ControllerException;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.Owlook;
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

    TableViewSelectionModel<DataEntity> selection = parent.getTableDataViewTable().getSelectionModel();
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
      } catch (ControllerException e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(edit);

    MenuItem duplicate = new MenuItem("Duplicate Action");
    duplicate.setOnAction(event -> {
      try {
        Owl<ActionEntity> newActionOwl = OwlLoader.INSTANCE.duplicateOwl(getActionOwl());
        ActionDecorator newActionDecorator = new ActionDecorator(newActionOwl, getActionDecorator().getTags());
        parent.getTableViewer().entity().getActionDecorators().add(newActionDecorator);
        new EditActionController(newActionDecorator, parent).show();
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(duplicate);

    MenuItem close = new MenuItem("Close Action");
    close.setOnAction(event -> {
      parent.getTableViewer().entity().getActionDecorators().remove(actionDecorator);
    });
    contextMenu.getItems().add(close);

    MenuItem delete = new MenuItem("Delete Action");
    delete.setOnAction(event -> {
      try {
        OwlLoader.INSTANCE.deleteOwl(getActionOwl(), Arrays.asList(parent.getTableViewer()),
            DeleteFlag.NO_DEPENDENCIES);
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });
    contextMenu.getItems().add(delete);
  }

  public ActionDecorator getActionDecorator() {
    return actionDecorator;
  }

  public Owl<ActionEntity> getActionOwl() {
    return getActionDecorator().getActionOwlRef().get();
  }

  public Action getAction() throws ActionProviderNotFound {
    Optional<ActionProvider> oProvider = getActionOwl().entity().getActionProviderSafe();
    OwlReference<TableData> tableDataRef = parent.getTableViewer().entity().getTableDataRef();
    if (oProvider.isPresent()) {
      if (tableDataRef.isPresent()) {
        return oProvider.get().createAction(getActionOwl(), tableDataRef.get());
      } else {
        return oProvider.get().createAction(getActionOwl());
      }
    } else {
      throw new ActionProviderNotFound();
    }
  }

}
