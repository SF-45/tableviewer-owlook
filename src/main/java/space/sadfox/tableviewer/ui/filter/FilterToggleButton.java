package space.sadfox.tableviewer.ui.filter;

import javafx.event.ActionEvent;
import javafx.scene.control.ToggleButton;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.tableviewer.TableViewer;
import space.sadfox.tableviewer.ui.base.ButtonList.Moveble;

public class FilterToggleButton extends ToggleButton implements Moveble {

  private Owl<TableDataFilter> filter;
  private Owl<TableViewer> tableViewerOwl;

  public FilterToggleButton(Owl<TableDataFilter> filter, Owl<TableViewer> tableViewerOwl) {
    this.filter = filter;
    this.tableViewerOwl = tableViewerOwl;
    this.textProperty().bind(filter.head().titleProperty());
    this.addEventHandler(ActionEvent.ACTION, event -> {
      if (!this.isSelected()) {
        this.setSelected(true);
      }
    });
  }

  public Owl<TableDataFilter> getFilter() {
    return filter;
  }

  @Override
  public void moveTo(int ind) {
    if (!tableViewerOwl.entity().getTableDataFilters().contains(filter))
      return;
    tableViewerOwl.entity().getTableDataFilters().remove(filter);
    tableViewerOwl.entity().getTableDataFilters().add(ind, filter);
  }



}
