package space.sadfox.wstableviewer;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import space.sadfox.owlook.moduleapi.WorkspaceUI;
import space.sadfox.wstableviewer.ui.CommandNode;
import space.sadfox.wstableviewer.ui.filter.FiltersTab;
import space.sadfox.wstableviewer.ui.view.ViewsTab;
import space.sadfox.xmldataccess.dataccess.TableDataDao;
import space.sadfox.xmldataccess.view.TableViewForTableData;

public class TableViewerUI implements WorkspaceUI {

	private TableViewer tableViewer;
	private TableViewerDao tableViewerDao;

	private CommandNode leftToolPane;
	private TabPane rightToolTabPane = new TabPane();
	private TableViewForTableData tableDataViewTable = new TableViewForTableData();

	private TableDataDao tableDataDao;

	public TableViewerUI(TableViewer tableViewer) throws IOException, JAXBException {
		this.tableViewer = tableViewer;
		tableViewerDao = new TableViewerDao(tableViewer);
		tableDataDao = tableViewerDao.getTableDataDao();

		initializ();
		rightToolTabPane.getTabs().add(new ViewsTab(this));
		rightToolTabPane.getTabs().add(new FiltersTab(this));
		leftToolPane = new CommandNode(this);

	}

	private void initializ() {
		rightToolTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		rightToolTabPane.setSide(Side.TOP);
	}

	public TableViewForTableData getTableDataViewTable() {
		return tableDataViewTable;
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	@Override
	public Node getRightToolsNode() {
		return rightToolTabPane;
	}
	
	@Override
	public Node getRootNode() {
		return tableDataViewTable;
	}

	@Override
	public Node getLeftToolsNode() {
		return leftToolPane;
	}
	
	

}
