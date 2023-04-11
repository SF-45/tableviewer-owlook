package space.sadfox.wstableviewer.ui.view;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.wstableviewer.TableViewerUI;
import space.sadfox.wstableviewer.ui.base.FileNamePicker;
import space.sadfox.wstableviewer.ui.base.OpenEntityDialog;
import space.sadfox.wstableviewer.ui.base.ToolTabBase;
import space.sadfox.wstableviewer.ui.base.ViewToggleButton;
import space.sadfox.xmldataccess.filter.TableDataFilter;
import space.sadfox.xmldataccess.view.TableDataView;

public class ViewsTab extends ToolTabBase {
	
	private ToggleGroup toggleGroup;


	public ViewsTab(TableViewerUI tableViewerUI) {
		super(tableViewerUI, "Views");
		
		toggleGroup = new ToggleGroup();
		
		getTableViewer().getChangeHistory().addChangeListener(() -> getTableViewer().save());
		getTableViewerDao().getViews().forEach(this::addView);
		getTableViewer().tableDataViewsProperty().addListener((ListChangeListener<String>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(viewFileName -> {
						int ind = getTableViewer().getTableDataFilters().indexOf(viewFileName);
						addView(ind, getTableViewerDao().getView(viewFileName));
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(viewFileName -> {
						deleteView(viewFileName);
					});
				}
			}
		});
		
		ContextMenu contextMenu = new ContextMenu();
		getButtonList().setContextMenu(contextMenu);
		this.tabPaneProperty().addListener((property, oldValue, newValue) -> {
			getButtonList().setContextMenuHideProperty(newValue.focusedProperty());
		});
		
		MenuItem createView = new MenuItem("Create View");
		createView.setOnAction(event -> {
			try {
				FileNamePicker picker = new FileNamePicker(TableDataView.class);
				picker.setModality(Modality.APPLICATION_MODAL);
				picker.showAndWait();
				if (!picker.isCreate()) return;
				
				EntityLoader loader = new EntityLoader();
				
				try {
					TableDataView newView = loader.createEntity(picker.getFileName(), TableDataView.class);
					
					newView.setTitle(picker.getFileName());
					getTableViewer().getTableDataViews().add(newView.getFileName());
					editView(newView);
				} catch (JAXBException e) {
					ErrorLogger.registerException(e);
				}
				
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(createView);
		
		MenuItem open = new MenuItem("Open View");
		open.setOnAction(event -> {
			try {
				OpenEntityDialog<TableDataView> openDialog = new OpenEntityDialog<>(TableDataView.class, getTableViewerDao().getViews());
				openDialog.setModality(Modality.APPLICATION_MODAL);
				openDialog.showAndWait();
				if (openDialog.isOpened()) {
					getTableViewer().getTableDataViews().add(openDialog.getOpenned().getFileName());
				}
			} catch (IOException e) {
				ErrorLogger.registerException(e);
			}
		});
		contextMenu.getItems().add(open);
		
		
	}
	
	private void addView(int ind, TableDataView view) {
		if (view == null) return;
		
		ViewToggleButton button = new ViewToggleButton(view, getTableViewer());
		button.setToggleGroup(toggleGroup);
		button.setOnAction(event -> {
			getTableViewerUI().getTableDataViewTable().setTableDataView(view);
		});
		if (getButtonList().getChildren().size() == 0) {
			button.fire();
		}
		ContextMenu contextMenu = new ContextMenu();
		button.setContextMenu(contextMenu);
		
		MenuItem edit = new MenuItem("Edit View");
		edit.setOnAction(event -> {
			editView(view);
		});
		contextMenu.getItems().add(edit);
		
		MenuItem close = new MenuItem("Close View");
		close.setOnAction(event -> {
			getTableViewer().getTableDataViews().remove(view.getFileName());
		});
		contextMenu.getItems().add(close);
		
		MenuItem delete = new MenuItem("Delete View");
		delete.setOnAction(event -> {
			EntityLoader loader = new EntityLoader();
			if (loader.deleteEntity(view)) {
				getTableViewer().getTableDataViews().remove(view.getFileName());
			}
		});
		contextMenu.getItems().add(delete);
		
		if (ind < 0) getButtonList().getChildren().add(button);
		else getButtonList().getChildren().add(ind, button);
	}
	
	private void addView(TableDataView view) {
		addView(-1, view);
	}
	
	private void deleteView(String viewFileName) {
		var btnList = getButtonList().getChildren();
		for (int i = 0; i < btnList.size(); i++) {
			Node node = btnList.get(i);
			if (node instanceof ViewToggleButton) {
				ViewToggleButton viewButton = (ViewToggleButton) node;
				if (viewButton.getView().getFileName().equals(viewFileName)) {
					btnList.remove(i);
					return;
				}
			}
		}
	}
	
	private void editView(TableDataView view) {
		try {
			var controller = new EditViewController(view, getTableViewerDao().getTableData());
			controller.getStage().titleProperty().bind(view.titleProperty());
			controller.show();
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
	}
	
	
	

}
