package space.sadfox.tableviewer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionEntityAdapter;
import space.sadfox.owlook.jaxb.adapters.StringPropertyAdapter;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
public class ActionDecorator implements ChangeHistoryKeeping {
	
	private ObjectProperty<ActionEntity> action = new SimpleObjectProperty<>();
	private ObservableList<StringProperty> tags = FXCollections.observableArrayList();
	
	public ActionDecorator() {
		
	}
	
	public ActionDecorator(ActionEntity actionEntity, String ... tags) {
		setAction(actionEntity);
		getTags().addAll(Arrays.asList(tags).stream().map(SimpleStringProperty::new).collect(Collectors.toList()));
	}
	
	public ActionDecorator(ActionEntity actionEntity, List<StringProperty> tags) {
		setAction(actionEntity);
		getTags().addAll(tags);
	}
	@XmlJavaTypeAdapter(ActionEntityAdapter.class)
	@XmlAttribute(name = "name")
	public ActionEntity getAction() {
		return action.get();
	}
	public void setAction(ActionEntity action) {
		this.action.set(action);
	}
	public ObjectProperty<ActionEntity> actionProperty() {
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
