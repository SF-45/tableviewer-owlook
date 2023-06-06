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
import space.sadfox.owlook.utils.Nullable;

public class TableViewerDao {

	private TableViewer tableViewer;

	public TableViewerDao(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	public List<TableDataView> getViews() {

		List<TableDataView> tableDataViews = new ArrayList<>();

		getTableViewer().getTableDataViews().forEach(tv -> {
			try {
				tableDataViews.add(TableDataViewDao.loadTableDataView(tv));
			} catch (IOException | JAXBException e) {
				ErrorLogger.registerException(e);
			}
		});

		return tableDataViews;
	}
	
	public List<TableDataViewDao> getViewsDao() {
		return getViews().stream().map(f -> new TableDataViewDao(f)).collect(Collectors.toList());
	}

	public void addView(TableDataView tableDataView) {
		getTableViewer().getTableDataViews().add(tableDataView.getFileName());
	}

	public void removeView(TableDataView tableDataView) {
		getTableViewer().getTableDataViews().remove(tableDataView.getFileName());
	}

	public List<TableDataFilter> getFilters() {
		List<TableDataFilter> filters = new ArrayList<>();

		getTableViewer().getTableDataFilters().forEach(tf -> {
			try {
				filters.add(TableDataFilterDao.loadTableDataFilter(tf));
			} catch (IOException | JAXBException e) {
				ErrorLogger.registerException(e);
			}
		});

		return filters;
	}

	public TableDataFilterDao getFilterDao(TableDataFilter filter) throws Nullable {
		return new TableDataFilterDao(filter, getTableData());
	}

	public List<TableDataFilterDao> getFiltersDao() {
		return getFilters().stream().map(f -> {
			try {
				return getFilterDao(f);
			} catch (Nullable e) {
				return null;
			}
		}).collect(Collectors.toList());
	}
	
	public void addFilter(TableDataFilter tableDataFilter) {
		getTableViewer().getTableDataFilters().add(tableDataFilter.getFileName());
	}
	
	public void removeFilter(TableDataFilter tableDataFilter) {
		getTableViewer().getTableDataFilters().remove(tableDataFilter.getFileName());
	}

	public TableData getTableData() throws Nullable {
		String tableDataConnection = getTableViewer().getTableDataConnection();
		if (tableDataConnection == null || !TableDataDao.existTableData(tableDataConnection)) {
			throw new Nullable();
		}
		try {
			return TableDataDao.loadTableData(getTableViewer().getTableDataConnection());
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
			throw new Nullable();
		}
	}

	public void setTableData(TableData tableData) {
		tableViewer.setTableDataConnection(tableData.getFileName());
		
	}

	public TableDataDao getTableDataDao() throws Nullable {
		return new TableDataDao(getTableData());
	}

	public static ActionEntity getActionEntity(ActionDecorator actionDecorator) {
		try {
			return ActionEntityDao.loadActionEntity(actionDecorator.getAction());
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public ActionEntityDao getActionEntityDao(ActionEntity actionEntity) throws Nullable {
		return new ActionEntityDao(actionEntity, getTableData());
	}

	public ActionEntityDao getActionEntityDao(ActionDecorator actionDecorator) throws Nullable {
		return new ActionEntityDao(getActionEntity(actionDecorator), getTableData());
	}
	
	public ActionDecorator addActionEntity(ActionEntity actionEntity) {
		ActionDecorator actionDecorator = new ActionDecorator();
		actionDecorator.setAction(actionEntity.getFileName());
		getTableViewer().getActionDecorators().add(actionDecorator);
		return actionDecorator;
	}

	public List<ActionEntity> getActionEntities() {
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

	public List<ActionEntityDao> getActionEntityDaos() {
		return getActionEntities().stream().map(t -> {
			try {
				return getActionEntityDao(t);
			} catch (Nullable e) {
				return null;
			}
		}).collect(Collectors.toList());
	}

	public static TableViewer createTableViewer() {
		try {
			TableViewer newTableViewer = EntityLoader.INSTANCE.createEntity(TableViewer.class);
			newTableViewer.setTitle("New Table Viewer");
			return newTableViewer;
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public static List<TableViewer> getTableViewers() {
		return EntityLoader.INSTANCE.loadAllEntities(TableViewer.class);
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

}
