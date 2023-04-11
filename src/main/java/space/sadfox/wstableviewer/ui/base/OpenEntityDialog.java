package space.sadfox.wstableviewer.ui.base;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.wstableviewer.TableViewer;

public class OpenEntityDialog<T extends JAXBEntity> extends Controller {

	@FXML
	private Button open;

	@FXML
	private TableView<T> tableView;
	
	private EntityLoader loader;
	
	private boolean isOpened = false;

	public OpenEntityDialog(Class<T> target, List<T> alredyOpenned) throws IOException {
		super(TableViewer.class.getResource("fxml/open-entity-dialog.fxml"));
		
		loader = new EntityLoader();
		
		TableColumn<T, String> fileName = new TableColumn<>("File Name");
		fileName.setCellValueFactory(call -> {
			return new SimpleStringProperty(call.getValue().getFileName());
		});
		
		TableColumn<T, String> title = new TableColumn<>("Title");
		title.setCellValueFactory(call -> {
			return new SimpleStringProperty(call.getValue().getTitle());
		});
		
		tableView.getColumns().addAll(fileName, title);
		
		ObservableList<T> allEntities = FXCollections.observableList(loader.loadAllEntities(target));
		
		allEntities = allEntities.filtered(f -> {
			for (var e : alredyOpenned) {
				if (f.equals(e)) return false;
			}
			return true;
		});
		
		tableView.setItems(FXCollections.observableList(allEntities));
		
		
		tableView.getSelectionModel().selectedIndexProperty().addListener((prop, oldVal, newVal) -> {
			if (newVal.intValue() < 0) open.setDisable(true);
			else open.setDisable(false);
		});
		open.setOnAction(event -> {
			isOpened = true;
			getStage().close();
		});
		
		
		
	}
	
	public T getOpenned() {
		return tableView.getSelectionModel().getSelectedItem();
	}
	
	public boolean isOpened() {
		return isOpened;
	}

}
