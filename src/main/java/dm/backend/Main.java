package dm.backend;

import dm.backend.table.ColumnType;
import dm.backend.table.Table;

public class Main {
    public static void main(String[] args) {
        Table t = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"});
        System.out.println(t);
        System.out.println(t.columns.get(0));
    }
}
