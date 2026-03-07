package space.sadfox.tableviewer;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.ui.base.ControllerException;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.owlook.utils.Nullable;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class TableViewerController extends FXMLController {

  @FXML
  private BorderPane actionsRoot;

  @FXML
  private ScrollPane filtersRoot;

  @FXML
  private MenuBar menuBar;

  @FXML
  private MenuItem openIndex;

  @FXML
  private Button searchButton;

  @FXML
  private ProgressBar searchDelay;

  @FXML
  private TextField searchField;

  @FXML
  private Label searchHistory;

  @FXML
  private MenuItem settings;

  @FXML
  private TabPane tableTabPane;

  @FXML
  private Menu tablesMenu;

  @FXML
  private ScrollPane viewsRoot;

  public TableViewerController() throws ControllerException {
    super(TableViewer.class.getResource("fxml/main-scene.fxml"));

    stageTitle.set("OwlookTV");

    MenuItem createTableViewer = new MenuItem("Create new");
    createTableViewer.setOnAction(event -> TableViewers.createTableViewer());
    tablesMenu.getItems().addAll(new SeparatorMenuItem(), createTableViewer);

    tableTabPane.getTabs().addListener((InvalidationListener) listner -> {
      if (tableTabPane.getTabs().size() == 0) {
        actionsRoot.setCenter(null);
        viewsRoot.setContent(null);
        filtersRoot.setContent(null);
        removeTVMenu();
      }
    });

    OwlLoader.INSTANCE.addCreateOwlListener(owl -> {
      if (owl.entityClass().equals(TableViewer.class)) {
        createTableViewerMenuItem((Owl<TableViewer>) owl);
      }
    });

    TableViewers.getTableViewers().forEach(this::createTableViewerMenuItem);

    // searchField.textProperty().addListener((property, oldValue, newValue) -> {
    // try {
    // getSelectedTableViewerTab().getTableDataViewTable().findAction(newValue);
    // } catch (Nullable e) {}
    // });

    searchField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
      switch (keyEvent.getCode()) {
        case ENTER:
          try {
            getSelectedTableViewerTab().getTableDataViewTable().nextFind();
            searchField.setText("");
          } catch (Nullable e) {
          }
          break;
        default:
          break;
      }
    });

    searchButton.setOnAction(event -> {
      try {
        getSelectedTableViewerTab().getTableDataViewTable().nextFind();
        searchField.setText("");
      } catch (Nullable e) {
      }
    });

    viewsRoot.contentProperty().addListener((property, oldValue, newValue) -> {
      if (oldValue != null || oldValue instanceof Parent) {
        ((Region) oldValue).prefWidthProperty().unbind();
        ((Region) oldValue).prefHeightProperty().unbind();
      }
      if (newValue != null || newValue instanceof Parent) {
        ((Region) newValue).prefWidthProperty().bind(viewsRoot.widthProperty().subtract(5));
        ((Region) newValue).prefHeightProperty().bind(viewsRoot.heightProperty().subtract(5));
      }
    });

    filtersRoot.contentProperty().addListener((property, oldValue, newValue) -> {
      if (oldValue != null || oldValue instanceof Parent) {
        ((Region) oldValue).prefWidthProperty().unbind();
        ((Region) oldValue).prefHeightProperty().unbind();
      }
      if (newValue != null || newValue instanceof Parent) {
        ((Region) newValue).prefWidthProperty().bind(filtersRoot.widthProperty().subtract(5));
        ((Region) newValue).prefHeightProperty().bind(filtersRoot.heightProperty().subtract(5));
      }
    });

  }

  private TableViewerTab getSelectedTableViewerTab() throws Nullable {
    Tab tab = tableTabPane.getSelectionModel().getSelectedItem();
    if (tab instanceof TableViewerTab) {
      return (TableViewerTab) tab;
    }
    throw new Nullable();
  }

  private void createTableViewerMenuItem(Owl<TableViewer> tableViewerOwl) {
    MenuItem tableMenuItem = new MenuItem(tableViewerOwl.head().getTitle());
    tableMenuItem.textProperty().bindBidirectional(tableViewerOwl.head().titleProperty());
    tableMenuItem.setOnAction(event -> {
      TableViewerTab tableViewerTab = new TableViewerTab(tableViewerOwl);

      OwlLoader.INSTANCE.addDeleteOwlListener(deleteOwl -> {
        if (deleteOwl == tableViewerOwl) {
          tableTabPane.getTabs().remove(tableViewerTab);
        }
        if (deleteOwl == tableViewerOwl && tablesMenu.getItems().contains(tableMenuItem)) {
          tableTabPane.getTabs().remove(tableViewerTab);
        }

      });

      tableViewerTab.setOnSelectionChanged(tabEvent -> {
        if (tableViewerTab.isSelected()) {
          actionsRoot.setCenter(tableViewerTab.getActionsNode());
          viewsRoot.setContent(tableViewerTab.getViewsNode());
          filtersRoot.setContent(tableViewerTab.getFiltersNode());

          removeTVMenu();
          menuBar.getMenus().add(tableViewerTab.getMenu());
          searchHistory.textProperty()
              .bind(tableViewerTab.getTableDataViewTable().searchTextHistoryProperty());
          searchDelay.progressProperty()
              .bind(tableViewerTab.getTableDataViewTable().getFindActionDelay().progressProperty());
          searchField.setText(tableViewerTab.getTableDataViewTable().getCurrentSearchText());
          searchField.textProperty().bindBidirectional(
              tableViewerTab.getTableDataViewTable().currentSearchTextProperty());
        } else {
          searchField.textProperty().unbindBidirectional(
              tableViewerTab.getTableDataViewTable().currentSearchTextProperty());
        }
      });
      tableTabPane.getTabs().add(tableViewerTab);
      tableTabPane.getSelectionModel().select(tableViewerTab);
    });

    int pos = tablesMenu.getItems().size() - 2;
    tablesMenu.getItems().add(pos, tableMenuItem);
  }

  private void removeTVMenu() {
    menuBar.getMenus().remove(4, menuBar.getMenus().size());
  }

}
