package space.sadfox.tableviewer.ui.action;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.ControllerNotDefined;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ResourceTarget;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class EditActionController extends Controller {
	
	@FXML
    private BorderPane root;

    @FXML
    private ListView<StringProperty> tags;
	
	private ActionDecorator actionDecorator;
	private ActionEntity actionEntity;
	
	private TableViewerTab tableViewerTab;

	public EditActionController(ActionDecorator actionDecorator, TableViewerTab parent) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

		this.actionDecorator = actionDecorator;
		this.tableViewerTab = parent;
		init();
		
		tags.setItems(getActionDecorator().tagsProperty());
		
		
		
	}
	
	private void init() throws IOException {
		getStage().titleProperty().bind(Bindings.concat("Edit Action [", getActionEntity().titleProperty(), "]"));
		
		try {
			root.setCenter(getActionEntity().getConfigController(getParentTableData()).getParent());
		} catch (Nullable e) {
			root.setCenter(getActionEntity().getConfigController().getParent());
		}
		
		
		initTagsListView();
	}
	
	private void initTagsListView() {
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
			getActionDecorator().getTags().add(tag);
			
		});
		tagsContextMenu.getItems().add(newTag);
		
		MenuItem deleteTag = new MenuItem("Delete Tag");
		deleteTag.setOnAction(event -> {
			getActionDecorator().getTags().removeAll(tags.getSelectionModel().getSelectedItems());
		});
		tagsContextMenu.getItems().add(deleteTag);
		
		
	}
	
	private ActionDecorator getActionDecorator() {
		return actionDecorator;
	}
	
	private ActionEntity getActionEntity() {
		if (actionEntity == null) {
			actionEntity = TableViewerDao.getActionEntity(getActionDecorator());
		}
		return actionEntity;
	}

	private TableViewerTab getTableViewerTab() {
		
		return tableViewerTab;
	}
	
	private TableData getParentTableData() throws Nullable {
		return getTableViewerTab().getTableViewerDao().getTableData();
	}
	
	
	
	

}
