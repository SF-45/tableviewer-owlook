package space.sadfox.tableviewer;

import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.action.ActionProvider;
import space.sadfox.owlook.base.owl.Owl;

public class ActionDecoratorsCollector {

  private final Owl<TableViewer> tableViewer;
  private static final String PROVIDER_NOT_FOUND = "unknown";

  private final ObservableList<String> actionTags =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
  private final ObservableList<String> actionProviders =
      FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

  private final ObservableList<String> roActionTags =
      FXCollections.unmodifiableObservableList(actionTags);
  private final ObservableList<String> roActionProviders =
      FXCollections.unmodifiableObservableList(actionProviders);

  private ListChangeListener<String> tagListChangeListener;

  public ActionDecoratorsCollector(Owl<TableViewer> tableViewer) {
    this.tableViewer = tableViewer;

    tableViewer.entity().getActionDecorators().forEach(this::registerActionDecorator);
    tableViewer.entity().actionDecoratorsProperty()
        .addListener((ListChangeListener<ActionDecorator>) change -> {
          while (change.next()) {
            if (change.wasAdded()) {
              change.getAddedSubList().forEach(this::registerActionDecorator);
            }
            if (change.wasRemoved()) {
              change.getRemoved().forEach(this::unregisterActionDecorator);
            }
          }
        });
  }

  public ObservableList<String> getTags() {
    return roActionTags;
  }

  public ObservableList<String> getProviders() {
    return roActionProviders;
  }

  private void registerActionDecorator(ActionDecorator actionDecorator) {
    actionDecorator.getTags().forEach(this::checkAndAddTag);
    checkAndAddProvider(actionDecorator.getActionOwl().entity().getActionProviderSafe());
    actionDecorator.tagsProperty().addListener(getTagListChangeListener());
  }

  private void unregisterActionDecorator(ActionDecorator actionDecorator) {
    actionDecorator.getTags().forEach(this::checkAndRemoveTag);
    checkAndRemoveProvider(actionDecorator.getActionOwl().entity().getActionProviderSafe());
    actionDecorator.tagsProperty().removeListener(getTagListChangeListener());
  }

  public void replaceTag(String oldTag, String newTag) {
    tableViewer.entity().getActionDecorators().forEach(actionDecorator -> {
      boolean isDel = actionDecorator.getTags().remove(oldTag);
      if (isDel) {
        actionDecorator.getTags().add(newTag);
      }
    });
    FXCollections.sort(actionTags);
  }

  public void removeTag(String tag) {
    tableViewer.entity().getActionDecorators().forEach(actionDecorator -> {
      actionDecorator.getTags().remove(tag);
    });
  }

  private void checkAndAddTag(String tag) {

    if (!actionTags.contains(tag)) {
      actionTags.add(tag);
      FXCollections.sort(actionTags);
      // actionTags.sort((s1, s2) -> s1.compareTo(s2));
    }
  }

  private void checkAndRemoveTag(String tag) {
    int countTag = 0;
    for (ActionDecorator actionDecorator : tableViewer.entity().getActionDecorators()) {
      if (actionDecorator.getTags().contains(tag)) {
        countTag++;
      }
    }
    if (countTag == 0) {
      actionTags.remove(tag);
    }
  }

  private void checkAndAddProvider(Optional<ActionProvider> provider) {
    if (provider.isPresent()) {
      if (!actionProviders.contains(provider.get().getIdentifier())) {
        actionProviders.add(provider.get().getIdentifier());
      }
    }
  }

  private void checkAndRemoveProvider(Optional<ActionProvider> oProvider) {
    if (oProvider.isPresent()) {
      ActionProvider provider = oProvider.get();
      int providerCount = 0;
      for (ActionDecorator actionDecorator : tableViewer.entity().getActionDecorators()) {
        Optional<ActionProvider> coProvider =
            actionDecorator.getActionOwl().entity().getActionProviderSafe();
        if (coProvider.isPresent()) {
          if (coProvider.get().equals(provider)) {
            providerCount++;
          }
        }
      }
      if (providerCount == 0) {
        actionProviders.remove(provider.getIdentifier());
      }
    }
  }

  private ListChangeListener<String> getTagListChangeListener() {
    if (tagListChangeListener == null) {
      tagListChangeListener = change -> {
        while (change.next()) {
          if (change.wasAdded()) {
            change.getAddedSubList().forEach(this::checkAndAddTag);
          }
          if (change.wasRemoved()) {
            change.getRemoved().forEach(this::checkAndRemoveTag);
          }
        }
      };
    }
    return tagListChangeListener;
  }

}
