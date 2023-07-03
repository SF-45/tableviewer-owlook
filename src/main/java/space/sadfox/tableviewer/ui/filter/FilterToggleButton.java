package space.sadfox.tableviewer.ui.filter;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.ui.base.ButtonList;
import space.sadfox.tableviewer.ui.base.ButtonList.Moveble;

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
		if (!tableViewer.getTableDataFilters().contains(filter)) return;
		tableViewer.getTableDataFilters().remove(filter);
		tableViewer.getTableDataFilters().add(ind, filter);
	}
	
	
	
	

}
