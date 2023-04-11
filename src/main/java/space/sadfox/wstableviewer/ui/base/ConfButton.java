package space.sadfox.wstableviewer.ui.base;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ConfButton extends Button {

	public ConfButton() {
		super();
		initializ();
	}

	public ConfButton(String text, Node graphic) {
		super(text, graphic);
		initializ();
	}

	public ConfButton(String text) {
		super(text);
		initializ();
	}
	
	private void initializ() {
		VBox.setMargin(this, new Insets(5, 5, 0, 5));
	}
	
	public void bindPrefWidth(Region region) {
		this.prefWidthProperty().bind(region.widthProperty());
	}
	

}
