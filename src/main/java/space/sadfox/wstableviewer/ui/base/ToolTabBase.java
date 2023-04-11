package space.sadfox.wstableviewer.ui.base;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import space.sadfox.wstableviewer.TableViewer;
import space.sadfox.wstableviewer.TableViewerDao;
import space.sadfox.wstableviewer.TableViewerUI;

public abstract class ToolTabBase extends Tab {

	private TableViewerUI tableViewerUI;
	private TableViewer tableViewer;
	private TableViewerDao tableViewerDao;
	private ButtonList buttonList;

	public ToolTabBase(TableViewerUI tableViewerUI, String title) {
		this.tableViewerUI = tableViewerUI;
		this.tableViewer = tableViewerUI.getTableViewer();
		this.tableViewerDao = new TableViewerDao(tableViewer);

		this.setText(title);
		this.buttonList = new ButtonList();
		this.setContent(buttonList);
	}

	protected TableViewer getTableViewer() {
		return tableViewer;
	}

	protected TableViewerDao getTableViewerDao() {
		return tableViewerDao;
	}

	protected TableViewerUI getTableViewerUI() {
		return tableViewerUI;
	}

	protected ButtonList getButtonList() {
		return buttonList;
	}

	protected void addToButtonList(Node node) {
		buttonList.getChildren().add(node);
	}

}
