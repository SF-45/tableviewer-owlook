package space.sadfox.tableviewer;

import java.io.IOException;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableData.DataUpdateListener;
import space.sadfox.dataccess.dataccess.TableDatas;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwleryOpenDialog;
import space.sadfox.owlook.ui.base.DesignController;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.Nullable;

public class TableViewerEditController extends DesignController<TableViewerEditDesigner> {

  private final Owl<TableViewer> tableViewer;

  public TableViewerEditController(Owl<TableViewer> tableViewer) {
    super(new TableViewerEditDesigner());
    this.tableViewer = tableViewer;

    stageTitle.bind(tableViewer.head().titleProperty());

    DESIGN.titleTextField.setText(tableViewer.head().getTitle());
    DESIGN.titleTextField.textProperty().bindBidirectional(tableViewer.head().titleProperty());

    DataUpdateListener dataUpdateListener = () -> refreshTableData();

    tableViewer.entity().tableDataProperty().addListener((property, oldValue, newValue) -> {
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
        tableViewer.entity().setTableData(newTableData);
      } catch (Exception e) {
        Owlook.registerException(1, e);
      }
    });

    DESIGN.editTableDataButton.setOnAction(event -> {
      try {
        tableViewer.entity().getTableDataSafe().entity().getController().show();
      } catch (IOException e) {
        Owlook.registerException(1, e);
      } catch (Nullable e) {
      }
    });

    DESIGN.searchDelaySlider.setValue(tableViewer.entity().getSearchDelay());
    DESIGN.searchDelaySlider.valueProperty()
        .bindBidirectional(tableViewer.entity().searchDelayProperty());

    DESIGN.searchDelayField.setValue(DESIGN.searchDelaySlider.getValue());
    DESIGN.searchDelaySlider.valueProperty()
        .bindBidirectional(DESIGN.searchDelayField.valueProperty());

    DESIGN.selectTableDataButton.setOnAction(event -> {
      try {
        OwleryOpenDialog<TableData> openDialog = new OwleryOpenDialog<>(TableData.class);
        openDialog.setSelectionModel(SelectionMode.SINGLE);
        try {
          openDialog.setAlredyOpenedOwls(tableViewer.entity().getTableDataSafe());
        } catch (Nullable e) {
        }

        openDialog.showAndWait(Modality.APPLICATION_MODAL);

        if (openDialog.isOpened() && openDialog.getOpenedOwls().size() > 0) {
          tableViewer.entity().setTableData(openDialog.getOpenedOwls().get(0));
        }
      } catch (ReflectiveOperationException e) {
        Owlook.registerException(1, e);
      }
    });
  }

  private void refreshTableData() {
    try {
      DESIGN.previewTextArea.setText(tableViewer.entity().getTableDataSafe().toString());
    } catch (Nullable e) {
      DESIGN.previewTextArea.setText("");
    }

  }

}
