package space.sadfox.tableviewer;

//TODO: Удалить класс
public class TableViewerDao2 {

//	private TableViewer tableViewer;
//
//	public TableViewerDao2(TableViewer tableViewer) {
//		this.tableViewer = tableViewer;
//	}
//
//	public List<TableDataView> getViews() {
//
//		List<TableDataView> tableDataViews = new ArrayList<>();
//
//		getTableViewer().getTableDataViews().forEach(tv -> {
//			try {
//				tableDataViews.add(TableDataViewDao.loadTableDataView(tv));
//			} catch (IOException | JAXBException e) {
//				ErrorLogger.registerException(e);
//			}
//		});
//
//		return tableDataViews;
//	}
//	
//
//	public void addView(TableDataView tableDataView) {
//		getTableViewer().getTableDataViews().add(tableDataView.getFileName());
//	}
//
//	public void removeView(TableDataView tableDataView) {
//		getTableViewer().getTableDataViews().remove(tableDataView.getFileName());
//	}
//
//	public List<TableDataFilter> getFilters() {
//		List<TableDataFilter> filters = new ArrayList<>();
//
//		getTableViewer().getTableDataFilters().forEach(tf -> {
//			try {
//				filters.add(TableDataFilterDao.loadTableDataFilter(tf));
//			} catch (IOException | JAXBException e) {
//				ErrorLogger.registerException(e);
//			}
//		});
//
//		return filters;
//	}
//	
//	public void addFilter(TableDataFilter tableDataFilter) {
//		getTableViewer().getTableDataFilters().add(tableDataFilter.getFileName());
//	}
//	
//	public void removeFilter(TableDataFilter tableDataFilter) {
//		getTableViewer().getTableDataFilters().remove(tableDataFilter.getFileName());
//	}
//
//	public TableDataDao getTableDataDao() throws Nullable {
//		return new TableDataDao(getTableData());
//	}
//
//	public static ActionEntity getActionEntity(ActionDecorator actionDecorator) {
//		return actionDecorator.getAction();
//	}
//	
//	public ActionDecorator addActionEntity(ActionEntity actionEntity) {
//		ActionDecorator actionDecorator = new ActionDecorator();
//		actionDecorator.setAction(actionEntity.getFileName());
//		getTableViewer().getActionDecorators().add(actionDecorator);
//		return actionDecorator;
//	}
//
//	public List<ActionEntity> getActionEntities() {
//		List<ActionEntity> actionEntities = new ArrayList<>();
//
//		for (int i = 0; i < tableViewer.getActionDecorators().size(); i++) {
//			ActionDecorator actionDecorator = tableViewer.getActionDecorators().get(i);
//			ActionEntity actionEntity = getActionEntity(actionDecorator);
//			if (actionEntity == null) {
//				tableViewer.getActionDecorators().remove(i);
//				i--;
//				continue;
//			}
//			actionEntities.add(actionEntity);
//		}
//
//		return actionEntities;
//	}
//
//	public static TableViewer createTableViewer() {
//		try {
//			TableViewer newTableViewer = EntityLoader.INSTANCE.createEntity(TableViewer.class);
//			newTableViewer.setTitle("New Table Viewer");
//			return newTableViewer;
//		} catch (JAXBException | IOException e) {
//			ErrorLogger.registerException(e);
//		}
//		return null;
//	}
//
//	public static List<TableViewer> getTableViewers() {
//		return EntityLoader.INSTANCE.loadAllEntities(TableViewer.class);
//	}
//
//	public TableViewer getTableViewer() {
//		return tableViewer;
//	}

}
