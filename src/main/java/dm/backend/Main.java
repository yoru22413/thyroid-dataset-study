package dm.backend;

import dm.backend.apriori.Algorithm;
import dm.backend.apriori.AprioriStruct;
import dm.backend.apriori.IntValue;
import dm.backend.apriori.IntValueSet;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Table t = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"});
        t.removeColumn(0);
        for (int i = 0; i < t.width(); i++) {
            t.setColumn(i, t.column(i).discretization(5));
        }
        AprioriStruct aps = new AprioriStruct(t);
        IntValueSet[] sets = Algorithm.apriori(aps, 0.4);
        for (IntValueSet set :
                sets) {
            System.out.println(set);
        }
    }
}
