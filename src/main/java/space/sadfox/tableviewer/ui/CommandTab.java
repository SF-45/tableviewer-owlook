package space.sadfox.tableviewer.ui;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import space.sadfox.dataccess.command.CommandEntity;
import space.sadfox.dataccess.command.CommandEntityList;
import space.sadfox.dataccess.command.CommandEntityListDao;
import space.sadfox.dataccess.command.CommandTypes;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.tableviewer.ui.base.ButtonList;
import space.sadfox.tableviewer.ui.base.CommandButton;

public class CommandTab extends Tab {
	
	private CommandEntityList targetCommandList;
	private CommandEntityListDao targetCommandListDao;
	
	private ContextMenu contextMenu;
	
	private ButtonList root;
	private TableViewerTab tableViewerTab;
	
	public CommandTab(CommandEntityList target, TableViewerTab tableViewerTab) {
		this.targetCommandList = target;
		this.targetCommandListDao = new CommandEntityListDao(target);
		this.tableViewerTab = tableViewerTab;
		this.setText(target.getTitle());
		root = new ButtonList();
		this.setContent(root);
		
		targetCommandList.getCommands().forEach(this::addCommand);
		targetCommandList.commandsProperty().addListener((ListChangeListener<? super CommandEntity>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(c -> {
						int ind = targetCommandList.getCommands().indexOf(c);
						addCommand(ind, c);
						
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(c -> {
						deleteCommand(c);
					});
				}
			}
		});
		
		contextMenu = new ContextMenu();
		root.setContextMenu(contextMenu);
		this.tabPaneProperty().addListener((property, oldValue, newValue) -> {
			root.setContextMenuHideProperty(newValue.focusedProperty());
		});
		
		
		
//		ContextMenu parentContextMenu = this.getTabPane().getContextMenu();
//		if (parentContextMenu != null && parentContextMenu.getItems().size() != 0) {
//			contextMenu.getItems().addAll(parentContextMenu.getItems());
//			contextMenu.getItems().add(new SeparatorMenuItem());
//		}
		
		MenuItem create = new MenuItem("Create Command");
		create.setOnAction(event -> {
			CommandEntity newCommandEntity = targetCommandListDao.createCommandEntity("title", CommandTypes.EXEC);
			editCommand(newCommandEntity);
		});
		contextMenu.getItems().add(create);
		
		
	}
	
	private void addCommand(int ind, CommandEntity commandEntity) {
		CommandButton comButton = new CommandButton(commandEntity, targetCommandList);
		comButton.setOnAction(event -> {
			var selection = tableViewerTab.getTableDataViewTable().getSelectionModel();
			if (selection.isEmpty()) return;
			
			comButton.getCommand().execCommand(selection.getSelectedItems().toArray(new DataEntity[0]));
		});
		
		ContextMenu commandContextMenu = new ContextMenu();
		comButton.setContextMenu(commandContextMenu);
//		if (contextMenu.getItems().size() > 0) {
//			for (var mi : contextMenu.getItems()) {
//				commandContextMenu.getItems().add(mi);
//			}
//			commandContextMenu.getItems().add(new SeparatorMenuItem());
//		} TODO: Не работает хуй знает почему
		
		MenuItem edit = new MenuItem("Edit Command");
		edit.setOnAction(event -> {
			editCommand(commandEntity);
		});
		commandContextMenu.getItems().add(edit);
		
		MenuItem delete = new MenuItem("Delete Command");
		delete.setOnAction(event -> {
			targetCommandList.getCommands().remove(commandEntity);
		});
		commandContextMenu.getItems().add(delete);
		
		if (ind < 0) root.getChildren().add(comButton);
		else root.getChildren().add(ind, comButton);
	}
	private void addCommand(CommandEntity commandEntity) {
		addCommand(-1, commandEntity);
	}
	
	private void deleteCommand(CommandEntity commandEntity) {
		var rootChildrens = root.getChildren();
		
		for (int i = 0; i < rootChildrens.size(); i++) {
			Node n = rootChildrens.get(i);
			if (n instanceof CommandButton) {
				CommandButton comButton = (CommandButton) n;
				if (comButton.getCommandEntity() == commandEntity) {
					rootChildrens.remove(i);
				}
			}
		}
	}
	
	private void editCommand(CommandEntity commandEntity) {
		var editController = CommandEntityListDao.getCommand(commandEntity).getConfigController();
		editController.getStage().titleProperty().bind(commandEntity.nameProperty());
		editController.show();
	}
	
	
	
	
	

}
