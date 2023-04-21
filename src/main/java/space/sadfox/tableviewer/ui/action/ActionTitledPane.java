package space.sadfox.tableviewer.ui.action;

import javafx.scene.Node;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TitledPane;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;

public class ActionTitledPane extends TitledPane {

	private ButtonList buttonList;
	private TableViewerTab tableViewerTab;

	public ActionTitledPane(String tag, TableViewerTab tableViewerTab) {
		this.tableViewerTab = tableViewerTab;
		buttonList = new ButtonList();
		buttonList.setSorted((node1, node2) -> {
			if (node1 instanceof ActionButton && node2 instanceof ActionButton) {
				String buttonName1 = ((ActionButton) node1).getActionEntity().getTitle();
				String buttonName2 = ((ActionButton) node2).getActionEntity().getTitle();
				return buttonName1.compareToIgnoreCase(buttonName2);
			} else {
				return -1;
			}
		});
		this.setContent(buttonList);
		this.setText(tag);
	}

	public void addAction(ActionDecorator actionDecorator) {
		ActionButton actionButton = new ActionButton(actionDecorator, tableViewerTab);
		TableViewSelectionModel<DataEntity> selection = tableViewerTab.getTableDataViewTable().getSelectionModel();
		actionButton.setOnAction(event -> {
			if (selection.isEmpty()) return;
			actionButton.getAction().run(selection.getSelectedItems().toArray(new DataEntity[0]));
		});
		buttonList.addAndSort(actionButton);
		
		
	}

	public void removeAction(ActionDecorator actionDecorator) {
		for (int i = 0; i < buttonList.getChildren().size(); i++) {
			Node node = buttonList.getChildren().get(i);
			if (!(node instanceof ActionButton)) continue;
			ActionButton actionButton = (ActionButton) node;
			if (actionButton.getActionDecorator().equals(actionDecorator)) {
				buttonList.getChildren().remove(i);
				i--;
			}
		}
	}

	public int size() {
		return buttonList.getChildren().size();
	}

	public boolean contains(ActionDecorator actionDecorator) {
		for (Node node : buttonList.getChildren()) {
			if (node instanceof ActionButton) {
				ActionButton actionButton = (ActionButton) node;
				if (actionButton.getActionDecorator().equals(actionDecorator)) {
					return true;
				}
			}
		}
		return false;
	}

}
