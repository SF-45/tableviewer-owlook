package space.sadfox.tableviewer;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataController;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.owlook.jaxb.ChangeListener;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.StageFactory;

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
    private TableViewerDao tableViewerDao;

	public TableViewerEditController(TableViewer tableViewer) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-tableviewer.fxml"));
		
		this.tableViewer = tableViewer;
		
		init();
	}
	
	private void init() {
		titleTaxtField.setText(getTableViewer().getTitle());
		titleTaxtField.textProperty().bindBidirectional(getTableViewer().titleProperty());
		
		getTableViewer().tableDataConnectionProperty().addListener(change -> {
			refreshTableData();
			getTableViewerDao().getTableData().addEntityChangeListener(changeEntity -> {
				if (changeEntity.wasModify()) {
					refreshTableData();
				}
			});
		});
		refreshTableData();
		
		createTableDataButton.setOnAction(event -> {
			TableData newTableData = TableDataDao.createTableData();
			getTableViewerDao().setTableData(newTableData);
		});
		
		editTableData.setOnAction(event -> {
			try {
				new TableDataController(getTableViewerDao().getTableData()).show();
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		
		selectTableDataButton.setOnAction(event -> {
			try {
				OpenEntityDialog<TableData> openEntityDialog = new OpenEntityDialog<>(TableData.class, null);
				openEntityDialog.showAndWait();
				if (openEntityDialog.isOpened()) {
					getTableViewerDao().setTableData(openEntityDialog.getOpenned());
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		
	}

	private TableViewer getTableViewer() {
		return tableViewer;
	}

	private TableViewerDao getTableViewerDao() {
		if (tableViewerDao == null) {
			tableViewerDao = new TableViewerDao(getTableViewer());
		}
		return tableViewerDao;
	}

	private void refreshTableData() {
		previewTextArea.setText(getTableViewerDao().getTableData().toString());
	}
	
	
	
	
	

}
