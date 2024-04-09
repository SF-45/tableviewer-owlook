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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataAdapter;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilterAdapter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViewAdapter;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends OwlEntity implements Controllable {

  private final LongProperty searchDelay = new SimpleLongProperty(0);
  private final ObjectProperty<Owl<TableData>> tableData = new SimpleObjectProperty<>();
  private final ObservableList<Owl<TableDataFilter>> tableDataFilters =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
  private final ObservableList<Owl<TableDataView>> tableDataViews =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
  private final ObservableList<ActionDecorator> actionDecorators =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

  @XmlElement(name = "searchDelay")
  public long getSearchDelay() {
    return searchDelayProperty().get();
  }

  public void setSearchDelay(long millis) {
    searchDelayProperty().set(millis);
  }

  public LongProperty searchDelayProperty() {
    return searchDelay;
  }

  @XmlElement(name = "tableData")
  @XmlJavaTypeAdapter(TableDataAdapter.class)
  public Owl<TableData> getTableData() {
    return tableDataProperty().get();
  }

  public Owl<TableData> getTableDataSafe() throws Nullable {
    if (tableDataProperty().get() == null) {
      throw new Nullable();
    }
    return tableDataProperty().get();
  }

  public void setTableData(Owl<TableData> tableData) {
    tableDataProperty().set(tableData);
  }

  public ObjectProperty<Owl<TableData>> tableDataProperty() {
    return tableData;
  }

  @XmlElementWrapper(name = "tableDataFilters")
  @XmlElement(name = "filter")
  @XmlJavaTypeAdapter(TableDataFilterAdapter.class)
  public List<Owl<TableDataFilter>> getTableDataFilters() {
    return tableDataFiltersProperty();
  }

  public ObservableList<Owl<TableDataFilter>> tableDataFiltersProperty() {
    return tableDataFilters;
  }

  @XmlElementWrapper(name = "tableDataViews")
  @XmlElement(name = "view")
  @XmlJavaTypeAdapter(TableDataViewAdapter.class)
  public List<Owl<TableDataView>> getTableDataViews() {
    return tableDataViewsProperty();
  }

  public ObservableList<Owl<TableDataView>> tableDataViewsProperty() {
    return tableDataViews;
  }

  @XmlElementWrapper(name = "actions")
  @XmlElement(name = "action")
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
      if (getActionDecorators().get(i).getActionOwl() == null) {
        getActionDecorators().remove(i);
        i--;
      }
    }
    for (int i = 0; i < getTableDataFilters().size(); i++) {
      if (getTableDataFilters().get(i) == null) {
        getTableDataFilters().remove(i);
        i--;
      }
    }
    for (int i = 0; i < getTableDataViews().size(); i++) {
      if (getTableDataViews().get(i) == null) {
        getTableDataViews().remove(i);
        i--;
      }
    }

    OwlLoader.INSTANCE.addDeleteOwlListener(owl -> {
      if (owl.entityClass().equals(TableDataFilter.class)) {
        getTableDataFilters().remove(owl);
      } else if (owl.entityClass().equals(TableDataView.class)) {
        getTableDataViews().remove(owl);
      } else if (owl.entityClass().equals(TableData.class)) {
        try {
          if (getTableDataSafe().equals(owl)) {
            setTableData(null);
          }
        } catch (Nullable e) {
        }
      } else if (owl.entityClass().equals(ActionEntity.class)) {
        for (int i = 0; i < getActionDecorators().size(); i++) {
          ActionDecorator ad = getActionDecorators().get(i);
          if (ad.getActionOwl().equals(owl)) {
            getActionDecorators().remove(i);
            i--;
          }
        }
      }
    });
  }

  @Override
  public Controller getController() throws IOException {
    return new TableViewerEditController((Owl<TableViewer>) getOwl());
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("TableViewer: " + getOwl().head().getTitle() + "\n");
    try {
      builder.append("TableData: " + getTableDataSafe().head().getTitle() + "\n");
    } catch (Nullable e) {
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
      builder.append("\t" + action.getActionOwl().head().getTitle());
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

    try {
      setTableData(targetTableView.getTableDataSafe());
    } catch (Nullable e) {
    }
    getTableDataFilters().clear();
    getTableDataFilters().addAll(targetTableView.getTableDataFilters());

    getTableDataViews().clear();
    getTableDataViews().addAll(targetTableView.getTableDataViews());

    getActionDecorators().clear();
    targetTableView.getActionDecorators().forEach(targetActionDecorator -> {
      ActionDecorator newActionDecorator = new ActionDecorator();
      newActionDecorator.setActionOwl(targetActionDecorator.getActionOwl());
      targetActionDecorator.getTags().forEach(newActionDecorator.getTags()::add);
      getActionDecorators().add(newActionDecorator);
    });

  }

  @Override
  public List<Owl<?>> getChildrenOwls() {
    List<Owl<?>> childOwls = new ArrayList<>();
    try {
      childOwls.add(getTableDataSafe());
    } catch (Nullable e) {
    }
    childOwls.addAll(getTableDataFilters());
    childOwls.addAll(getTableDataViews());
    childOwls.addAll(getActionDecorators().stream().map(actionDec -> actionDec.getActionOwl())
        .collect(Collectors.toList()));

    return childOwls;
  }
}
