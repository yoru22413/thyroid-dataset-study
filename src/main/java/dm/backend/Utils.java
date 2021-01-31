package dm.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Utils {
    class Table{
        private final Object[] columns;
        private final Class[] types;

        Table(Object[] columns, Class[] types){
            this.columns = columns;
            this.types = types;
        }
    }
    private static Object convert(String s, int n){
        switch (n){
            case 0: case 1:
                return Integer.parseInt(s);

            case 2: case 3: case 4:
                return Double.parseDouble(s);

            default:
                return null;
        }
    }

    public static void printCol(ArrayList<Object[]> table, int idx){
        for(Object o : table.get(idx)){
            System.out.print(o+", ");
        }
    }

    public static ArrayList<Object[]> readData(String path){
        URL url = Utils.class.getClassLoader().getResource(path);
        BufferedReader br;
        ArrayList<Integer> col1 = new ArrayList<>();
        ArrayList<Integer> col2 = new ArrayList<>();
        ArrayList<Double> col3 = new ArrayList<>();
        ArrayList<Double> col4 = new ArrayList<>();
        ArrayList<Double> col5 = new ArrayList<>();
        ArrayList<Double> col6 = new ArrayList<>();
        ArrayList[] cols = {col1, col2, col3, col4, col5, col6};
        try {
            assert url != null;
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = br.readLine();
            while(line != null){
                String[] tokens = line.split(",");
                for(int i=0;i<tokens.length;i++){
                    cols[i].add(Utils.convert(tokens[i], i));
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<Object[]> res = new ArrayList<>();
        for(int i=0;i<cols.length;i++){
            res.add(cols[i].toArray());
        }
        return res;
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

    public static double correlation(double[] col1, double[] col2){
    double mean1 = mean(col1);
    double mean2 = mean(col2);
    double pearson_cor = covariance(col1, col2, mean1, mean2)/(Math.sqrt(var(col1, mean1)*var(col2, mean2)));
    return pearson_cor;
    }

    public static double max(Double[] col){
        double max = col[0];
        for(int i=1;i<col.length;i++){
            if (col[i] > max){
                max = col[i];
            }
        }
        return max;
    }
    public static double max(Integer[] col){
        double max = col[0];
        for(int i=1;i<col.length;i++){
            if (col[i] > max){
                max = col[i];
            }
        }
        return max;
    }



    public static double min(Double[] col){
        double min = col[0];
        for(int i=1;i<col.length;i++){
            if (col[i] < min){
                min = col[i];
            }
        }
        return min;
    }

    public static double min(Integer[] col){
        double min = col[0];
        for(int i=1;i<col.length;i++){
            if (col[i] < min){
                min = col[i];
            }
        }
        return min;
    }



    public static int[] discretization(Double[] col, int n){
        int[] res = new int[col.length];
        double max = Utils.max(col);
        double min = Utils.min(col);
        double step = (max - min)/n;
        for(int i=0;i<col.length;i++){
            res[i] = (int) Math.floor((col[i] - min)/step);
        }
        return res;
    }

    public static int[] discretization(Integer[] col, int n){
        int[] res = new int[col.length];
        double max = Utils.max(col);
        double min = Utils.min(col);
        double step = (max - min)/n;
        for(int i=0;i<col.length;i++){
            res[i] = (int) Math.floor((col[i] - min)/step);
        }
        return res;
    }

    public static Object[] getRow(Object[][] table, int n){
        Object[] t = new Object[table.length];
        for(int i=0;i<t.length;i++){
            t[i] = table[i][n];
        }
        return t;
    }

    public static ArrayList<Integer> apriori(ArrayList<Integer[]> table){
        String[][] l = new String[table.size()][table.get(0).length];
        for(int i=0;i<table.size();i++){
            Integer[] col = table.get(i);
            for(int j=0;j<col.length;j++){
                l[i][j] = "C"+ i + "_" + col[j];
            }
        }
        System.out.println(Arrays.toString(Utils.getRow(l, 1)));
        return null;
    }

    public static Integer[] object2intarray(Object[] t){
        Integer[] res = new Integer[t.length];
        for(int i=0;i<t.length;i++){
            res[i] = (Integer)t[i];
        }
        return res;
    }

    public static Double[] object2doublearray(Object[] t){
        Double[] res = new Double[t.length];
        for(int i=0;i<t.length;i++){
            res[i] = (Double)t[i];
        }
        return res;
    }
}
