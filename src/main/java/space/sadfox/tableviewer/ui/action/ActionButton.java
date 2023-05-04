package space.sadfox.tableviewer.ui.action;

import java.io.IOException;

import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView.TableViewSelectionModel;
import space.sadfox.dataccess.action.Action;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class ActionButton extends Button {
	
	private ActionEntity actionEntity;
	private ActionDecorator actionDecorator;
	private Action action;
	private TableViewerTab parent;

	public ActionButton(ActionDecorator actionDecorator, TableViewerTab parent) {
		this.actionDecorator = actionDecorator;
		this.parent = parent;
		
		this.setText(getActionEntity().getTitle());
		getActionEntity().titleProperty().bindBidirectional(this.textProperty());
		TableViewSelectionModel<DataEntity> selection = parent.getTableDataViewTable().getSelectionModel();
		this.setOnAction(event -> {
			if (selection.isEmpty()) return;
			
			getAction().run(selection.getSelectedItems().toArray(new DataEntity[0]));
		});
		
		ContextMenu contextMenu = new ContextMenu();
		this.setContextMenu(contextMenu);
		
		MenuItem edit = new MenuItem("Edit Action");
		edit.setOnAction(event -> {
			try {
				new EditActionController(actionDecorator, parent).show();
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(edit);
		
		MenuItem close = new MenuItem("Close Action");
		close.setOnAction(event -> {
			parent.getTableViewer().getActionDecorators().remove(actionDecorator);
		});
		contextMenu.getItems().add(close);
		
		MenuItem delete = new MenuItem("Delete Filter");
		delete.setOnAction(event -> {
			EntityLoader loader = new EntityLoader();
			if (loader.deleteEntity(getActionEntity())) {
				parent.getTableViewer().getActionDecorators().remove(actionDecorator);
			}
		});
		contextMenu.getItems().add(delete);
	}

	public ActionDecorator getActionDecorator() {
		return actionDecorator;
	}
	
	public ActionEntity getActionEntity() {
		if (actionEntity == null ) {
			actionEntity = TableViewerDao.getActionEntity(getActionDecorator());
		}
		return actionEntity;
	}
	
	public Action getAction() {
		if (action == null) {
			action = parent.getTableViewerDao().getActionEntityDao(getActionDecorator()).createAction();
		}
		return action;
	}


}
