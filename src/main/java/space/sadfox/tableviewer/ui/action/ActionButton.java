package space.sadfox.tableviewer.ui.action;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView.TableViewSelectionModel;
import space.sadfox.dataccess.action.Action;
import space.sadfox.dataccess.action.ActionEntities;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionProviderNotFound;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class ActionButton extends Button {

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
			if (selection.isEmpty())
				return;

			try {
				getAction().run(selection.getSelectedItems().toArray(new DataEntity[0]));
			} catch (ActionProviderNotFound e) {
				// TODO Оповещение туть
			}
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

		MenuItem duplicate = new MenuItem("Duplicate Action");
		duplicate.setOnAction(event -> {
			try {
				ActionEntity newActionEntity = EntityLoader.INSTANCE.duplicateEntity(getActionEntity());
				ActionDecorator newActionDecorator = new ActionDecorator(newActionEntity,
						getActionDecorator().getTags());
				parent.getTableViewer().getActionDecorators().add(newActionDecorator);
				new EditActionController(newActionDecorator, parent).show();
			} catch (JAXBException | IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(duplicate);

		MenuItem close = new MenuItem("Close Action");
		close.setOnAction(event -> {
			parent.getTableViewer().getActionDecorators().remove(actionDecorator);
		});
		contextMenu.getItems().add(close);

		MenuItem delete = new MenuItem("Delete Filter");
		delete.setOnAction(event -> {
			if (ActionEntities.deleteActionEntity(getActionEntity())) {
				parent.getTableViewer().getActionDecorators().remove(actionDecorator);
			}
		});
		contextMenu.getItems().add(delete);
	}

	public ActionDecorator getActionDecorator() {
		return actionDecorator;
	}

	public ActionEntity getActionEntity() {
		return getActionDecorator().getAction();
	}

	public Action getAction() throws ActionProviderNotFound {
		try {
			return ActionEntities.createAction(getActionEntity(), parent.getTableData());
		} catch (Nullable e) {
			return ActionEntities.createAction(getActionEntity());
		}

	}

}
