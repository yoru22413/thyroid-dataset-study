import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.Histogram;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.HistogramTrace;

import java.io.IOException;
import java.util.Arrays;
import java.lang.Math;

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

    static Table meanMedianMode(Table t){
        Table a = t.summarize("t3_resin",
                AggregateFunctions.mean, AggregateFunctions.median).apply().setName("t3_resin");
        Table b = t.summarize( "total_thyroxin",
                AggregateFunctions.mean, AggregateFunctions.median).apply().setName("total_thyroxin");
        Table c = t.summarize("total_triio",
                AggregateFunctions.mean, AggregateFunctions.median).apply().setName("total_triio");
        Table d = Table.create("Summary");
        Table[] tt = {a,b,c};

        DoubleColumn mode = DoubleColumn.create("Mode");
        DoubleColumn mean = DoubleColumn.create("Mean");
        DoubleColumn median = DoubleColumn.create("Median");
        StringColumn name = StringColumn.create("Name");
        for(Table f : tt){
            name.append(f.name());
            mean.append((Double) f.get(0,0));
            median.append((Double) f.get(0,1));
            mode.append((Double)f.get(0,0) - 3*((Double)f.get(0,0) - (Double)f.get(0,1)));
        }

        d.addColumns(name, mean, median, mode);
        return d;
    }

    static void boxplot(Table t, String[] columns){
        StringColumn str = StringColumn.create("Name");
        DoubleColumn dd = DoubleColumn.create("Value");
        Table p = Table.create();
        for(String col : columns){
            String[] s = new String[t.rowCount()];
            Arrays.fill(s, col);
            str.append(StringColumn.create("",s));
            for(int i=0;i<t.rowCount();i++){
                dd.append(toDouble(t.column(col).get(i)));
            }
        }
        p.addColumns(str, dd);
        Plot.show(BoxPlot.create("Boxplots", p,"Name","Value"));
    }

    static void boxplot(Table t, String column){
        boxplot(t, new String[]{column});
    }

    static void histogram(Table t, String column){
        HistogramTrace h = HistogramTrace.builder(t.numberColumn(column)).
                name("Distribution of " + column).build();
        Axis x = Axis.builder().title(column).build();
        Axis y = Axis.builder().title("Count").build();
        Layout l = Layout.builder().title("Distribution of " + column).barMode(Layout.BarMode.OVERLAY).xAxis(x).yAxis(y)
                .build();
        Figure f = new Figure(l, h);
        Plot.show(f);
    }

    static void scatterPlot(Table t, String col1, String col2){
        Figure f = ScatterPlot.create("Scatter Plot " + col1 + "/" + col2, t, col1, col2);
        Plot.show(f);
    }

    private static Double toDouble(Object o){
        if(o instanceof Integer){
            Integer i = (Integer)o;
            return Double.valueOf(i);
        }
        else{
            return (Double) o;
        }
    }

    static double mean(double[] a){
        double mean = 0;
        for(int i=0;i<a.length;i++){
            mean += a[i];
        }
        mean /= a.length;
        return mean;
    }
    static double var(double[] a, double mean){
        double var = 0;
        double t = 0;
        for(int i=0;i<a.length;i++){
            t = a[i] - mean;
            var += t*t;
        }
        var /= a.length;
        return var;
    }
    static double covariance(double[] a, double[] b, double mean_a, double mean_b) {
        double covar = 0;
        for(int i=0;i<a.length;i++){
            covar += (a[i] - mean_a)*(b[i] - mean_b);
        }
        covar /= a.length;
        return covar;
    }
     static double correlation(Table t, String col1, String col2){
        double[] ncol1 = t.numberColumn(col1).asDoubleArray();
        double[] ncol2 = t.numberColumn(col2).asDoubleArray();
        double mean1 = mean(ncol1);
        double mean2 = mean(ncol2);
        double pearson_cor = covariance(ncol1, ncol2, mean1, mean2)/(Math.sqrt(var(ncol1, mean1)*var(ncol2, mean2)));
        return pearson_cor;
     }
}
