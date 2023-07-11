package space.sadfox.tableviewer.ui.base;

import java.util.Comparator;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
import space.sadfox.owlook.utils.Nullable;

public class GroupAccordion<I, C> extends Accordion {

	public interface InternalItemСhangeNotifier<I> {
		void addListener(I item, InvalidationListener listener);
	}

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
			textProperty().bind(getGroupNameFactory().call(criterion));

		}

		void addItem(I item) {
			Button itemButton = getButtonFactory().call(item);
			itemButton.setUserData(item);
			buttonList.addAndSort(itemButton);
		}

		void removeItem(I item) {
			buttonList.getChildren().removeIf(button -> button.getUserData().equals(item));
		}

		/**
		 * Проверяет <I> item
		 * <br>
		 * Удаляет если содержится в коллекции {@link #buttonList} и не соответствует {@link GroupAccordion#matcher}
		 * <br>
		 * Добавляет если не содержится в коллекции {@link #buttonList} и соответствует {@link GroupAccordion#matcher}
		 * @param item - Элемент коллекции {@link GroupAccordion#items}
		 */
		void insertItem(I item) {
			boolean contains = buttonList.getChildren().stream().anyMatch(node -> node.getUserData().equals(item));
			boolean match = getMatcher().match(item, criterion);
			
			if (contains && !match) {
				removeItem(item);
			} else if (!contains && match) {
				addItem(item);
			}
		}

	}

	private ObservableList<I> items;
	private ObservableList<C> criteria;

	private final ObservableList<GroupTitledPane> groupTitledPanes = FXCollections.observableArrayList();
	private Match<I, C> matcher;
	private InternalItemСhangeNotifier<I> notifier;

	private Callback<I, Button> buttonFactory;
	private Callback<C, StringProperty> groupNameFactory;

	private final ObjectProperty<Comparator<Node>> comparator = new SimpleObjectProperty<>();

	public GroupAccordion() {
		groupTitledPanes.addListener((InvalidationListener) invalidChange -> {
			getPanes().clear();
			getPanes().addAll(groupTitledPanes);
		});
	}

	/**
	 * Инициализация коллекции {@link #items} при ее изменении
	 * <p>
	 * Вызывается в {@link #setItems(ObservableList)}
	 */
	private void initItemsList() {
		getItems().addListener((ListChangeListener<I>) change -> {
			while (change.next()) {
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(this::allocateItem);
				}
				if (change.wasRemoved()) {
					change.getRemoved().forEach(this::allocateItem);
				}
			}
		});
		initInternalItemСhangeNotifier();
		
		
	}
	
	/**
	 * Инициализация слушателей элементов ({@link #items}).
	 * <p>
	 * Используется в том случае, если элемент может менять свое внутреннее состояние, которое влияет на {@link #matcher}
	 * <p>
	 * Вызывается в {@link #initItemsList()}, {@link #setInternalItemСhangeNotifier()}
	 */
	private void initInternalItemСhangeNotifier() {
		try {
			var notifier = getInternalItemСhangeNotifier();
			getItems().forEach(item -> {
				notifier.addListener(item, change -> {
					allocateItem(item);
				});
			});
			getItems().addListener((ListChangeListener<I>) changeList -> {
				while (changeList.next()) {
					if (changeList.wasAdded()) {
						changeList.getAddedSubList().forEach(item -> {
							notifier.addListener(item, change -> {
								allocateItem(item);
							});
						});
					}
				}
			});
		} catch (Nullable e) {
		}
		update();
	}
	
	/**
	 * Инициализация коллекции {@link #criteria} при ее изменении
	 * <p>
	 * Вызывается в {@link #setCriteria(ObservableList)}
	 */
	private void initCriteriaList( ) {
		getCriteria().addListener((ListChangeListener<C>) change -> {
			while (change.next()) {
				if (change.wasRemoved()) {
					change.getRemoved().forEach(crit -> {
						var remList = groupTitledPanes.stream()
								.filter(titledPane -> titledPane.criterion == crit).collect(Collectors.toList());
						groupTitledPanes.removeAll(remList);
					});
				}
				if (change.wasAdded()) {
					change.getAddedSubList().forEach(crit -> {
						GroupTitledPane titledPane = new GroupTitledPane(crit);
						getItems().forEach(titledPane::insertItem);
						groupTitledPanes.add(titledPane);
					});
					groupTitledPanes.sort((pane1, pane2) -> pane1.getText().compareTo(pane2.getText()));

				}
			}
		});
		update();
	}

	/**
	 * Распределяет <b> item по {@link #groupTitledPanes}.
	 * @param item - Элемент коллекции {@link #items}
	 */
	private void allocateItem(I item) {
		groupTitledPanes.forEach(pane -> pane.insertItem(item));
	}
	/**
	 * Удаляет все {@link #groupTitledPanes} и добавляет их заново. Потом распределяет {@link #items} по ним.
	 */
	private void update() {
		if (getCriteria().size() == 0)
			return;
		groupTitledPanes.clear();
		getCriteria().forEach(crit -> groupTitledPanes.add(new GroupTitledPane(crit)));
		groupTitledPanes.sort((pane1, pane2) -> pane1.getText().compareTo(pane2.getText()));
		
		getItems().forEach(this::allocateItem);
	}

	public ObservableList<I> getItems() {
		if (items == null) {
			setItems(FXCollections.observableArrayList());
		}
		return items;
	}

	public void setItems(ObservableList<I> items) {
		this.items = items;
		initItemsList();
	}

	public ObservableList<C> getCriteria() {
		if (criteria == null) {
			setCriteria(FXCollections.observableArrayList());
		}
		return criteria;
	}

	public void setCriteria(ObservableList<C> criteria) {
		this.criteria = criteria;
		initCriteriaList();
	}

	public Match<I, C> getMatcher() {
		if (matcher == null) {
			matcher = (a, b) -> true;
		}
		return matcher;
	}

	public void setMatcher(Match<I, C> matcher) {
		this.matcher = matcher;
		update();
	}

	public Callback<I, Button> getButtonFactory() {
		if (buttonFactory == null) {
			buttonFactory = item -> new Button(item.toString());
		}
		return buttonFactory;
	}

	public void setButtonFactory(Callback<I, Button> buttonFactory) {
		this.buttonFactory = buttonFactory;
		update();
	}

	public Callback<C, StringProperty> getGroupNameFactory() {
		if (groupNameFactory == null) {
			groupNameFactory = crit -> new SimpleStringProperty(crit.toString());
		}
		return groupNameFactory;
	}

	public void setGroupNameFactory(Callback<C, StringProperty> groupNameFactory) {
		this.groupNameFactory = groupNameFactory;
		update();
	}

	public Comparator<Node> getSortComparator() {
		return comparator.get();
	}

	public void setSortComparator(Comparator<Node> comparator) {
		this.comparator.set(comparator);
		update();
	}
	
	public ReadOnlyObjectProperty<Comparator<Node>> sortComparatorProperty() {
		return comparator;
	}

	public void setInternalItemСhangeNotifier(InternalItemСhangeNotifier<I> notifier) {
		this.notifier = notifier;
		initInternalItemСhangeNotifier();
	}
	
	public InternalItemСhangeNotifier<I> getInternalItemСhangeNotifier() throws Nullable {
		if (notifier == null)
			throw new Nullable();
		return notifier;
	}

}
