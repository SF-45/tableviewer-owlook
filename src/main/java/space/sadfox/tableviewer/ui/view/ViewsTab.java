package space.sadfox.tableviewer.ui.view;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViews;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.ui.tools.OpenEntityDialog;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;

public class ViewsTab extends ButtonList {

	private ToggleGroup toggleGroup;
	private TableViewerTab tableViewerTab;

	public ViewsTab(TableViewerTab tableViewerTab) {

		this.tableViewerTab = tableViewerTab;

		toggleGroup = new ToggleGroup();
		getTableViewerTab().getTableViewer().getTableDataViews().forEach(this::addView);
		getTableViewerTab().getTableViewer().tableDataViewsProperty()
				.addListener((ListChangeListener<TableDataView>) change -> {
					while (change.next()) {
						if (change.wasAdded()) {
							change.getAddedSubList().forEach(view -> {
								int ind = getTableViewerTab().getTableViewer().getTableDataViews().indexOf(view);
								addView(ind, view);

							});
						}
						if (change.wasRemoved()) {
							change.getRemoved().forEach(view -> {
								deleteView(view);
							});
						}
					}
				});

		ContextMenu contextMenu = new ContextMenu();
		setContextMenu(contextMenu);

		MenuItem createView = new MenuItem("Create View");
		createView.setOnAction(event -> {
			TableDataView newView = TableDataViews.createTableDataView();
			if (newView == null)
				return;
			newView.setTitle("New View");
			getTableViewerTab().getTableViewer().getTableDataViews().add(newView);
			editView(newView);
		});
		contextMenu.getItems().add(createView);

		MenuItem open = new MenuItem("Open View");
		open.setOnAction(event -> {
			try {
				OpenEntityDialog<TableDataView> openDialog = new OpenEntityDialog<>(TableDataView.class,
						SelectionMode.MULTIPLE, getTableViewerTab().getTableViewer().getTableDataViews());
				openDialog.setModality(Modality.APPLICATION_MODAL);
				openDialog.showAndWait();
				if (openDialog.isOpened()) {
					openDialog.getOpenned().forEach(v -> {
						getTableViewerTab().getTableViewer().getTableDataViews().add(v);
					});
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(open);

	}

	private void addView(int ind, TableDataView view) {
		if (view == null)
			return;

		ViewToggleButton button = new ViewToggleButton(view, getTableViewerTab().getTableViewer());
		button.setToggleGroup(toggleGroup);
		button.setOnAction(event -> {
			getTableViewerTab().getTableDataViewTable().setTableDataView(view);
		});
		if (getChildren().size() == 0) {
			button.fire();
		}
		ContextMenu contextMenu = new ContextMenu();
		button.setContextMenu(contextMenu);

		MenuItem edit = new MenuItem("Edit View");
		edit.setOnAction(event -> {
			editView(view);
		});
		contextMenu.getItems().add(edit);

		MenuItem duplicate = new MenuItem("Duplicate View");
		duplicate.setOnAction(event -> {
			try {
				TableDataView newView = EntityLoader.INSTANCE.duplicateEntity(view);
				getTableViewerTab().getTableViewer().getTableDataViews().add(newView);
				editView(newView);
			} catch (JAXBException | IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(duplicate);

		MenuItem close = new MenuItem("Close View");
		close.setOnAction(event -> {
			getTableViewerTab().getTableViewer().getTableDataViews().remove(view);
		});
		contextMenu.getItems().add(close);

		MenuItem delete = new MenuItem("Delete View");
		delete.setOnAction(event -> {
			if (TableDataViews.deleteTableDataView(view)) {
				getTableViewerTab().getTableViewer().getTableDataViews().remove(view);
			}
		});
		contextMenu.getItems().add(delete);

		if (ind < 0)
			getChildren().add(button);
		else
			getChildren().add(ind, button);
	}

	private void addView(TableDataView view) {
		addView(-1, view);
	}

	private void deleteView(TableDataView view) {
		var btnList = getChildren();
		for (int i = 0; i < btnList.size(); i++) {
			Node node = btnList.get(i);
			if (node instanceof ViewToggleButton) {
				ViewToggleButton viewButton = (ViewToggleButton) node;
				if (viewButton.getView().equals(view)) {
					btnList.remove(i);
					return;
				}
			}
		}
	}

	private void editView(TableDataView view) {
		try {
			view.getConfigController(getTableViewerTab().getTableData()).show();
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		} catch (Nullable e) {
			try {
				view.getConfigController().show();
			} catch (IOException e1) {
				ErrorLogger.registerException(e1);
			}
		}
	}

	public TableViewerTab getTableViewerTab() {
		return tableViewerTab;
	}

}
