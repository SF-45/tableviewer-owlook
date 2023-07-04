package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataAdapter;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilterAdapter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViewAdapter;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends JAXBEntity {

	private final StringProperty title = new SimpleStringProperty("");
	private final ObjectProperty<TableData> tableData = new SimpleObjectProperty<>();
	private final ObservableList<TableDataFilter> tableDataFilters = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	private final ObservableList<TableDataView> tableDataViews = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	private final ObservableList<ActionDecorator> actionDecorators = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
	
	@XmlAttribute(name = "title")
	public String getTitle() {
		return titleProperty().get();
	}

	public void setTitle(String name) {
		titleProperty().set(name);
	}

	public StringProperty titleProperty() {
		return title;
	}

	@XmlElement(name = "TableData")
	@XmlJavaTypeAdapter(TableDataAdapter.class)
	public TableData getTableData() {
		return tableDataProperty().get();
	}
	
	public TableData getTableDataSafe() throws Nullable {
		if (tableDataProperty().get() == null) {
			throw new Nullable();
		}
		return tableDataProperty().get();
	}

	public void setTableData(TableData tableData) {
		tableDataProperty().set(tableData);
	}

	public ObjectProperty<TableData> tableDataProperty() {
		return tableData;
	}

	@XmlElementWrapper(name = "TableDataFilters")
	@XmlElement(name = "Filter")
	@XmlJavaTypeAdapter(TableDataFilterAdapter.class)
	public List<TableDataFilter> getTableDataFilters() {
		return tableDataFiltersProperty();
	}

	public ObservableList<TableDataFilter> tableDataFiltersProperty() {
		return tableDataFilters;
	}

	@XmlElementWrapper(name = "TableDataViews")
	@XmlElement(name = "View")
	@XmlJavaTypeAdapter(TableDataViewAdapter.class)
	public List<TableDataView> getTableDataViews() {
		return tableDataViewsProperty();
	}

	public ObservableList<TableDataView> tableDataViewsProperty() {
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
		return Arrays.asList(title, tableData, tableDataFilters, tableDataViews, actionDecorators);
	}

	@Override
	public String getExtension() {
		return ".wtable";
	}

	@Override
	public void initialize() {
		EntityLoader.INSTANCE.addDeleteChangeListener(entity -> {
			if (entity.getClass().equals(TableDataFilter.class)) {
				getTableDataFilters().remove(entity);
			} else if (entity.getClass().equals(TableDataView.class)) {
				getTableDataViews().remove(entity);
			} else if (entity.getClass().equals(ActionEntity.class)) {
				getActionDecorators()
						.removeIf(actionDecrator -> actionDecrator.getAction().equals(entity));
			} else if (entity.getClass().equals(TableData.class)) {
				setTableData(null);
			}
		});
		

	}

	@Override
	public void validate() {
		for (int i = 0; i < getActionDecorators().size(); i++) {
			if (getActionDecorators().get(i).getAction() == null) {
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
		
	}


	@Override
	public Controller getConfigController() throws IOException {
		return new TableViewerEditController(this);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TableViewer: " + getTitle() + "\n");
		try {
			builder.append("TableData: " + getTableDataSafe().getTitle() + "\n\n");
		} catch (Nullable e) {
			builder.append("TableData: Indefined\n\n");
		}

		builder.append("Filters:\n");
		getTableDataFilters().forEach(s -> {
			builder.append("\t" + s.getTitle() + "\n");
		});

		builder.append("\n");

		builder.append("Views:\n");
		getTableDataViews().forEach(s -> {
			builder.append("\t" + s.getTitle() + "\n");
		});

		builder.append("\n");

		builder.append("Actions:\n");
		getActionDecorators().forEach(action -> {
			builder.append("\t" + action.getAction().getTitle());
			builder.append(
					" [" + action.getTags().stream().map(p -> p).collect(Collectors.joining(", ")) + "]\n");

		});
		return builder.toString();
	}

	@Override
	public void syncWith(JAXBEntity entity) {
		if (!(entity instanceof TableViewer)) {
			return;
		}

		TableViewer targetTableView = (TableViewer) entity;

		setTitle(targetTableView.getTitle());
		try {
			setTableData(targetTableView.getTableDataSafe());
		} catch (Nullable e) {}
		getTableDataFilters().clear();
		getTableDataFilters().addAll(targetTableView.getTableDataFilters());

		getTableDataViews().clear();
		getTableDataViews().addAll(targetTableView.getTableDataViews());

		getActionDecorators().clear();
		targetTableView.getActionDecorators().forEach(targetActionDecorator -> {
			ActionDecorator newActionDecorator = new ActionDecorator();
			newActionDecorator.setAction(targetActionDecorator.getAction());
			targetActionDecorator.getTags().forEach(newActionDecorator.getTags()::add);
			getActionDecorators().add(newActionDecorator);
		});
		
	}

}
