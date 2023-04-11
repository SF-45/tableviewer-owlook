package space.sadfox.wstableviewer.ui;

import javafx.scene.control.TabPane;
import space.sadfox.wstableviewer.TableViewerDao;
import space.sadfox.wstableviewer.TableViewerUI;

public class CommandNode extends TabPane {
	
	private TableViewerUI tableViewerUI;

	public CommandNode(TableViewerUI tableViewerUI) {
		this.tableViewerUI = tableViewerUI;
		
		TableViewerDao tableViewerDao = new TableViewerDao(tableViewerUI.getTableViewer());
		
		for (var comList : tableViewerDao.getCommandEtityLists()) {
			this.getTabs().add(new CommandTab(comList, tableViewerUI));
			comList.getChangeHistory().addChangeListener(() -> comList.save());
		}
		
		
		
	}
	
	
	
	

}
