package space.sadfox.tableviewer;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableData.DataUpdateListener;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.owlery.OwleryOpenDialog;
import space.sadfox.owlook.ui.base.DesignController;
import space.sadfox.owlook.utils.Owlook;

public class TableViewerEditController extends DesignController<TableViewerEditDesigner> {

  private final Owl<TableViewer> tableViewer;

  public TableViewerEditController(Owl<TableViewer> tableViewer) {
    super(new TableViewerEditDesigner());
    this.tableViewer = tableViewer;
    stageTitle.bind(Bindings.concat("Edit TableViewer [", tableViewer.head().titleProperty(), "]"));

    DESIGN.titleTextField.setText(tableViewer.head().getTitle());
    DESIGN.titleTextField.textProperty().bindBidirectional(tableViewer.head().titleProperty());

    DataUpdateListener dataUpdateListener = () -> refreshTableData();

    tableViewer.entity().getTableDataRef().addListener((property, oldValue, newValue) -> {
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
        Owl<TableData> newTableData = OwlLoader.INSTANCE.createOwl(TableData.class);
        tableViewer.entity().getTableDataRef().set(newTableData);
      } catch (Exception e) {
        Owlook.registerException(e);
      }
    });

    DESIGN.editTableDataButton.setOnAction(event -> {
      OwlReference<TableData> tableDataRef = tableViewer.entity().getTableDataRef();
      if (tableDataRef.isPresent()) {
        try {
          tableDataRef.get().entity().getController().show();
        } catch (IOException e) {
          Owlook.registerException(e);
        }
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
        if (tableViewer.entity().getTableDataRef().isPresent()) {
          openDialog.setAlredyOpenedOwls(tableViewer.entity().getTableDataRef().get());
        }

        openDialog.showAndWait(Modality.APPLICATION_MODAL);

        if (openDialog.isOpened() && openDialog.getOpenedOwls().size() > 0) {
          tableViewer.entity().getTableDataRef().set(openDialog.getOpenedOwls().get(0));
        }
      } catch (ReflectiveOperationException e) {
        Owlook.registerException(e);
      }
    });
  }

  private void refreshTableData() {
    OwlReference<TableData> tableDataRef = tableViewer.entity().getTableDataRef();
    if (tableDataRef.isPresent()) {
      DESIGN.previewTextArea.setText(tableDataRef.get().toString());
    }

  }

}
