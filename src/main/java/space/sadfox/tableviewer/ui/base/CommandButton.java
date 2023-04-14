package space.sadfox.tableviewer.ui.base;

import javafx.scene.control.Button;
import space.sadfox.dataccess.command.Command;
import space.sadfox.dataccess.command.CommandEntity;
import space.sadfox.dataccess.command.CommandEntityList;
import space.sadfox.dataccess.command.CommandEntityListDao;

public class CommandButton extends Button implements ButtonList.Moveble {
	
	private CommandEntity commandEntity;
	private CommandEntityList parentComList;

	public CommandButton(CommandEntity commandEntity, CommandEntityList parent) {
		this.commandEntity = commandEntity;
		parentComList = parent;
		
		this.textProperty().bind(commandEntity.nameProperty());
	}

	public Command getCommand() {
		return CommandEntityListDao.getCommand(commandEntity);
	}
	
	public CommandEntity getCommandEntity() {
		return commandEntity;
	}

	@Override
	public void moveTo(int ind) {
		var comList = parentComList.getCommands();
		if (!comList.contains(commandEntity)) return;
		System.out.println("From moveTo " + ind);
		comList.remove(commandEntity);
		comList.add(ind, commandEntity);
		
	}
	
	

}
