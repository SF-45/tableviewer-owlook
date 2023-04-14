package space.sadfox.tableviewer.ui;

import javafx.scene.control.TabPane;
import space.sadfox.tableviewer.TableViewerDao;

public class CommandNode extends TabPane {
	
	private TableViewerTab tableViewerTab;

	public CommandNode(TableViewerTab tableViewerTab) {
		this.tableViewerTab = tableViewerTab;
		
		TableViewerDao tableViewerDao = new TableViewerDao(tableViewerTab.getTableViewer());
		
		for (var comList : tableViewerDao.getCommandEtityLists()) {
			this.getTabs().add(new CommandTab(comList, tableViewerTab));
			comList.getChangeHistory().addChangeListener(() -> comList.save());
		}
		
		
		
	}
	
	
	
	

}
