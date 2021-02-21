package dm.backend.kmeans;

import dm.backend.table.IntegerColumn;
import dm.backend.table.Table;

public class KMeans {
    public IntegerColumn columnLabel;
    public Table table;
    public int numClusters;
    public boolean finished = false;


    public KMeans(Table table, int columnLabel, int numClusters) {
        this.columnLabel = (IntegerColumn) table.column(columnLabel);
        this.table = table;
        this.table.removeColumn(columnLabel);
        this.numClusters = numClusters;
    }

    public void start(){

    }
}
