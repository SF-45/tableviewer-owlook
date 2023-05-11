package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.action.ActionEntityDao;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.filter.TableDataFilterDao;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.dataccess.view.TableDataViewDao;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;

public class TableViewerDao {

	private TableViewer tableViewer;
	private static EntityLoader loader;
	
	static {
		loader = new EntityLoader();
	}

	public TableViewerDao(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public static TableDataView getView(String fileName) {
		
		//TODO: Вообще удалить этот метод
		try {
			return loader.loadEntity(fileName, TableDataView.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public List<TableDataView> getViews() {
		List<TableDataView> tableDataViews = new ArrayList<>();
		
		//TODO: Переделать, так как удаление происходит при инициализации
		for (int i = 0; i < tableViewer.getTableDataViews().size(); i++) {
			String fileName = tableViewer.getTableDataViews().get(i);
			TableDataView viev = getView(fileName);
			if (viev == null) {
				tableViewer.getTableDataViews().remove(fileName);
				i--;
				continue;
			}
			tableDataViews.add(viev);
		}
		
		return tableDataViews;
	}

	public List<TableDataViewDao> getViewsDao() {
		return getViews().stream().map(f -> new TableDataViewDao(f)).collect(Collectors.toList());
	}

	public static TableDataFilter getFilter(String fileName) {
		try {
			return loader.loadEntity(fileName, TableDataFilter.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public List<TableDataFilter> getFilters() {
		List<TableDataFilter> filters = new ArrayList<>();
		
		//TODO: Переделать, так как удаление происходит при инициализации
		for (int i = 0; i < tableViewer.getTableDataFilters().size(); i++) {
			String fileName = tableViewer.getTableDataFilters().get(i);
			TableDataFilter filter = getFilter(fileName);
			if (filter == null) {
				tableViewer.getTableDataFilters().remove(fileName);
				i--;
				continue;
			}
			filters.add(filter);
		}
		return filters;
	}
	
	public TableDataFilterDao getFilterDao(TableDataFilter filter) {
		return new TableDataFilterDao(filter, getTableData());
	}

	public List<TableDataFilterDao> getFiltersDao() {
		return getFilters().stream().map(f -> getFilterDao(f)).collect(Collectors.toList());
	}

	public TableData getTableData() {
		try {
			return loader.loadEntity(tableViewer.getTableDataConnection(), TableData.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		
		return null;
	}
	
	public void setTableData(TableData tableData) {
		tableViewer.setTableDataConnection(tableData.getFileName());
	}

	public TableDataDao getTableDataDao() {
		return new TableDataDao(getTableData());
	}

	public static ActionEntity getActionEntity(ActionDecorator actionDecorator) {
		try {
			return loader.loadEntity(actionDecorator.getAction(), ActionEntity.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	public ActionEntityDao getActionEntityDao(ActionEntity actionEntity) {
		return new ActionEntityDao(actionEntity, getTableData());
	}
	
	public ActionEntityDao getActionEntityDao(ActionDecorator actionDecorator) {
		return new ActionEntityDao(getActionEntity(actionDecorator), getTableData());
	}
	
	public List<ActionEntity> getActionEntities () {
		List<ActionEntity> actionEntities = new ArrayList<>();
		
		for (int i = 0; i < tableViewer.getActionDecorators().size(); i++) {
			ActionDecorator actionDecorator = tableViewer.getActionDecorators().get(i);
			ActionEntity actionEntity = getActionEntity(actionDecorator);
			if (actionEntity == null) {
				tableViewer.getActionDecorators().remove(i);
				i--;
				continue;
			}
			actionEntities.add(actionEntity);
		}
		
		return actionEntities;
	}
	
	public List<ActionEntityDao> getActionEntityDaos () {
		return getActionEntities().stream()
				.map(this::getActionEntityDao)
				.collect(Collectors.toList());
	}

	public static TableViewer createTableViewer() {
		try {
			TableViewer newTableViewer = loader.createEntity(TableViewer.class);
			newTableViewer.setTitle("New Table Viewer");
			return newTableViewer;
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	public static List<TableViewer> getTableViewers() {
		try {
			return loader.loadAllEntities(TableViewer.class);
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
		return new ArrayList();
	}

}
