package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.command.CommandEntityList;
import space.sadfox.dataccess.command.CommandEntityListDao;
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
	private TableData tableData;
	private EntityLoader loader;

	public TableViewerDao(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
		loader = new EntityLoader();
	}

	public TableDataView getView(String fileName) {
		try {
			return loader.loadEntity(fileName, TableDataView.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public List<TableDataView> getViews() {
		List<TableDataView> tableDataViews = new ArrayList<>();
		for (String fileName : tableViewer.getTableDataViews()) {
			tableDataViews.add(getView(fileName));

		}
		return tableDataViews;
	}

	public List<TableDataViewDao> getViewsDao() {
		return getViews().stream().map(f -> new TableDataViewDao(f)).collect(Collectors.toList());
	}

	public TableDataFilter getFilter(String fileName) {
		try {
			return loader.loadEntity(fileName, TableDataFilter.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public List<TableDataFilter> getFilters() {
		List<TableDataFilter> filters = new ArrayList<>();
		
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
		if (tableData == null) {
			try {
				tableData = loader.loadEntity(tableViewer.getTableDataConnection(), TableData.class);
			} catch (IOException | JAXBException e) {
				ErrorLogger.registerException(e);
			}
		}
		return tableData;
	}

	public TableDataDao getTableDataDao() {
		return new TableDataDao(getTableData());
	}

	public CommandEntityList createCommandEntityList(String name) {
		try {
			CommandEntityList commandEntityList = loader.createEntity(name, CommandEntityList.class);
			tableViewer.getCommands().add(name);
			return commandEntityList;
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public List<CommandEntityList> getCommandEtityLists() {
		List<CommandEntityList> commandEntityLists = new ArrayList<>();
		for (String fileName : tableViewer.getCommands()) {
			try {
				commandEntityLists.add(loader.loadEntity(fileName, CommandEntityList.class));
			} catch (IOException | JAXBException e) {
				ErrorLogger.registerException(e);
			}
		}
		return commandEntityLists;
	}

	public List<CommandEntityListDao> getCommandEtityListDao() {
		return getCommandEtityLists().stream().map(c -> new CommandEntityListDao(c)).collect(Collectors.toList());
	}

}
