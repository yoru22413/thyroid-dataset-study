import tech.tablesaw.api.Table;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Table t = Utils.readData();
        System.out.println(t);
        System.out.println(Utils.mode(t, "t3_resin"));
        System.out.println(Utils.mode(t, "total_thyroxin"));
        System.out.println(Utils.mode(t, "total_triio"));
        System.out.println(Utils.meanMedian(t));
    }
}
