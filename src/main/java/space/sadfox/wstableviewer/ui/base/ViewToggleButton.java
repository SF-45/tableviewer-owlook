package space.sadfox.wstableviewer.ui.base;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.wstableviewer.TableViewer;
import space.sadfox.wstableviewer.ui.base.ButtonList.Moveble;
import space.sadfox.xmldataccess.view.TableDataView;

public class ViewToggleButton extends ToggleButton implements Moveble {
	
	private TableDataView tableDataView;
	private TableViewer tableViewer;
	
	

	public ViewToggleButton(TableDataView view, TableViewer tableViewer) {
		this.tableDataView = view;
		this.tableViewer = tableViewer;
		this.textProperty().bind(view.titleProperty());
		this.addEventHandler(ActionEvent.ACTION, event -> {
			if (!this.isSelected()) { 
				this.setSelected(true);
			}
		});
	}
	
	public TableDataView getView() {
		return tableDataView;
	}

	@Override
	public void moveTo(int ind) {
		List<String> viewsNameList = tableViewer.getTableDataViews();
		String viewFileName = tableDataView.getFileName();
		if (!viewsNameList.contains(viewFileName)) return;
		viewsNameList.remove(viewFileName);
		viewsNameList.add(ind, viewFileName);
	}

}
