import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.util.Arrays;

public class Utils {
    static Table readData() throws IOException {
        CsvReadOptions.Builder builder = CsvReadOptions.builder("src/main/resources/Thyroid_Dataset.txt").
                header(false).tableName("Thyroid Dataset");

        CsvReadOptions options = builder.build();
        Table t = Table.read().usingOptions(options);
        String[] column_names = {"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
        for(int i=0;i<column_names.length;i++){
            t.column(i).setName(column_names[i]);
        }
        return t;
    }

    static Table mode(Table t, String column_name){
        Table t2 = null;
        if (t.column(column_name).type() == ColumnType.DOUBLE){
            Table t3 = Table.create("", t.doubleColumn(column_name).asStringColumn());
            t2 = t3.countBy(column_name + " strings");

            t2.replaceColumn(0, t2.stringColumn(0).parseDouble().setName("Category"));
        }
        else{
            t2 = t.countBy(column_name);
        }
        int m = ((Double)(t2.summarize("Count", AggregateFunctions.max).apply().get(0,0))).intValue();
        Table mode = t2.where(t2.numberColumn(1).isEqualTo(m));
        return mode;
    }

    static Table meanMedian(Table t){
        return t.summarize("t3_resin", "total_thyroxin", "total_triio",
                AggregateFunctions.mean, AggregateFunctions.median).apply();
    }


}
