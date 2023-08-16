package space.sadfox.tableviewer;

import java.io.IOException;

import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDatas;
import space.sadfox.owlook.base.jaxb.EntityChangeListener;
import space.sadfox.owlook.ui.base.DesignController;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;

public class TableViewerEditController extends DesignController<TableViewerEditDesigner> {
	
	private final TableViewer tableViewer;

	public TableViewerEditController(TableViewer tableViewer) {
		super(new TableViewerEditDesigner());
		this.tableViewer = tableViewer;
		
		stageTitle.bind(tableViewer.titleProperty());
		
		DESIGN.titleTextField.setText(getTableViewer().getTitle());
		DESIGN.titleTextField.textProperty().bindBidirectional(getTableViewer().titleProperty());
		
		EntityChangeListener tbListener = change -> {
			if (change.wasModify()) {
				refreshTableData();
			}
		};

		getTableViewer().tableDataProperty().addListener((property, oldValue, newValue) -> {
			refreshTableData();
			if (oldValue != null) {
				oldValue.removeEntityChangeListener(tbListener);
			}
			if (newValue != null) {
				newValue.addEntityChangeListener(tbListener);
			}
		});
		refreshTableData();

		DESIGN.createTableDataButton.setOnAction(event -> {
			TableData newTableData = TableDatas.createTableData();
			getTableViewer().setTableData(newTableData);
		});

		DESIGN.editTableDataButton.setOnAction(event -> {
			try {
				getTableViewer().getTableDataSafe().getConfigController().show();
			} catch (IOException e) {
				OwlLogger.registerException(1, e);
			} catch (Nullable e) {
			}
		});

		DESIGN.searchDelaySlider.setValue(getTableViewer().getSearchDelay());
		DESIGN.searchDelaySlider.valueProperty().bindBidirectional(getTableViewer().searchDelayProperty());
		
		DESIGN.searchDelayField.setValue(DESIGN.searchDelaySlider.getValue());
		DESIGN.searchDelaySlider.valueProperty().bindBidirectional(DESIGN.searchDelayField.valueProperty());
	}

	private TableViewer getTableViewer() {
		return tableViewer;
	}

	private void refreshTableData() {
		try {
			DESIGN.previewTextArea.setText(getTableViewer().getTableDataSafe().toString());
		} catch (Nullable e) {
			DESIGN.previewTextArea.setText("");
		}

	}

}
