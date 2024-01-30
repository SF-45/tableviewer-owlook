package space.sadfox.tableviewer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Logger;

public class TableViewers {
  public static List<Owl<ActionEntity>> getActionEntities(TableViewer tableViewer) {
    return tableViewer.getActionDecorators().stream().map(ActionDecorator::getActionOwl)
        .collect(Collectors.toList());
  }

  public static Owl<TableViewer> createTableViewer() {
    try {
      // TableViewer newTableViewer = EntityLoader.INSTANCE.createEntity(TableViewer.class);
      Owl<TableViewer> newTableViewer = OwlLoader.INSTANCE.createOwl(TableViewer.class);
      newTableViewer.head().setTitle("New Table Viewer");
      return newTableViewer;
    } catch (Exception e) {
      Logger.registerException(1, e);
    }
    return null;
  }

  public static List<Owl<TableViewer>> getTableViewers() throws IOException {
    return OwlLoader.INSTANCE.getOwls(TableViewer.class);
  }
}
