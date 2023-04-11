package space.sadfox.wstableviewer.ui.base;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.wstableviewer.TableViewer;
import space.sadfox.wstableviewer.ui.base.ButtonList.Moveble;
import space.sadfox.xmldataccess.filter.TableDataFilter;

public class FilterToggleButton extends ToggleButton implements Moveble {
	
	private TableDataFilter filter;
	private TableViewer tableViewer;

	public FilterToggleButton(TableDataFilter filter, TableViewer tableViewer) {
		this.filter = filter;
		this.tableViewer = tableViewer;
		this.textProperty().bind(filter.titleProperty());
		this.addEventHandler(ActionEvent.ACTION, event -> {
			if (!this.isSelected()) { 
				this.setSelected(true);
			}
		});
	}

	public TableDataFilter getFilter() {
		return filter;
	}

	@Override
	public void moveTo(int ind) {
		List<String> filterNameList = tableViewer.getTableDataFilters();
		String filterFileName = filter.getFileName();
		if (!filterNameList.contains(filterFileName)) return;
		System.out.println("From moveTo " + ind);
		filterNameList.remove(filterFileName);
		filterNameList.add(ind, filterFileName);
		
		
	}
	
	
	
	

}
