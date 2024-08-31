package space.sadfox.tableviewer;

import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.owlook.base.jaxb.ChangeHistoryKeeping;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlDependence;
import space.sadfox.owlook.owlery.OwlReference;
import space.sadfox.owlook.owlery.OwlReferenceAdapter;

@XmlAccessorType(XmlAccessType.NONE)
public class ActionDecorator implements ChangeHistoryKeeping {

  private OwlReference<ActionEntity> actionOwlRef = new OwlReference<>(ActionEntity.class);
  private ObservableList<String> tags = FXCollections.observableArrayList();

  public ActionDecorator() {

  }

  public ActionDecorator(Owl<ActionEntity> actionOwl, String... tags) {
    getActionOwlRef().set(actionOwl);
    getTags().addAll(Arrays.asList(tags));
  }

  public ActionDecorator(Owl<ActionEntity> actionOwl, List<String> tags) {
    getActionOwlRef().set(actionOwl);
    getTags().addAll(tags);
  }

  @OwlDependence
  @XmlJavaTypeAdapter(OwlReferenceAdapter.class)
  public OwlReference<ActionEntity> getActionOwlRef() {
    return actionOwlRef;
  }

  @SuppressWarnings("unused")
  private void setActionOwlRef(OwlReference<ActionEntity> actionOwlRef) {
    this.actionOwlRef = actionOwlRef;
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
    return Arrays.asList(actionOwlRef, tags);
  }



}
