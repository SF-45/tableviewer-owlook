package space.sadfox.tableviewer.ui.view;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.ui.base.ButtonList.Moveble;

public class ViewToggleButton extends ToggleButton implements Moveble {
	
	private TableDataView view;
	private TableViewer tableViewer;
	
	

	public ViewToggleButton(TableDataView view, TableViewer tableViewer) {
		this.view = view;
		this.tableViewer = tableViewer;
		this.textProperty().bind(view.titleProperty());
		this.addEventHandler(ActionEvent.ACTION, event -> {
			if (!this.isSelected()) { 
				this.setSelected(true);
			}
		});
	}
	
	public TableDataView getView() {
		return view;
	}

	@Override
	public void moveTo(int ind) {
		if (!tableViewer.getTableDataViews().contains(view)) return;
		tableViewer.getTableDataViews().remove(view);
		tableViewer.getTableDataViews().add(ind, view);
	}

}
