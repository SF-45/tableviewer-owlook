package space.sadfox.tableviewer.ui.base;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.util.Callback;

public class GroupAccordion<I, C> extends Accordion {
	@FunctionalInterface
	public interface Match<I, C> {
		boolean match(I item, C criterion);
	}

	private class GroupTitledPane extends TitledPane {
		final C criterion;
		final ButtonList buttonList = new ButtonList();

		GroupTitledPane(C criterion) {
			this.criterion = criterion;
			buttonList.sortComparatorProperty().bindBidirectional(comparator);
			setContent(buttonList);
			setText(getGroupNameFactory().call(criterion).get());
			textProperty().bind(getGroupNameFactory().call(getCriterion()));
			getItems().forEach(this::addIfMatchButton);
			getItems().addListener((ListChangeListener<I>) change -> {
				while (change.next()) {
					if (change.wasAdded()) {
						change.getAddedSubList().forEach(this::addIfMatchButton);
					}
					if (change.wasRemoved()) {
						change.getRemoved().forEach(item -> {
							buttonList.getChildren().removeIf(button -> button.getUserData().equals(item));
						});
					}
				}
			});
			
		}

		void addIfMatchButton(I item) {
			if (!getMatcher().match(item, getCriterion())) {
				return;
			}
			Button itemButton = getButtonFactory().call(item);
			itemButton.setUserData(item);
			buttonList.getChildren().add(itemButton);

//			ListChangeListener<I> deleteListener = change -> {
//				while (change.next()) {
//					if (change.wasRemoved() && change.getRemoved().contains(item)) {
//						getButtonList().getChildren().remove(itemButton);
//						// getItems().removeListener(change);
//						// todo: может вызывать ошибки
//						// Если добавить, удалить, а потом снова добавить item
//					}
//				}
//			};
//			getItems().addListener(deleteListener);
		}
		
		C getCriterion() {
			return criterion;
		}

	}

	private ObservableList<I> items;
	private ObservableList<C> criteria;

	private ObservableList<GroupTitledPane> groupTitledPanes = FXCollections.observableArrayList();
	private Match<I, C> matcher;

	private Callback<I, Button> buttonFactory;
	private Callback<C, StringProperty> groupNameFactory;
	
	private ObjectProperty<Comparator<Node>> comparator = new SimpleObjectProperty<>();

	public GroupAccordion() {
		
		
//		getGroupTitledPanes().addListener((ListChangeListener<GroupTitledPane<T>>) change -> {
//			while (change.next()) {
//				if (change.wasAdded()) {
//					getPanes().addAll(change.getAddedSubList());
//				}
//				if (change.wasRemoved()) {
//					getPanes().removeAll(change.getRemoved());
//				}
//			}
//		});
		
		getGroupTitledPanes().addListener((InvalidationListener)invalidChange -> {
			getPanes().clear();
			getPanes().addAll(getGroupTitledPanes());
		});
	}

	public ObservableList<I> getItems() {
		if (items == null) {
			items = FXCollections.observableArrayList();
		}
		return items;
	}

	public void setItems(ObservableList<I> items) {
		this.items = items;
		fullReload();
	}

	public ObservableList<C> getCriteria() {
		if (criteria == null) {
			criteria = FXCollections.observableArrayList();
		}
		return criteria;
	}

	public void setCriteria(ObservableList<C> criteria) {
		this.criteria = criteria;
		getCriteria().addListener((ListChangeListener<C>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(crit -> {
						getGroupTitledPanes().add(new GroupTitledPane(crit));
					});
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(crit -> {
						var remList = getGroupTitledPanes().stream()
								.filter(titledPane -> titledPane.getCriterion() == crit)
								.collect(Collectors.toList());
						getGroupTitledPanes().removeAll(remList);
					});
				}
			}
		});
		fullReload();
	}

	private Match<I, C> getMatcher() {
		if (matcher == null) {
			matcher = (a, b) -> true;
		}
		return matcher;
	}

	public void setMatcher(Match<I, C> matcher) {
		this.matcher = matcher;
		fullReload();
	}

	public Callback<I, Button> getButtonFactory() {
		if (buttonFactory == null) {
			buttonFactory = item -> new Button(item.toString());
		}
		return buttonFactory;
	}

	public void setButtonFactory(Callback<I, Button> buttonFactory) {
		this.buttonFactory = buttonFactory;
		fullReload();
	}
	
	public Callback<C, StringProperty> getGroupNameFactory() {
		if (groupNameFactory == null) {
			groupNameFactory = crit -> new SimpleStringProperty(crit.toString());
		}
		return groupNameFactory;
	}

	public void setGroupNameFactory(Callback<C, StringProperty> groupNameFactory) {
		this.groupNameFactory = groupNameFactory;
		fullReload();
	}

	private ObservableList<GroupTitledPane> getGroupTitledPanes() {
		return groupTitledPanes;
	}

	private void fullReload() {
		if (getCriteria().size() == 0) return;
		getGroupTitledPanes().clear();
		getCriteria().forEach(crit -> getGroupTitledPanes().add(new GroupTitledPane(crit)));
		
	}
	
	public void setSortComparator(Comparator<Node> comparator) {
		this.comparator.set(comparator);
	}

}
