package space.sadfox.wstableviewer.ui.filter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.wstableviewer.TableViewer;
import space.sadfox.xmldataccess.dataccess.Comparison;
import space.sadfox.xmldataccess.dataccess.TableData;
import space.sadfox.xmldataccess.filter.Filter;
import space.sadfox.xmldataccess.filter.NextComp;
import space.sadfox.xmldataccess.filter.TableDataFilter;
import space.sadfox.xmldataccess.filter.TableDataFilterDao;

public class EditFilterController extends Controller {

	@FXML
	private SplitMenuButton addto;

	@FXML
	private ComboBox<Comparison> compareComboBox;

	@FXML
	private ComboBox<String> dateFieldComboBox;

	@FXML
	private TableView<Filter> filtersTableView;

	@FXML
	private TextField titleTextBox;

	@FXML
	private TextField valueTextBox;

	private TableDataFilter filter;
	private TableDataFilterDao filterDao;
	private TableData tableData;
	private ObjectProperty<NextComp> currectComp = new SimpleObjectProperty<>(NextComp.AND);

	public EditFilterController(TableDataFilter filter, TableData tableData) throws IOException {
		super(TableViewer.class.getResource("fxml/edit-filter.fxml"));

		this.filter = filter;
		filterDao = new TableDataFilterDao(filter, tableData);
		this.tableData = tableData;

		titleTextBox.textProperty().bindBidirectional(filter.titleProperty());
		titleTextBox.textProperty().addListener((property, oldValue, newValue) -> {
			filter.save();
		});

		dateFieldComboBox.getItems().addAll(getTableDataFields());
		if (dateFieldComboBox.getItems().size() > 0) {
			dateFieldComboBox.getSelectionModel().select(0);
		}

		compareComboBox.getItems().addAll(Comparison.values());
		compareComboBox.getSelectionModel().select(0);

		currectComp.addListener((property, oldValue, newValue) -> {
			addto.setText(newValue.toString());
		});
		MenuItem and = new MenuItem("AND");
		and.setOnAction(event -> currectComp.set(NextComp.AND));
		MenuItem or = new MenuItem("OR");
		or.setOnAction(event -> currectComp.set(NextComp.OR));
		addto.getItems().addAll(and, or);
		addto.setText(currectComp.get().toString());
		addto.setOnAction(event -> {
			filterDao.addNewFilter(dateFieldComboBox.getValue(), compareComboBox.getValue(), valueTextBox.getText(),
					currectComp.get());
			filter.save();
		});

		getParent().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case Z:
				if (keyEvent.isControlDown()) {
					filter.getChangeHistory().back();
					filter.save();
				}
				break;
			}
		});

		// =======================Init TableData=======================
		TableColumn<Filter, String> field = new TableColumn<>("Field");
		field.setEditable(true);
		field.setCellValueFactory(new PropertyValueFactory<>("field"));
		field.setCellFactory(ComboBoxTableCell.forTableColumn(getTableDataFields()));
		field.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setField(editEvent.getNewValue());
			filter.save();
		});

		TableColumn<Filter, Comparison> comparison = new TableColumn<>("Comparison");
		comparison.setEditable(true);
		comparison.setCellValueFactory(new PropertyValueFactory<>("comparision"));
		comparison.setCellFactory(ComboBoxTableCell.forTableColumn(Comparison.values()));
		comparison.setOnEditCommit(event -> {
			event.getRowValue().setComparision(event.getNewValue());
			filter.save();
		});

		TableColumn<Filter, String> value = new TableColumn<>("Value");
		value.setEditable(true);
		value.setCellValueFactory(new PropertyValueFactory<>("value"));
		value.setCellFactory(TextFieldTableCell.forTableColumn());
		value.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setValue(editEvent.getNewValue());
			filter.save();
		});

		TableColumn<Filter, NextComp> next = new TableColumn<>("Next");
		next.setEditable(true);
		next.setCellValueFactory(new PropertyValueFactory<>("next"));
		next.setCellFactory(ComboBoxTableCell.forTableColumn(NextComp.values()));
		next.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setNext(editEvent.getNewValue());
			filter.save();
		});

		filtersTableView.getColumns().addAll(field, comparison, value, next);
		filtersTableView.setItems(filter.filtersProperty());

		ObjectProperty<Filter> draggedFilter = new SimpleObjectProperty<>();
		IntegerProperty draggedInd = new SimpleIntegerProperty();

		filtersTableView.setRowFactory(call -> {
			TableRow<Filter> row = new TableRow<>();

			row.setOnDragDetected(dragEvent -> {
				draggedFilter.set(row.getItem());
				draggedInd.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedInd.get() == row.getIndex() || draggedFilter.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				filtersTableView.getItems().remove(draggedFilter.get());
				filtersTableView.getItems().add(tempInd, draggedFilter.get());
				filtersTableView.getSelectionModel().select(tempInd);
				draggedInd.set(tempInd);
				filter.save();
				dragEvent.consume();
			});

			return row;
		});
		filtersTableView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = filtersTableView.getSelectionModel();
				if (!selection.isEmpty()) {
					var item = selection.getSelectedItem();
					filter.getFilters().remove(item);
					filter.save();
				}
				break;
			}

		});
	}

	private ObservableList<String> getTableDataFields() {
		List<String> fields = tableData.getFields().stream().map(f -> f.getFieldName()).collect(Collectors.toList());
		return FXCollections.observableList(fields);
	}

	private void initTableView() {

	}

}
