package space.sadfox.tableviewer.ui.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import space.sadfox.dataccess.action.ActionApi;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionEntityDao;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.TableViewerProvider;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;
import space.sadfox.tableviewer.ui.base.FileNamePicker;

/* TODO:
 * Провести рефакторинг имён переменных
 * 
 * */
public class ActionController extends Controller {

	@FXML
	private MenuButton menuNewAction;

	@FXML
	private RadioButton radioByNone;

	@FXML
	private RadioButton radioByProvider;

	@FXML
	private RadioButton radioByTag;

	@FXML
	private BorderPane root;

	@FXML
	private TextField serachTextBox;

	private ObservableList<ActionButton> actionButtons;
	private TableViewerTab tableViewerTab;
	private ActionAccordion actionAccordion;
	private ButtonList buttonList;
	private ToggleGroup toggleGroup;

	public ActionController(TableViewerTab tableViewerTab) throws IOException {
		super(TableViewerProvider.class.getResource("fxml/acion-pane.fxml"));
		this.tableViewerTab = tableViewerTab;

		actionAccordion = new ActionAccordion(tableViewerTab);
		actionAccordion.showByTag();
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
		toggleGroup = new ToggleGroup();
		actionButtons = FXCollections.observableArrayList();
		actionButtons.addListener((InvalidationListener) prop -> filtredAllActions());

		ObservableList<ActionDecorator> actionDecorators = tableViewerTab.getTableViewer().actionDecoratorsProperty();
		actionDecorators.forEach(this::addAction);
		actionDecorators.addListener((ListChangeListener<ActionDecorator>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(this::addAction);
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(this::deleteAction);
				}
			}
		});

		for (ActionApi actionProvider : ActionEntityDao.getActionProviders()) {
			MenuItem menuItem = new MenuItem(actionProvider.getModuleExtensionName());
			menuItem.setOnAction(event -> {
				createAction(actionProvider);
			});
			menuNewAction.getItems().add(menuItem);
		}
		
		

		initializ();

	}

	private void addAction(ActionDecorator actionDecorator) {
		actionButtons.add(new ActionButton(actionDecorator, tableViewerTab));
		actionAccordion.addAction(actionDecorator);
	}

	private void deleteAction(ActionDecorator actionDecorator) {
		for (int i = 0; i < actionButtons.size(); i++) {
			ActionButton actionButton = actionButtons.get(i);
			if (actionButton.getActionDecorator().equals(actionDecorator)) {
				actionButtons.remove(i);
				i--;
			}
		}
		actionAccordion.deleteAction(actionDecorator);
		filtredAllActions();
	}

	private void filtredAllActions() {
		buttonList.getChildren().clear();
		if (serachTextBox.getText().equals("")) {
			buttonList.addAllAndSort(actionButtons);
		} else {
			var filtredButtons = actionButtons.stream()
					.filter(but -> but.getActionEntity().getTitle().toLowerCase().contains(serachTextBox.getText().toLowerCase()))
					.collect(Collectors.toList());
			buttonList.addAllAndSort(filtredButtons);
		}

	}

	private void createAction(ActionApi actionProvider) {
		try {
			FileNamePicker picker = new FileNamePicker(ActionEntity.class);
			picker.setModality(Modality.APPLICATION_MODAL);
			picker.showAndWait();
			if (!picker.isCreate())
				return;

			ActionEntity actionEntity = ActionEntityDao.createActionEntity(picker.getFileName(), actionProvider);
			actionEntity.setTitle(picker.getFileName());
			ActionDecorator actionDecorator = new ActionDecorator();
			actionDecorator.setAction(actionEntity.getFileName());
			tableViewerTab.getTableViewer().getActionDecorators().add(actionDecorator);
			editAction(actionDecorator);

		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
	}

	private void editAction(ActionDecorator actionDecorator) {
		try {
			new EditActionController(actionDecorator).show();
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
	}

	private void initializ() {
		root.setCenter(actionAccordion);
		radioByTag.setToggleGroup(toggleGroup);
		radioByProvider.setToggleGroup(toggleGroup);
		radioByNone.setToggleGroup(toggleGroup);
		serachTextBox.textProperty().addListener((property, oldValue, newValue) -> {
			if (oldValue.equals(newValue))
				return;
			toggleGroup.selectToggle(radioByNone);
			filtredAllActions();
		});

		toggleGroup.selectedToggleProperty().addListener((property, oldValue, newValue) -> {
			if (newValue == radioByTag) {
				root.setCenter(actionAccordion);
				actionAccordion.showByTag();
			} else if (newValue == radioByProvider) {
				root.setCenter(actionAccordion);
				actionAccordion.showByProvider();
			} else {
				root.setCenter(buttonList);
			}
		});
	}

//	public ActionController(TableViewerTab tableViewerTab) {
//		this.tableViewerTab = tableViewerTab;
//		
//		tableViewerTab.getTableViewer().getActionDecorators().forEach(al -> {
//			getTabs().add(new ActionTab(al, tableViewerTab));
//		});
//	}

}
