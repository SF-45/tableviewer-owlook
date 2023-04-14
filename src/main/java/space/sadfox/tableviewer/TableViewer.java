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
import javafx.scene.Node;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.jaxb.PreLoadAction;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableViewer extends JAXBEntity  {
	
	private StringProperty title = new SimpleStringProperty();
	private ObjectProperty<String> tableDataConnection = new SimpleObjectProperty<>();
	private ObservableList<String> tableDataFilters = FXCollections.observableArrayList();
	private ObservableList<String> tableDataViews = FXCollections.observableArrayList();
	private ObservableList<String> commands = FXCollections.observableArrayList();
	
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
	
	@XmlElementWrapper(name = "CommandLists")
	@XmlElement(name = "CommandList")
	public List<String> getCommands() {
		return commands;
	}
	
	public ObservableList<String> commandsProperty() {
		return commands;
	}


	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, tableDataConnection, tableDataFilters, tableDataViews);
	}

	@Override
	public String getExtension() {
		return ".wtable";
	}

	@Override
	public Node getSimpleConfigNode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreLoadAction getPreLoadAction() {
		return null;
	}

}
