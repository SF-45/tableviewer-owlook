package space.sadfox.tableviewer;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.jaxb.adapters.StringPropertyAdapter;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
public class ActionDecorator implements ChangeHistoryKeeping {
	
	private StringProperty action = new SimpleStringProperty();
	private ObservableList<StringProperty> tags = FXCollections.observableArrayList();
	
	@XmlAttribute(name = "name")
	public String getAction() {
		return action.get();
	}
	public void setAction(String action) {
		this.action.set(action);
	}
	public StringProperty actionProperty() {
		return action;
	}
	
	@XmlJavaTypeAdapter(StringPropertyAdapter.class)
	@XmlElement(name = "tag")
	public List<StringProperty> getTags() {
		return tags;
	}
	
	public ObservableList<StringProperty> tagsProperty() {
		return tags;
	}
	@Override
	public List<Object> getProperties() {
		return Arrays.asList(action, tags);
	}
	
	
	
	

}
