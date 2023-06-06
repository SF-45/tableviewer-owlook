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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionEntityDao;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.components.logger.LogLevel;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.LoggerMessage;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends JAXBEntity {

	private StringProperty title = new SimpleStringProperty("");
	private StringProperty tableDataConnection = new SimpleStringProperty();
	private ObservableList<String> tableDataFilters = FXCollections.observableArrayList();;
	private ObservableList<String> tableDataViews = FXCollections.observableArrayList();;
	private ObservableList<ActionDecorator> actionDecorators = FXCollections.observableArrayList();;

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

	@XmlElement(name = "TableDataConnection")
	public String getTableDataConnection() {
		return tableDataConnectionProperty().get();
	}

	public void setTableDataConnection(String tableDataConnection) {
		tableDataConnectionProperty().set(tableDataConnection);
	}

	public StringProperty tableDataConnectionProperty() {
		return tableDataConnection;
	}

	@XmlElementWrapper(name = "TableDataFilters")
	@XmlElement(name = "Filter")
	public List<String> getTableDataFilters() {
		return tableDataFiltersProperty();
	}

	public ObservableList<String> tableDataFiltersProperty() {
		return tableDataFilters;
	}

	@XmlElementWrapper(name = "TableDataViews")
	@XmlElement(name = "View")
	public List<String> getTableDataViews() {
		return tableDataViewsProperty();
	}

	public ObservableList<String> tableDataViewsProperty() {
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
		return Arrays.asList(title, tableDataConnection, tableDataFilters, tableDataViews, actionDecorators);
	}

	@Override
	public String getExtension() {
		return ".wtable";
	}

	@Override
	public void initialize() {
		EntityLoader.INSTANCE.addDeleteChangeListener(entity -> {
			if (entity.getClass().equals(TableDataFilter.class)) {
				getTableDataFilters().remove(entity.getFileName());
			} else if (entity.getClass().equals(TableDataView.class)) {
				getTableDataViews().remove(entity.getFileName());
			} else if (entity.getClass().equals(ActionEntity.class)) {
				getActionDecorators()
						.removeIf(actionDecrator -> actionDecrator.getAction().equals(entity.getFileName()));
			}
		});

	}

	@Override
	public void validate() {
		validateFilters();
		validateViews();
		validateActions();
		validateData();
	}

	private void validateViews() {
		checkExist(getTableDataViews(), TableDataView.class, "TableViewerValidation: " + getFileName(),
				"View not exist");
	}

	private void validateFilters() {
		checkExist(getTableDataFilters(), TableDataFilter.class, "TableViewerValidation: " + getFileName(),
				"Filter not exist");
	}

	private void validateActions() {
		for (int i = 0; i < getActionDecorators().size(); i++) {
			String fileName = getActionDecorators().get(i).getAction();
			if (!ActionEntityDao.existActionEntity(fileName)) {
				LoggerMessage loggerMessage = new LoggerMessage(LogLevel.WARNING);
				loggerMessage.setName("TableViewerValidation: " + getFileName());
				loggerMessage.setMessage("Action not exist" + ": " + fileName);
				ErrorLogger.registerMessage(loggerMessage);
				getActionDecorators().remove(i);
				i--;
			}
		}
	}

	private void validateData() {
		if (getTableDataConnection() != null) {
			if (!TableDataDao.existTableData(getTableDataConnection())) {
				LoggerMessage loggerMessage = new LoggerMessage(LogLevel.WARNING);
				loggerMessage.setName("TableViewerValidation: " + getFileName());
				loggerMessage.setMessage("TableData not exist " + getTableDataConnection());
				ErrorLogger.registerMessage(loggerMessage);
				setTableDataConnection(null);
			}
		}
	}

	private void checkExist(List<String> files, Class<? extends JAXBEntity> target, String name, String message) {
		for (int i = 0; i < files.size(); i++) {
			String fileName = files.get(i);
			if (!EntityLoader.INSTANCE.entityExist(fileName, target)) {
				LoggerMessage loggerMessage = new LoggerMessage(LogLevel.WARNING);
				loggerMessage.setName(name);
				loggerMessage.setMessage(message + ": " + fileName);
				ErrorLogger.registerMessage(loggerMessage);
				files.remove(i);
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
		builder.append("TableData: " + getTableDataConnection() + "\n\n");

		builder.append("Filters:\n");
		getTableDataFilters().forEach(s -> {
			builder.append("\t" + s + "\n");
		});

		builder.append("\n");

		builder.append("Views:\n");
		getTableDataViews().forEach(s -> {
			builder.append("\t" + s + "\n");
		});

		builder.append("\n");

		builder.append("Actions:\n");
		getActionDecorators().forEach(action -> {
			builder.append("\t" + action.getAction());
			builder.append(
					" [" + action.getTags().stream().map(p -> p.get()).collect(Collectors.joining(", ")) + "]\n");

		});
		return builder.toString();
	}

	@Override
	public void syncWith(JAXBEntity entity) {
		if (!(entity instanceof TableViewer)) {
			return;
		}

		TableViewer tv = (TableViewer) entity;

		setTitle(tv.getTitle());
		setTableDataConnection(tv.getTableDataConnection());
		getTableDataFilters().clear();
		getTableDataFilters().addAll(tv.getTableDataFilters());

		getTableDataViews().clear();
		getTableDataViews().addAll(tv.getTableDataViews());

		getActionDecorators().clear();
		getActionDecorators().addAll(tv.getActionDecorators());
	}

}
