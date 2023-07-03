package space.sadfox.tableviewer.ui.base;
//
//import java.io.IOException;
//
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import space.sadfox.owlook.jaxb.EntityLoader;
//import space.sadfox.owlook.jaxb.JAXBEntity;
//import space.sadfox.owlook.ui.base.Controller;
//import space.sadfox.tableviewer.TableViewer;
//
public class FileNamePicker {// extends Controller {
//	
//    @FXML
//    private Button cancel;
//
//    @FXML
//    private Button confirm;
//
//    @FXML
//    private TextField filename;
//
//    @FXML
//    private Label warningLabel;
//    
//    private BooleanProperty nameValid = new SimpleBooleanProperty(false);
//    
//    private boolean isConfirm = false;
//    
//    private Class<? extends JAXBEntity> target;
//    
//    
//    private String oldFileName;
//
//	public FileNamePicker(Class<? extends JAXBEntity> target) throws IOException {
//		super(TableViewer.class.getResource("fxml/filename-picker.fxml"));
//		
//		this.target = target;
//		
//		confirm.disableProperty().bind(nameValid.not());
//		warningLabel.visibleProperty().bind(nameValid.not());
//		filename.textProperty().addListener((property, oldValue, newValue) -> {
//			String err = validateFileName(newValue);
//			if (err.equals("")) nameValid.set(true);
//			else {
//				warningLabel.setText(err);
//				nameValid.set(false);
//			}
//		});
//		
//		cancel.setOnAction(event -> {
//			getStage().close();
//		});
//		confirm.setOnAction(event -> {
//			isConfirm = true;
//			getStage().close();
//		});
//		
//		
//	}
//	
//	public FileNamePicker(Class<? extends JAXBEntity> target, String oldFileName) throws IOException {
//		this(target);
//		this.oldFileName = oldFileName;
//		filename.setText(oldFileName);
//		
//	}
//
//	
//	private String validateFileName(String fileName) {
//		if (fileName.equals("")) {
//			 return "File name is empty";
//		} else if (fileName.equals(oldFileName)) {
//			return " ";
//		} else if (EntityLoader.INSTANCE.entityExist(fileName, target)) {
//			return "File name alredy exist \"" + fileName + "\"";
//		}
//		return "";
//	}
//	
//	public boolean isConfirm () {
//		return isConfirm;
//	}
//	
//	public String getFileName() {
//		return filename.getText();
//	}
//
}
