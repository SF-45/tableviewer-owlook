package space.sadfox.tableviewer.ui.action;

import java.io.IOException;
import java.net.URL;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import space.sadfox.dataccess.action.Action;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ResourceTarget;
import space.sadfox.tableviewer.TableViewerDao;

public class EditActionController extends Controller {
	
	@FXML
    private BorderPane root;

    @FXML
    private ListView<StringProperty> tags;

    @FXML
    private TextField title;
	
	private ActionDecorator actionDecorator;
	private ActionEntity actionEntity;
	private Action action;
	
	

	public EditActionController(ActionDecorator actionDecorator) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));
		this.actionDecorator = actionDecorator;
		actionEntity = TableViewerDao.getActionEntity(actionDecorator);
		action = TableViewerDao.getActionEntityDao(actionEntity).createAction();
		
		title.textProperty().bind(actionEntity.titleProperty());
		root.setCenter(action.getConfigController().getParent());
		
		
		tags.setItems(actionDecorator.tagsProperty());
		tags.setEditable(true);
		tags.setCellFactory(call -> {
			TextFieldListCell<StringProperty> cell = new TextFieldListCell<>();
			
			cell.setConverter(new StringConverter<StringProperty>() {

				@Override
				public String toString(StringProperty object) {
					return object.get();
				}

				@Override
				public StringProperty fromString(String string) {
					return new SimpleStringProperty(string);
				}
				
			});
			
			cell.setEditable(true);
			return cell;
		});
		
		ContextMenu tagsContextMenu = new ContextMenu();
		tags.setContextMenu(tagsContextMenu);
		
		MenuItem newTag = new MenuItem("Create Tag");
		newTag.setOnAction(event -> {
			StringProperty tag = new SimpleStringProperty("New Tag");
			actionDecorator.getTags().add(tag);
			
		});
		tagsContextMenu.getItems().add(newTag);
		
		MenuItem deleteTag = new MenuItem("Delete Tag");
		deleteTag.setOnAction(event -> {
			actionDecorator.getTags().removeAll(tags.getSelectionModel().getSelectedItems());
		});
		tagsContextMenu.getItems().add(deleteTag);
	}
	
	

}
