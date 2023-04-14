package space.sadfox.tableviewer.ui.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.view.FieldView;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViewDao;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.tableviewer.TableViewer;

public class EditViewController extends Controller {
	
	@FXML
	private ChoiceBox<String> TDField;
	
	@FXML
	private Button add;
	
	@FXML
	private TextField friendlyName;
	
	@FXML
	private TableView<FieldView> tableView;
	
	@FXML
	private TextField title;
	
	private TableData tableData;
	private TableDataView view;
	private TableDataViewDao viewDao;
	
	public EditViewController(TableDataView view, TableData tableData) throws IOException {
		super(TableViewer.class.getResource("fxml/edit-view.fxml"));
		
		this.tableData = tableData;
		this.view = view;
		viewDao = new TableDataViewDao(view);
		
		title.textProperty().bindBidirectional(view.titleProperty());
		title.textProperty().addListener((property, oldValue, newValue) -> {
			view.save();
		});
		
		TDField.getItems().addAll(getTableDataFields());
		if (TDField.getItems().size() > 0) {
			TDField.getSelectionModel().select(0);
		}
		
		add.setOnAction(event -> {
			viewDao.addNewField(TDField.getValue(), friendlyName.getText());
			view.save();
		});
		
		getParent().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case Z:
				if (keyEvent.isControlDown()) {
					view.getChangeHistory().back();
					view.save();
				}
				break;
			}
		});
		// =======================Init TableData=======================
		
		TableColumn<FieldView, String> field = new TableColumn<>("Field");
		field.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		field.setCellFactory(ComboBoxTableCell.forTableColumn(getTableDataFields()));
		field.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFieldName(editEvent.getNewValue());
			view.save();
		});
		
		TableColumn<FieldView, String> friendlyName = new TableColumn<>("Friendly Name");
		friendlyName.setCellValueFactory(new PropertyValueFactory<>("friendlyFieldName"));
		friendlyName.setCellFactory(TextFieldTableCell.forTableColumn());
		friendlyName.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFriendlyFieldName(editEvent.getNewValue());
			view.save();
		});
		
		TableColumn<FieldView, Boolean> visible = new TableColumn<>("Visible");
		visible.setCellValueFactory(new PropertyValueFactory<>("visible"));
		visible.setCellFactory(call -> {
			CheckBoxTableCell<FieldView, Boolean> cell = new CheckBoxTableCell<>();
			cell.itemProperty().addListener((prop, oldVal, newVal) -> {
				view.save();
			});
			return cell;
		});
		//visible.setCellFactory(CheckBoxTableCell.forTableColumn(visible));
		
		tableView.getColumns().addAll(field, friendlyName, visible);
		tableView.setItems(view.fieldViewsProperty());
		
		ObjectProperty<FieldView> draggedView = new SimpleObjectProperty<>();
		IntegerProperty draggedInd = new SimpleIntegerProperty();
		
		tableView.setRowFactory(call -> {
			TableRow<FieldView> row = new TableRow<>();
			
			row.setOnDragDetected(dragEvent -> {
				draggedView.set(row.getItem());
				draggedInd.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedInd.get() == row.getIndex() || draggedView.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				tableView.getItems().remove(draggedView.get());
				tableView.getItems().add(tempInd, draggedView.get());
				tableView.getSelectionModel().select(tempInd);
				draggedInd.set(tempInd);
				view.save();
				dragEvent.consume();
			});
			
			return row;
		});
		
		tableView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = tableView.getSelectionModel();
				if (!selection.isEmpty()) {
					var item = selection.getSelectedItem();
					view.getFieldViews().remove(item);
					view.save();
				}
				break;
			}

		});
		
	}
	
	private ObservableList<String> getTableDataFields() {
		List<String> fields = tableData.getFields().stream().map(f -> f.getFieldName()).collect(Collectors.toList());
		return FXCollections.observableList(fields);
	}



}
