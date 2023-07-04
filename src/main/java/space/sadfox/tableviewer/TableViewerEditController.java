package space.sadfox.tableviewer;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.owlook.jaxb.EntityChangeListener;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.Nullable;

public class TableViewerEditController extends Controller {

	@FXML
	private Button createTableDataButton;

	@FXML
	private Button editTableData;

	@FXML
	private TextArea previewTextArea;

	@FXML
	private Button selectTableDataButton;

	@FXML
	private TextField titleTaxtField;

	private TableViewer tableViewer;

	public TableViewerEditController(TableViewer tableViewer) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-tableviewer.fxml"));

		this.tableViewer = tableViewer;

		init();
	}

	private void init() {
		titleTaxtField.setText(getTableViewer().getTitle());
		titleTaxtField.textProperty().bindBidirectional(getTableViewer().titleProperty());

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

		createTableDataButton.setOnAction(event -> {
			TableData newTableData = TableDataDao.createTableData();
			getTableViewer().setTableData(newTableData);
		});

		editTableData.setOnAction(event -> {
			try {
				getTableViewer().getTableDataSafe().getConfigController().show();
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			} catch (Nullable e) {}
		});

		selectTableDataButton.setOnAction(event -> {
			try {
				OpenEntityDialog<TableData> openEntityDialog = new OpenEntityDialog<>(TableData.class, SelectionMode.SINGLE);
				openEntityDialog.showAndWait();
				if (openEntityDialog.isOpened()) {
					getTableViewer().setTableData(openEntityDialog.getOpenned().get(0));
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});

	}

	private TableViewer getTableViewer() {
		return tableViewer;
	}

	private void refreshTableData() {
		try {
			previewTextArea.setText(getTableViewer().getTableDataSafe().toString());
		} catch (Nullable e) {
			previewTextArea.setText("");
		}

	}

}
