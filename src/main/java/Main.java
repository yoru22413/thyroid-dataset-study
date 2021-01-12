import tech.tablesaw.aggregate.AggregateFunction;
import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Table t = Utils.readData();
        System.out.println(t);
        System.out.println(Utils.mode(t, "t3_resin"));
        System.out.println(Utils.mode(t, "total_thyroxin"));
        System.out.println(Utils.mode(t, "total_triio"));
    }
}
