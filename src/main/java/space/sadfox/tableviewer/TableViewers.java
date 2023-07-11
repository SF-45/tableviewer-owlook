package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.OwlLogger;

public class TableViewers {
	public static List<ActionEntity> getActionEntities(TableViewer tableViewer) {
		return tableViewer.getActionDecorators().stream().map(ActionDecorator::getAction).collect(Collectors.toList());
	}
	public static TableViewer createTableViewer() {
		try {
			TableViewer newTableViewer = EntityLoader.INSTANCE.createEntity(TableViewer.class);
			newTableViewer.setTitle("New Table Viewer");
			return newTableViewer;
		} catch (JAXBException | IOException e) {
			OwlLogger.registerException(1, e);
		}
		return null;
	}
	public static List<TableViewer> getTableViewers() throws IOException {
		return EntityLoader.INSTANCE.loadAllEntities(TableViewer.class);
	}
}
