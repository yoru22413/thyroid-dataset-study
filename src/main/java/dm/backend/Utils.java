package dm.backend;

import dm.backend.table.Table;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class Utils {
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

}
