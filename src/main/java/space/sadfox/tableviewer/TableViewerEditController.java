package space.sadfox.tableviewer;

import java.io.IOException;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableData.DataUpdateListener;
import space.sadfox.dataccess.dataccess.TableDatas;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.DesignController;
import space.sadfox.owlook.utils.Logger;
import space.sadfox.owlook.utils.Nullable;

public class TableViewerEditController extends DesignController<TableViewerEditDesigner> {

  private final TableViewer tableViewer;

  public TableViewerEditController(TableViewer tableViewer) {
    super(new TableViewerEditDesigner());
    this.tableViewer = tableViewer;

    stageTitle.bind(tableViewer.titleProperty());

    DESIGN.titleTextField.setText(getTableViewer().getTitle());
    DESIGN.titleTextField.textProperty().bindBidirectional(getTableViewer().titleProperty());

    DataUpdateListener dataUpdateListener = () -> refreshTableData();

    getTableViewer().tableDataProperty().addListener((property, oldValue, newValue) -> {
      refreshTableData();
      if (oldValue != null) {
        oldValue.entity().removeDataUpdateListener(dataUpdateListener);
      }
      if (newValue != null) {
        newValue.entity().addDataUpdateListener(dataUpdateListener);
      }
    });
    refreshTableData();

    DESIGN.createTableDataButton.setOnAction(event -> {
      try {
        Owl<TableData> newTableData = TableDatas.createTableDataOwl();
        getTableViewer().setTableData(newTableData);
      } catch (Exception e) {
        Logger.registerException(1, e);
      }
    });

    DESIGN.editTableDataButton.setOnAction(event -> {
      try {
        getTableViewer().getTableDataSafe().entity().getController().show();
      } catch (IOException e) {
        Logger.registerException(1, e);
      } catch (Nullable e) {
      }
    });

    DESIGN.searchDelaySlider.setValue(getTableViewer().getSearchDelay());
    DESIGN.searchDelaySlider.valueProperty()
        .bindBidirectional(getTableViewer().searchDelayProperty());

    DESIGN.searchDelayField.setValue(DESIGN.searchDelaySlider.getValue());
    DESIGN.searchDelaySlider.valueProperty()
        .bindBidirectional(DESIGN.searchDelayField.valueProperty());
  }

  private TableViewer getTableViewer() {
    return tableViewer;
  }

  private void refreshTableData() {
    try {
      DESIGN.previewTextArea.setText(getTableViewer().getTableDataSafe().toString());
    } catch (Nullable e) {
      DESIGN.previewTextArea.setText("");
    }

  }

}
