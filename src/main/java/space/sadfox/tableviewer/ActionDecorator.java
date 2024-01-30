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
import space.sadfox.owlook.base.jaxb.ChangeHistoryKeeping;
import space.sadfox.owlook.base.owl.Owl;

@XmlAccessorType(XmlAccessType.NONE)
public class ActionDecorator implements ChangeHistoryKeeping {

  private ObjectProperty<Owl<ActionEntity>> actionOwl = new SimpleObjectProperty<>();
  private ObservableList<String> tags = FXCollections.observableArrayList();

  public ActionDecorator() {

  }

  public ActionDecorator(Owl<ActionEntity> actionOwl, String... tags) {
    setActionOwl(actionOwl);
    getTags().addAll(Arrays.asList(tags));
  }

  public ActionDecorator(Owl<ActionEntity> actionOwl, List<String> tags) {
    setActionOwl(actionOwl);
    getTags().addAll(tags);
  }

  @XmlJavaTypeAdapter(ActionEntityAdapter.class)
  @XmlAttribute(name = "name")
  public Owl<ActionEntity> getActionOwl() {
    return actionOwl.get();
  }

  public void setActionOwl(Owl<ActionEntity> actionOwl) {
    this.actionOwl.set(actionOwl);
  }

  public ObjectProperty<Owl<ActionEntity>> actionOwlProperty() {
    return actionOwl;
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
    return Arrays.asList(actionOwl, tags);
  }



}
