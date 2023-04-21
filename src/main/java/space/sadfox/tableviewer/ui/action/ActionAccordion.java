package space.sadfox.tableviewer.ui.action;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.scene.control.Accordion;
import space.sadfox.tableviewer.ActionDecorator;
import space.sadfox.tableviewer.TableViewerDao;
import space.sadfox.tableviewer.ui.TableViewerTab;

public class ActionAccordion extends Accordion {

	private enum CurrectView {
		BY_TAG,
		BY_PROVIDER
	}
	
	private List<ActionDecorator> actionDecorators;
	private ObservableMap<String, ActionTitledPane> byTag;
	private ObservableMap<String, ActionTitledPane> byProvider;
	private CurrectView currectView;
	private TableViewerTab tableViewerTab;

	public ActionAccordion(TableViewerTab tableViewerTab) {
		this.tableViewerTab = tableViewerTab;
		actionDecorators = new ArrayList<>();
		byTag = FXCollections.observableHashMap();
		byTag.addListener((InvalidationListener) prop -> refresh() );
		byProvider = FXCollections.observableHashMap();
		byProvider.addListener((InvalidationListener) prop -> refresh() );
		
	}

	public void addAction(ActionDecorator actionDecorator) {
		if (actionDecorators.contains(actionDecorator))
			return;

		for (StringProperty tag : actionDecorator.getTags()) {
			tag.addListener((property, oldValue, newValue) -> {
				if (oldValue.equals(newValue))
					return;
				removeActionFromTag(actionDecorator, oldValue);
				putActionInTag(actionDecorator, newValue);
			});

			putActionInTag(actionDecorator, tag.get());
		}

		actionDecorator.tagsProperty().addListener((ListChangeListener<StringProperty>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(newTag -> {
						newTag.addListener((property, oldValue, newValue) -> {
							if (oldValue.equals(newValue))
								return;
							removeActionFromTag(actionDecorator, oldValue);
							putActionInTag(actionDecorator, newValue);
						});
						putActionInTag(actionDecorator, newTag.get());
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(delTag -> {
						removeActionFromTag(actionDecorator, delTag.get());
					});
				}
			}
		});
		putActionInProvider(actionDecorator);

		actionDecorators.add(actionDecorator);
	}

	public boolean deleteAction(ActionDecorator actionDecorator) {
		if (!actionDecorators.contains(actionDecorator))
			return false;
		actionDecorator.getTags().forEach(tag -> removeActionFromTag(actionDecorator, tag.get()));
		removeActionFromProvider(actionDecorator);
		return actionDecorators.remove(actionDecorator);
	}

	private void putActionInTag(ActionDecorator actionDecorator, String tag) {
		ActionTitledPane titledPaneByTag;
		if (byTag.containsKey(tag)) {
			titledPaneByTag = byTag.get(tag);
		} else {
			titledPaneByTag = new ActionTitledPane(tag, tableViewerTab);
			byTag.put(tag, titledPaneByTag);
		}
		titledPaneByTag.addAction(actionDecorator);
	}

	private void removeActionFromTag(ActionDecorator actionDecorator, String tag) {
		if (!byTag.containsKey(tag)) return;
		ActionTitledPane titledPaneByTag = byTag.get(tag);
		titledPaneByTag.removeAction(actionDecorator);
		if (titledPaneByTag.size() == 0) {
			byTag.remove(tag);
		}
		
	}

	private void putActionInProvider(ActionDecorator actionDecorator) {
		String actionProvider = TableViewerDao.getActionEntity(actionDecorator).getActionProvider();
		ActionTitledPane titledPaneByProvider;
		if (byProvider.containsKey(actionProvider)) {
			titledPaneByProvider = byProvider.get(actionProvider);
		} else {
			titledPaneByProvider = new ActionTitledPane(actionProvider, tableViewerTab);
			byProvider.put(actionProvider, titledPaneByProvider);
		}
		titledPaneByProvider.addAction(actionDecorator);
	}

	private void removeActionFromProvider(ActionDecorator actionDecorator) {
//		String actionProvider = TableViewerDao.getActionEntity(actionDecorator).getActionProvider();
//		if (!byProvider.containsKey(actionProvider)) return;
//		ActionTitledPane titledPaneByProvider = byProvider.get(actionProvider);
//		titledPaneByProvider.removeAction(actionDecorator);
//		if (titledPaneByProvider.size() == 0) {
//			byProvider.remove(actionProvider);
//		}
		byProvider.forEach((key, value) -> {
			value.removeAction(actionDecorator);
			if (value.size() == 0) {
				byProvider.remove(key);
			}
			});
		
		
		
	}

	public void showByTag() {
		getPanes().setAll(byTag.values());
		sort();
		currectView = CurrectView.BY_TAG;
	}

	public void showByProvider() {
		getPanes().setAll(byProvider.values());
		sort();
		currectView = CurrectView.BY_PROVIDER;
	}
	
	public void sort() {
		FXCollections.sort(getPanes(), (pane1, pane2) -> pane1.getText().toLowerCase().compareTo(pane2.getText().toLowerCase()));
	}
	
	private void refresh() {
		switch (currectView) {
		case BY_TAG:
			showByTag();
			break;

		case BY_PROVIDER:
			showByProvider();
			break;
		}
	}
	
}
