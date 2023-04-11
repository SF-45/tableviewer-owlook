package space.sadfox.wstableviewer.ui.base;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ButtonList extends VBox {

	public interface Moveble {
		void moveTo(int ind);
	}

	private ContextMenu contextMenu;
	private ReadOnlyBooleanProperty hideProperty;
	private ChangeListener<Boolean> changeListener;

	private int draggedInd = 0;
	private Node draggedNode;

	public ButtonList() {
		getChildren().addListener((ListChangeListener<? super Node>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(node -> {
						if (node instanceof Parent) {
							((Region) node).prefWidthProperty().bind(this.widthProperty());
							VBox.setMargin(node, new Insets(5, 5, 0, 5));
						}
						registerMove(node);
					});
				}
			}
		});
		changeListener = (property, oldValue, newValue) -> {
			if (!newValue)
				contextMenu.hide();
		};
		this.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
			if (contextMenu != null)
				contextMenu.hide();
		});
	}

	public void setContextMenu(ContextMenu contextMenu) {
		this.contextMenu = contextMenu;
		this.setOnContextMenuRequested(event -> {
			contextMenu.show(this, event.getScreenX(), event.getScreenY());
		});
	}

	public void setContextMenuHideProperty(ReadOnlyBooleanProperty readOnlyBooleanProperty) {
		if (hideProperty != null)
			hideProperty.removeListener(changeListener);
		hideProperty = readOnlyBooleanProperty;
		hideProperty.addListener(changeListener);
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}

	private void registerMove(Node node) {
		if (!(node instanceof Moveble)) return;

		node.setOnDragDetected(dragEvent -> {
			draggedNode = node;
			draggedInd = getChildren().indexOf(node);
			System.out.println("DragDetect: " + draggedInd);
			node.startFullDrag();
			dragEvent.consume();
		});
		node.setOnMouseDragOver(dragEvent -> {
			if (draggedNode == null) return;
			int tempInd = getChildren().indexOf(node);
			if (tempInd == draggedInd)
				return;
			((Moveble) draggedNode).moveTo(tempInd);
			System.out.println("Move: temp=" + tempInd + " drag=" + draggedInd);
			draggedInd = tempInd;
			dragEvent.consume();
		});
	}

}
