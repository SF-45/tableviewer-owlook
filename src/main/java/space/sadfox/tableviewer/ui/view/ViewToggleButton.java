package space.sadfox.tableviewer.ui.view;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.ui.base.ButtonList.Moveble;

public class ViewToggleButton extends ToggleButton implements Moveble {

  private Owl<TableDataView> view;
  private Owl<TableViewer> tableViewer;



  public ViewToggleButton(Owl<TableDataView> view, Owl<TableViewer> tableViewer) {
    this.view = view;
    this.tableViewer = tableViewer;
    this.textProperty().bind(view.head().titleProperty());
    this.addEventHandler(ActionEvent.ACTION, event -> {
      if (!this.isSelected()) {
        this.setSelected(true);
      }
    });
  }

  public Owl<TableDataView> getView() {
    return view;
  }

  @Override
  public void moveTo(int ind) {
    if (!tableViewer.entity().getTableDataViews().contains(view))
      return;
    tableViewer.entity().getTableDataViews().remove(view);
    tableViewer.entity().getTableDataViews().add(ind, view);
  }

}
