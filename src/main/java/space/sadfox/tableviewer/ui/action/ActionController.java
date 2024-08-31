package space.sadfox.tableviewer.ui.action;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionProvider;
import space.sadfox.dataccess.action.Actions;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwleryOpenDialog;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.ActionDecoratorsCollector;
import space.sadfox.tableviewer.TableViewerProvider;
import space.sadfox.tableviewer.TableViewers;
import space.sadfox.tableviewer.ui.TableViewerTab;
import space.sadfox.tableviewer.ui.base.ButtonList;
import space.sadfox.tableviewer.ui.base.GroupAccordion;

public class ActionController extends FXMLController {

  private class ActionDecoratorButtonList extends ButtonList {

    final ObservableList<ActionButton> buttons = FXCollections.observableArrayList();
    String currentFilter = "";

    ActionDecoratorButtonList() {
      init();
      getActionDecorators().forEach(this::createActionButton);
      getActionDecorators().addListener((ListChangeListener<ActionDecorator>) change -> {
        while (change.next()) {
          if (change.wasAdded()) {
            change.getAddedSubList().forEach(this::createActionButton);
          }
          if (change.wasRemoved()) {
            change.getRemoved().forEach(this::removeActionButton);
          }
        }
      });

    }

    void init() {
      setSortComparator((node1, node2) -> {
        if (node1 instanceof ActionButton && node2 instanceof ActionButton) {
          String buttonName1 = ((ActionButton) node1).getActionOwl().head().getTitle();
          String buttonName2 = ((ActionButton) node2).getActionOwl().head().getTitle();
          return buttonName1.compareToIgnoreCase(buttonName2);
        } else {
          return -1;
        }
      });
    }

    void createActionButton(ActionDecorator actionDecorator) {
      buttons.add(new ActionButton(actionDecorator, getTableViewerTab()));
      filtred();
    }

    void removeActionButton(ActionDecorator actionDecorator) {
      for (int i = 0; i < buttons.size(); i++) {
        ActionButton actionButton = buttons.get(i);
        if (actionButton.getActionDecorator().equals(actionDecorator)) {
          buttons.remove(i);
          i--;
        }
      }
      filtred();
    }

    void setFilter(String filter) {
      currentFilter = filter;
      filtred();
    }

    void filtred() {
      getChildren().clear();
      if (currentFilter.equals("")) {
        addAllAndSort(buttons);
      } else {
        var filtredButtons =
            buttons.stream().filter(but -> but.getActionOwl().head().getTitle().toLowerCase()
                .contains(serachTextBox.getText().toLowerCase())).collect(Collectors.toList());
        addAllAndSort(filtredButtons);
      }
    }

  }

  @FXML
  private MenuButton menuNewAction;

  @FXML
  private Button openAction;

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

  private TableViewerTab tableViewerTab;
  private ObservableList<ActionDecorator> actionDecorators;

  private final ToggleGroup toggleGroup = new ToggleGroup();

  private GroupAccordion<ActionDecorator, String> tagAccordion;
  private GroupAccordion<ActionDecorator, String> providerAccordion;
  private ActionDecoratorButtonList actionDecoratorButtonList;

  private final ActionDecoratorsCollector actionDecoratorsCollector;

  public ActionController(TableViewerTab tableViewerTab) throws IOException {
    super(TableViewerProvider.class.getResource("fxml/acion-pane.fxml"));
    this.tableViewerTab = tableViewerTab;
    actionDecoratorsCollector = new ActionDecoratorsCollector(tableViewerTab.getTableViewer());

    initializ();

  }

  private void initializ() {
    root.setCenter(tagAccordion);
    radioByTag.setToggleGroup(toggleGroup);
    radioByProvider.setToggleGroup(toggleGroup);
    radioByNone.setToggleGroup(toggleGroup);
    serachTextBox.textProperty().addListener((property, oldValue, newValue) -> {
      if (oldValue.equals(newValue))
        return;
      if (newValue != "") {
        toggleGroup.selectToggle(radioByNone);
      }
      getActionDecoratorButtonList().setFilter(newValue);
    });

    toggleGroup.selectedToggleProperty().addListener((property, oldValue, newValue) -> {

      if (newValue == radioByTag) {
        serachTextBox.clear();
        root.setCenter(getTagAccordion());
      } else if (newValue == radioByProvider) {
        serachTextBox.clear();
        root.setCenter(getProviderAccordion());
      } else {
        root.setCenter(getActionDecoratorButtonList());
      }
    });
    root.setCenter(getTagAccordion());

    for (ActionProvider actionProvider : Actions.getActionProviders()) {
      MenuItem menuItem = new MenuItem(actionProvider.getComponentName());
      menuItem.setOnAction(event -> {
        ActionDecorator newActionDecorator = new ActionDecorator();
        try {
          newActionDecorator.getActionOwlRef().set(Actions.createActionEntity(actionProvider));
          getTableViewerTab().getTableViewer().entity().getActionDecorators()
              .add(newActionDecorator);
          new EditActionController(newActionDecorator, tableViewerTab).show();
        } catch (Exception e) {
          Owlook.registerException(e);
        }

      });
      menuNewAction.getItems().add(menuItem);
    }

    openAction.setOnAction(event -> {
      try {
        OwleryOpenDialog<ActionEntity> openDialog = new OwleryOpenDialog<>(ActionEntity.class);
        openDialog.setSelectionModel(SelectionMode.MULTIPLE);
        List<Owl<ActionEntity>> alredyOpenedActions =
            TableViewers.getActionEntities(getTableViewerTab().getTableViewer());
        openDialog.setAlredyOpenedOwls(FXCollections.observableList(alredyOpenedActions));
        openDialog.showAndWait(Modality.APPLICATION_MODAL);
        if (openDialog.isOpened()) {
          for (Owl<ActionEntity> actionEntity : openDialog.getOpenedOwls()) {
            ActionDecorator openedActionDecorator = new ActionDecorator(actionEntity);
            getTableViewerTab().getTableViewer().entity().getActionDecorators()
                .add(openedActionDecorator);
          }
        }
      } catch (ReflectiveOperationException e) {
        Owlook.registerException(e);
      }
    });
  }

  private TableViewerTab getTableViewerTab() {
    return tableViewerTab;
  }

  private ObservableList<ActionDecorator> getActionDecorators() {
    if (actionDecorators == null) {
      actionDecorators = getTableViewerTab().getTableViewer().entity().actionDecoratorsProperty();
    }
    return actionDecorators;
  }

  private GroupAccordion<ActionDecorator, String> getTagAccordion() {
    if (tagAccordion == null) {
      tagAccordion = new GroupAccordion<>();
      tagAccordion.setItems(getActionDecorators());
      tagAccordion.setMatcher((item, crit) -> item.getTags().contains(crit));
      tagAccordion.setCriteria(actionDecoratorsCollector.getTags());
      tagAccordion.setButtonFactory(action -> new ActionButton(action, getTableViewerTab()));
      tagAccordion.setInternalItemСhangeNotifier(
          (item, listener) -> item.tagsProperty().addListener(listener));
      tagAccordion.setGroupNameFactory(s -> new SimpleStringProperty(s));
      tagAccordion.setSortComparator((node1, node2) -> {
        if (node1 instanceof ActionButton && node2 instanceof ActionButton) {
          String buttonName1 = ((ActionButton) node1).getActionOwl().head().getTitle();
          String buttonName2 = ((ActionButton) node2).getActionOwl().head().getTitle();
          return buttonName1.compareToIgnoreCase(buttonName2);
        } else {
          return -1;
        }
      });
    }
    return tagAccordion;
  }

  private GroupAccordion<ActionDecorator, String> getProviderAccordion() {
    if (providerAccordion == null) {
      providerAccordion = new GroupAccordion<>();
      providerAccordion.setItems(getActionDecorators());
      providerAccordion.setMatcher((item, crit) -> {
        Optional<ActionProvider> oProvider =
            item.getActionOwlRef().get().entity().getActionProviderSafe();
        if (oProvider.isPresent()) {
          return oProvider.get().getIdentifier().equals(crit);
        } else {
          return false;
        }
      });
      providerAccordion.setCriteria(actionDecoratorsCollector.getProviders());
      providerAccordion.setButtonFactory(action -> new ActionButton(action, getTableViewerTab()));
      providerAccordion.setGroupNameFactory(p -> new SimpleStringProperty(p));
      providerAccordion.setSortComparator((node1, node2) -> {
        if (node1 instanceof ActionButton && node2 instanceof ActionButton) {
          String buttonName1 = ((ActionButton) node1).getActionOwl().head().getTitle();
          String buttonName2 = ((ActionButton) node2).getActionOwl().head().getTitle();
          return buttonName1.compareToIgnoreCase(buttonName2);
        } else {
          return -1;
        }
      });

    }
    return providerAccordion;
  }

  private ActionDecoratorButtonList getActionDecoratorButtonList() {
    if (actionDecoratorButtonList == null) {
      actionDecoratorButtonList = new ActionDecoratorButtonList();
    }
    return actionDecoratorButtonList;
  }
}
