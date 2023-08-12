package space.sadfox.tableviewer;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDatas;
import space.sadfox.owlook.base.jaxb.EntityChangeListener;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.owlook.utils.OwlLogger;

public class TableViewerEditController extends FXMLController {

	@FXML
	private Button createTableDataButton;

	@FXML
	private Button editTableData;

	@FXML
	private TextArea previewTextArea;

	@FXML
	private Slider searchDelaySlider;

	@FXML
	private TextField searchDelayTextBox;

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
			TableData newTableData = TableDatas.createTableData();
			getTableViewer().setTableData(newTableData);
		});

		editTableData.setOnAction(event -> {
			try {
				getTableViewer().getTableDataSafe().getConfigController().show();
			} catch (IOException e) {
				OwlLogger.registerException(1, e);
			} catch (Nullable e) {
			}
		});

		searchDelaySlider.setValue(getTableViewer().getSearchDelay());
		searchDelaySlider.valueProperty().bindBidirectional(getTableViewer().searchDelayProperty());

		searchDelayTextBox.setText(String.valueOf(searchDelaySlider.getValue()));
		searchDelayTextBox.textProperty().bindBidirectional(searchDelaySlider.valueProperty().asObject(),
				new StringConverter<Double>() {
					@Override
					public String toString(Double object) {
						return String.valueOf(object.longValue());
					}

					@Override
					public Double fromString(String string) {
						Double min = searchDelaySlider.getMin();
						Double max = searchDelaySlider.getMax();
						try {
							return normolizeDouble(min, max, Double.parseDouble(string));
						} catch (NumberFormatException e) {
							return min;
						}
					}
				});

		searchDelayTextBox.setTextFormatter(new TextFormatter<>(change -> {
			if (change.getText().isEmpty()) {
				return change;
			} else if (!change.getText().matches("[0-9]+")) {
				return null;
			} else {
				double max = searchDelaySlider.getMax();
				Double i = searchDelaySlider.getMin();

				int endRange = change.getControlText().length();

				try {
					i = Double.parseDouble(change.getControlNewText());
					if (i > max) {
						i = max;
						throw new NumberFormatException();
					}
				} catch (NumberFormatException e) {
					change.setRange(0, endRange);
					change.setText(String.valueOf(i.longValue()));
				}
				return change;
			}
		}));
		Runnable minValidate = () -> {
			Double min = searchDelaySlider.getMin();
			Double i = min;
			String newText = searchDelayTextBox.getText();

			try {
				i = Double.parseDouble(newText);
				if (i < min) {
					i = min;
					throw new NumberFormatException();
				}

			} catch (NumberFormatException e) {
				searchDelayTextBox.setText(String.valueOf(i.longValue()));
			}
			System.out.println("Min = " + min + " i = " + i);
		};
		searchDelayTextBox.setOnAction(event -> minValidate.run());
		searchDelayTextBox.focusedProperty().addListener((property, oldValue, newValue) -> minValidate.run());

	}

	private Double normolizeDouble(Double min, Double max, Double current) {
		if (current < min) {
			return min;
		}
		if (current > max) {
			return max;
		}
		return current;
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
