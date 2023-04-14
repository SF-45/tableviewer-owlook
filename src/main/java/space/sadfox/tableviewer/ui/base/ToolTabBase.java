package space.sadfox.tableviewer.ui.base;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.TableViewerTab;

public abstract class ToolTabBase extends Tab {

	private TableViewerTab tableViewerTab;
	private ButtonList buttonList;

	public ToolTabBase(TableViewerTab tableViewerTab, String title) {
		this.tableViewerTab = tableViewerTab;

		this.setText(title);
		this.buttonList = new ButtonList();
		this.setContent(buttonList);
	}

	protected TableViewer getTableViewer() {
		return tableViewerTab.getTableViewer();
	}

	protected TableViewerDao getTableViewerDao() {
		return tableViewerTab.getTableViewerDao();
	}

	protected TableViewerTab getTableViewerTab() {
		return tableViewerTab;
	}

	protected ButtonList getButtonList() {
		return buttonList;
	}

	protected void addToButtonList(Node node) {
		buttonList.getChildren().add(node);
	}

}
