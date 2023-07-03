package space.sadfox.tableviewer;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class ActionDecoratorsCollector {

	private final TableViewer tableViewer;

	private final ObservableList<StringProperty> actionTags = FXCollections
			.synchronizedObservableList(FXCollections.observableArrayList());
	private final ObservableList<String> actionProviders = FXCollections
			.synchronizedObservableList(FXCollections.observableArrayList());

	private final ObservableList<StringProperty> roActionTags = FXCollections.unmodifiableObservableList(actionTags);
	private final ObservableList<String> roActionProviders = FXCollections.unmodifiableObservableList(actionProviders);

	private ListChangeListener<StringProperty> tagListChangeListener;
	
	public ActionDecoratorsCollector(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		
		tableViewer.getActionDecorators().forEach(this::registerActionDecorator);
		tableViewer.actionDecoratorsProperty().addListener((ListChangeListener<ActionDecorator>) change -> {
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

	public ObservableList<StringProperty> getTags() {
		return roActionTags;
	}

	public ObservableList<String> getProviders() {
		return roActionProviders;
	}
	
	private void registerActionDecorator(ActionDecorator actionDecorator) {
		actionDecorator.getTags().forEach(this::checkAndAddTag);
		checkAndAddProvider(actionDecorator.getAction().getActionProvider());
		actionDecorator.tagsProperty().addListener(getTagListChangeListener());
	}
	
	private void unregisterActionDecorator(ActionDecorator actionDecorator) {
		actionDecorator.getTags().forEach(this::checkAndRemoveTag);
		checkAndRemoveProvider(actionDecorator.getAction().getActionProvider());
		actionDecorator.tagsProperty().removeListener(getTagListChangeListener());
	}
	
	public void replaceTag(StringProperty oldTag, StringProperty newTag) {
		tableViewer.getActionDecorators().forEach(actionDecorator -> {
			boolean isDel = actionDecorator.getTags().removeIf(pred -> {
				return pred.equals(oldTag);
			});
			if (isDel) {
				actionDecorator.getTags().add(newTag);
			}
		});
	}
	
	private void checkAndAddTag(StringProperty tag) {
		
		if (!actionTags.stream().anyMatch(tagfind -> tagfind.get().equals(tag.get()))) {
			actionTags.add(tag);
		}
	}
	
	private void checkAndRemoveTag(StringProperty tag) {
		int countTag = 0;
		for (ActionDecorator actionDecorator : tableViewer.getActionDecorators()) {
			if (actionDecorator.getTags().contains(tag)) {
				countTag++;
			}
		}
		if (countTag == 0) {
			actionTags.remove(tag);
		}
	}
	
	private void checkAndAddProvider(String provider) {
		if (!actionProviders.contains(provider)) {
			actionProviders.add(provider);
		}
	}
	
	private void checkAndRemoveProvider(String provider) {
		int providerCount = 0;
		for (ActionDecorator actionDecorator : tableViewer.getActionDecorators()) {
			if (actionDecorator.getAction().getActionProvider().equals(provider)) {
				providerCount++;
			}
		}
		if (providerCount == 0) {
			actionProviders.remove(provider);
		}
	}
	
	private ListChangeListener<StringProperty> getTagListChangeListener() {
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
