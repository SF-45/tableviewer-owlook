package space.sadfox.tableviewer;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.components.logger.LogLevel;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.LoggerMessage;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends JAXBEntity {

	private StringProperty title = new SimpleStringProperty();
	private ObjectProperty<String> tableDataConnection = new SimpleObjectProperty<>();
	private ObservableList<String> tableDataFilters = FXCollections.observableArrayList();
	private ObservableList<String> tableDataViews = FXCollections.observableArrayList();
	private ObservableList<ActionDecorator> actions = FXCollections.observableArrayList();

	@XmlAttribute(name = "title")
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String name) {
		this.title.set(name);
	}

	public StringProperty titleProperty() {
		return title;
	}

	@XmlElement(name = "TableDataConnection")
	public String getTableDataConnection() {
		return tableDataConnection.get();
	}

	public void setTableDataConnection(String tableDataConnection) {
		this.tableDataConnection.set(tableDataConnection);
	}

	public ObjectProperty<String> tableDataConnectionProperty() {
		return tableDataConnection;
	}

	@XmlElementWrapper(name = "TableDataFilters")
	@XmlElement(name = "Filter")
	public List<String> getTableDataFilters() {
		return tableDataFilters;
	}

	public ObservableList<String> tableDataFiltersProperty() {
		return tableDataFilters;
	}

	@XmlElementWrapper(name = "TableDataViews")
	@XmlElement(name = "View")
	public List<String> getTableDataViews() {
		return tableDataViews;
	}

	public ObservableList<String> tableDataViewsProperty() {
		return tableDataViews;
	}

	@XmlElementWrapper(name = "actions")
	@XmlElement(name = "action")
	public List<ActionDecorator> getActionDecorators() {
		return actions;
	}

	public ObservableList<ActionDecorator> actionDecoratorsProperty() {
		return actions;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, tableDataConnection, tableDataFilters, tableDataViews, actions);
	}

	@Override
	public String getExtension() {
		return ".wtable";
	}

	@Override
	public void initialize() {

	}

	@Override
	public boolean validate() {
		String name = "TableViewerValidation: " + getFileName();
		String message = "Filter not exist";
		checkExist(getTableDataFilters(), TableDataFilter.class, name, message);

		message = "View not exist";
		checkExist(getTableDataViews(), TableDataView.class, name, message);

		message = "Action not exist";
		EntityLoader loader = new EntityLoader();
		for (int i = 0; i < getActionDecorators().size(); i++) {
			String fileName = getActionDecorators().get(i).getAction();
			if (!loader.entityExist(fileName, ActionEntity.class)) {
				LoggerMessage loggerMessage = new LoggerMessage(LogLevel.WARNING);
				loggerMessage.setName(name);
				loggerMessage.setMessage(message + ": " + fileName);
				ErrorLogger.registerMessage(loggerMessage);
				getActionDecorators().remove(i);
				i--;
			}
		}
		return true;
	}

	private void checkExist(List<String> files, Class<? extends JAXBEntity> target, String name, String message) {
		EntityLoader loader = new EntityLoader();
		for (int i = 0; i < files.size(); i++) {
			String fileName = files.get(i);
			if (!loader.entityExist(fileName, target)) {
				LoggerMessage loggerMessage = new LoggerMessage(LogLevel.WARNING);
				loggerMessage.setName(name);
				loggerMessage.setMessage(message + ": " + fileName);
				ErrorLogger.registerMessage(loggerMessage);
				files.remove(i);
				i--;
			}
		}
	}

}
