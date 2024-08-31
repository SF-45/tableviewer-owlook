package space.sadfox.tableviewer.ui.action;

import java.io.IOException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ActionDecoratorsCollector;
import space.sadfox.tableviewer.ResourceTarget;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class EditActionController extends FXMLController {

  @FXML
  private BorderPane root;

  @FXML
  private TableView<String> tags;

  private ActionDecorator actionDecorator;
  // private ActionEntity actionEntity;

  private TableViewerTab tableViewerTab;
  private ActionDecoratorsCollector collector;

  public EditActionController(ActionDecorator actionDecorator, TableViewerTab parent)
      throws IOException {
    super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

    this.actionDecorator = actionDecorator;
    this.tableViewerTab = parent;
    collector = new ActionDecoratorsCollector(parent.getTableViewer());
    init();

    tags.setItems(getActionDecoratorsCollector().getTags());

  }

  private void init() throws IOException {
    Controller actionEntityController;
    if (getParentTableData().isPresent()) {
      actionEntityController = getActionOwl().entity().getController(getParentTableData().get());
    } else {
      actionEntityController = getActionOwl().entity().getController();
    }
    root.setCenter(actionEntityController.getParent());
    stageTitle.bind(actionEntityController.stageTitleProperty());
    initTagsListView();
  }

  private void initTagsListView() {
    tags.setEditable(true);



    TableColumn<String, Boolean> selectedTagsColumn = new TableColumn<>();
    selectedTagsColumn.setEditable(true);
    tags.getColumns().add(selectedTagsColumn);
    selectedTagsColumn.setCellValueFactory(callback -> {
      BooleanProperty boolProperty =
          new SimpleBooleanProperty(getActionDecorator().getTags().contains(callback.getValue()));
      ChangeListener<Boolean> changeListener = (property, oldValue, newValue) -> {
        if (newValue) {
          getActionDecorator().getTags().add(callback.getValue());
        } else {
          getActionDecorator().getTags().remove(callback.getValue());
        }
      };

      boolProperty.addListener(changeListener);
      return boolProperty;
      //
    });
    selectedTagsColumn.setCellFactory(callback -> new CheckBoxTableCell<>());


    TableColumn<String, String> nameTagsColumn = new TableColumn<>();
    nameTagsColumn.setEditable(true);
    tags.getColumns().add(nameTagsColumn);
    nameTagsColumn.setCellValueFactory(callback -> new SimpleStringProperty(callback.getValue()));
    nameTagsColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    nameTagsColumn.setOnEditCommit(event -> {
      String oldValue = event.getOldValue();
      String newValue = event.getNewValue();
      if (oldValue.equals(newValue))
        return;
      var tagList = getActionDecoratorsCollector().getTags();
      if (tagList.contains(newValue))
        return;

      getActionDecoratorsCollector().replaceTag(oldValue, newValue);
    });

    // tags.setCellFactory(call -> {
    // TextFieldListCell<StringProperty> cell = new TextFieldListCell<>();
    //
    // cell.setConverter(new StringConverter<StringProperty>() {
    //
    // @Override
    // public String toString(StringProperty object) {
    // return object.get();
    // }
    //
    // @Override
    // public StringProperty fromString(String string) {
    // return new SimpleStringProperty(string);
    // }
    //
    // });
    //
    // cell.setEditable(true);
    // return cell;
    // });

    ContextMenu tagsContextMenu = new ContextMenu();
    tags.setContextMenu(tagsContextMenu);

    MenuItem newTag = new MenuItem("Create Tag");
    newTag.setOnAction(event -> {
      String newTagName = "New Tag";
      int i = 1;
      while (getActionDecoratorsCollector().getTags().contains(newTagName)) {
        newTagName = "New Tag " + i++;
      }
      getActionDecorator().getTags().add(newTagName);

    });
    tagsContextMenu.getItems().add(newTag);

    MenuItem deleteTag = new MenuItem("Delete Tag");
    deleteTag.setOnAction(event -> {
      getActionDecoratorsCollector().removeTag(tags.getSelectionModel().getSelectedItem());
    });
    tagsContextMenu.getItems().add(deleteTag);

  }

  private ActionDecoratorsCollector getActionDecoratorsCollector() {
    if (collector == null) {
      collector = new ActionDecoratorsCollector(getTableViewerTab().getTableViewer());
    }
    return collector;
  }

  private ActionDecorator getActionDecorator() {
    return actionDecorator;
  }

  private Owl<ActionEntity> getActionOwl() {
    return getActionDecorator().getActionOwlRef().get();
  }

  private TableViewerTab getTableViewerTab() {

    return tableViewerTab;
  }

  private OwlReference<TableData> getParentTableData() {
    return getTableViewerTab().getTableViewer().entity().getTableDataRef();
  }

}
