package space.sadfox.tableviewer;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import space.sadfox.owlook.ui.base.FormDesigner;
import space.sadfox.owlook.ui.base.FormDesigners;
import space.sadfox.owlook.ui.base.NumberField;

public class TableViewerEditDesigner extends FormDesigner {
	
	static class SearchDelayField extends NumberField<Number> {
		

		public SearchDelayField() {
			super(0d);
		}

		public SearchDelayField(Double initValue, Double minValue, Double maxValue) {
			super(initValue, minValue, maxValue);
		}

		public SearchDelayField(Double initValue) {
			super(initValue);
		}

		@Override
		protected StringConverter<Number> stringConverter() {
			return new StringConverter<Number>() {
				@Override
				public String toString(Number object) {
					if (object == null) {
						return "0";
					}
					return String.valueOf(object.longValue());
				}

				@Override
				public Number fromString(String string) {
					return normolize(Double.parseDouble(string));

				}
			};
		}

		@Override
		protected Boolean inputTextFilter(String newInput) {
			return newInput.matches("[0-9]+");
		}

		@Override
		protected Boolean rezultTextFilter(String newText) {
			return true;
		}
		
	}
	
	final VBox root;
	final TextField titleTextField;
	final SearchDelayField searchDelayField;
	final Slider searchDelaySlider;
	final TextArea previewTextArea;
	final Button selectTableDataButton;
	final Button editTableDataButton;
	final Button createTableDataButton;
	
	
	private final double INIT_SEARCH_DELAY = 0;
	private final double MIN_SEARCH_DELAY = 0;
	private final double MAX_SEARCH_DELAY = 10000;
	
	TableViewerEditDesigner() {
		root = new VBox();
		root.setPrefSize(500d, 400d);
		root.setPadding(new Insets(5d));
		
		titleTextField = FormDesigners.addTo(root, new TextField());
		titleTextField.minWidthProperty().bind(root.widthProperty().subtract(10d));
		
		searchDelayField = new SearchDelayField(INIT_SEARCH_DELAY, MIN_SEARCH_DELAY, MAX_SEARCH_DELAY);
		searchDelayField.setMaxWidth(60d);
		
		searchDelaySlider = new Slider();
		searchDelaySlider.setValue(INIT_SEARCH_DELAY);
		searchDelaySlider.setMin(MIN_SEARCH_DELAY);
		searchDelaySlider.setMax(MAX_SEARCH_DELAY);
		
		Label searchDelayLabel = new Label("Search Delay");
		searchDelayLabel.setGraphic(searchDelayField);
		searchDelayLabel.setContentDisplay(ContentDisplay.RIGHT);
		HBox.setMargin(searchDelayLabel, new Insets(0, 15d, 0, 0));
		
		HBox searchDelayhbox = FormDesigners.addTo(root, new HBox(searchDelayLabel, searchDelaySlider));
		searchDelayhbox.setAlignment(Pos.CENTER_LEFT);
		VBox.setMargin(searchDelayhbox, new Insets(20d, 0, 0, 0));
		
		previewTextArea = FormDesigners.addTo(root, new TextArea());
		VBox.setMargin(previewTextArea, new Insets(20d, 0, 0, 0));
		previewTextArea.minWidthProperty().bind(root.widthProperty().subtract(10d));
		previewTextArea.minHeightProperty().bind(root.heightProperty().subtract(150d));
		
		HBox buttonBarHBox = FormDesigners.addTo(root, new HBox());
		VBox.setMargin(buttonBarHBox, new Insets(15d, 0, 0, 0));
		
		ButtonBar leftButtonBar = FormDesigners.addTo(buttonBarHBox, new ButtonBar());
		leftButtonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
		HBox.setHgrow(leftButtonBar, Priority.ALWAYS);
		
		ButtonBar rightButtonBar = FormDesigners.addTo(buttonBarHBox, new ButtonBar());
		HBox.setHgrow(rightButtonBar, Priority.ALWAYS);
		
		selectTableDataButton = FormDesigners.addTo(leftButtonBar, new Button("Select"));
		
		editTableDataButton = FormDesigners.addTo(leftButtonBar, new Button("Edit"));
		
		createTableDataButton = FormDesigners.addTo(rightButtonBar, new Button("Create"));
		
		
	}


	@Override
	protected Parent root() {
		return root;
	}

}
