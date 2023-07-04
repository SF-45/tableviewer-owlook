package space.sadfox.tableviewer;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionEntityAdapter;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
public class ActionDecorator implements ChangeHistoryKeeping {
	
	private ObjectProperty<ActionEntity> action = new SimpleObjectProperty<>();
	private ObservableList<String> tags = FXCollections.observableArrayList();
	
	public ActionDecorator() {
		
	}
	
	public ActionDecorator(ActionEntity actionEntity, String ... tags) {
		setAction(actionEntity);
		getTags().addAll(Arrays.asList(tags));
	}
	
	public ActionDecorator(ActionEntity actionEntity, List<String> tags) {
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
	
	@XmlElement(name = "tag")
	public List<String> getTags() {
		return tags;
	}
	
	public ObservableList<String> tagsProperty() {
		return tags;
	}
	
	@Override
	public List<Object> getProperties() {
		return Arrays.asList(action, tags);
	}
	
	
	
	

}
