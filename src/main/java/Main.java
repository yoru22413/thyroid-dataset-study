import tech.tablesaw.api.Table;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Table t = Utils.readData();
        System.out.println(t);
        System.out.println(Utils.meanMedianMode(t));
        Utils.scatterPlot(t, "total_triio", "total_thyroxin");
        System.out.println(Utils.correlation(t, "total_triio", "total_thyroxin"));
    }
}
