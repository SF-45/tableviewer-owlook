package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.owlery.InternalOwlDependence;
import space.sadfox.owlook.owlery.OwlDependence;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.owlery.OwlReferenceAdapter;
import space.sadfox.owlook.owlery.OwlReferenceList;
import space.sadfox.owlook.owlery.OwlReferenceListAdapter;
import space.sadfox.owlook.owlery.OwleryCreatable;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends OwlEntity implements Controllable, OwleryCreatable {

  private final LongProperty searchDelay = new SimpleLongProperty(0);
  private OwlReference<TableData> tableData = new OwlReference<>(TableData.class);
  private OwlReferenceList<TableDataFilter> tableDataFilters =
      new OwlReferenceList<>(TableDataFilter.class);
  private OwlReferenceList<TableDataView> tableDataViews =
      new OwlReferenceList<>(TableDataView.class);
  private final ObservableList<ActionDecorator> actionDecorators =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

  public long getSearchDelay() {
    return searchDelayProperty().get();
  }

  public void setSearchDelay(long millis) {
    searchDelayProperty().set(millis);
  }

  public LongProperty searchDelayProperty() {
    return searchDelay;
  }

  @OwlDependence
  @XmlJavaTypeAdapter(OwlReferenceAdapter.class)
  public OwlReference<TableData> getTableDataRef() {
    return tableData;
  }

  @SuppressWarnings("unused")
  private void setTableDataRef(OwlReference<TableData> tableDataRef) {
    this.tableData = tableDataRef;
  }

  @OwlDependence
  @XmlJavaTypeAdapter(OwlReferenceListAdapter.class)
  public OwlReferenceList<TableDataFilter> getTableDataFilters() {
    return tableDataFilters;
  }

  @SuppressWarnings("unused")
  private void setTableDataFilters(OwlReferenceList<TableDataFilter> tableDataFilters) {
    this.tableDataFilters = tableDataFilters;
  }

  @OwlDependence
  @XmlJavaTypeAdapter(OwlReferenceListAdapter.class)
  public OwlReferenceList<TableDataView> getTableDataViews() {
    return tableDataViews;
  }

  @SuppressWarnings("unused")
  private void setTableDataViews(OwlReferenceList<TableDataView> tableDataViews) {
    this.tableDataViews = tableDataViews;
  }

  @XmlElementWrapper(name = "actions")
  @XmlElement(name = "action")
  @InternalOwlDependence
  public List<ActionDecorator> getActionDecorators() {
    return actionDecoratorsProperty();
  }

  public ObservableList<ActionDecorator> actionDecoratorsProperty() {
    return actionDecorators;
  }

  @Override
  public List<Object> getProperties() {
    return Arrays.asList(tableData, tableDataFilters, tableDataViews, actionDecorators,
        searchDelay);
  }

  @Override
  public void initialize() {
    for (int i = 0; i < getActionDecorators().size(); i++) {
      if (getActionDecorators().get(i).getActionOwlRef().isEmpty()) {
        getActionDecorators().remove(i);
        i--;
      }
    }

    OwlLoader.INSTANCE.addDeleteOwlListener(owl -> {
      if (owl.entityClass().equals(ActionEntity.class)) {
        for (int i = 0; i < getActionDecorators().size(); i++) {
          ActionDecorator ad = getActionDecorators().get(i);
          if (ad.getActionOwlRef().isEmpty()) {
            getActionDecorators().remove(i);
            i--;
          }
        }
      }
    });
  }

  @Override
  public Controller getController() throws IOException {
    return new TableViewerEditController((Owl<TableViewer>) thisOwl());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("TableViewer: " + thisOwl().head().getTitle() + "\n");
    if (getTableDataRef().isPresent()) {
      builder.append("TableData: " + getTableDataRef().get().head().getTitle() + "\n");
    } else {
      builder.append("TableData: Indefined\n\n");
    }
    builder.append("Search Delay: " + searchDelay.get() + "ms\n\n");

    builder.append("Filters:\n");
    getTableDataFilters().forEach(s -> {
      builder.append("\t" + s.head().getTitle() + "\n");
    });

    builder.append("\n");

    builder.append("Views:\n");
    getTableDataViews().forEach(s -> {
      builder.append("\t" + s.head().getTitle() + "\n");
    });

    builder.append("\n");

    builder.append("Actions:\n");
    getActionDecorators().forEach(action -> {
      builder.append("\t" + action.getActionOwlRef().get().head().getTitle());
      builder.append(
          " [" + action.getTags().stream().map(p -> p).collect(Collectors.joining(", ")) + "]\n");

    });
    return builder.toString();
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof TableViewer)) {
      return;
    }

    TableViewer targetTableView = (TableViewer) entity;

    if (targetTableView.getTableDataRef().isPresent()) {
      getTableDataRef().set(targetTableView.getTableDataRef().get());
    }
    getTableDataFilters().clear();
    getTableDataFilters().addAll(targetTableView.getTableDataFilters());

    getTableDataViews().clear();
    getTableDataViews().addAll(targetTableView.getTableDataViews());

    getActionDecorators().clear();
    targetTableView.getActionDecorators().forEach(targetActionDecorator -> {
      ActionDecorator newActionDecorator = new ActionDecorator();
      newActionDecorator.getActionOwlRef().set(targetActionDecorator.getActionOwlRef().get());
      targetActionDecorator.getTags().forEach(newActionDecorator.getTags()::add);
      getActionDecorators().add(newActionDecorator);
    });

  }

  @Override
  public List<Owl<?>> getChildrenOwls() {
    List<Owl<?>> childOwls = new ArrayList<>();
    if (getTableDataRef().isPresent()) {
      childOwls.add(getTableDataRef().get());
    }
    childOwls.addAll(getTableDataFilters());
    childOwls.addAll(getTableDataViews());
    childOwls.addAll(getActionDecorators().stream().map(actionDec -> actionDec.getActionOwlRef())
        .map(OwlReference::get).collect(Collectors.toList()));

    return childOwls;
  }
}
