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

    public static Metrics computeMetrics(int[] classColumnData, int[] clusterIndexPoints){
        classColumnData = Arrays.copyOf(classColumnData, classColumnData.length);
        int[] clusters = {0,1,2};
        double bestF1 = -1;
        double bestPrecision = 0, bestRecall = 0;
        int[] bestInterpretation = null;
        int[] count = new int[3];
        for (int i = 0; i < classColumnData.length; i++) {
            classColumnData[i]--;
            count[classColumnData[i]]++;
        }

        for (int[] interpretation :
                Permutations.generate(clusters)) {
            int[] TP = new int[3], TN = new int[3], FP = new int[3], FN = new int[3];
            for (int i = 0; i < classColumnData.length; i++) {
                int classId = classColumnData[i], predicted = interpretation[clusterIndexPoints[i]];
                if(classId == predicted){
                    TP[classId]++;
                    TN[(classId+1)%3]++;
                    TN[(classId+2)%3]++;
                }
                if(classId != predicted){
                    FP[predicted]++;
                    FN[classId]++;
                    for (int j = 0; j < 3; j++) {
                        if(j != predicted && j != classId){
                            TN[j]++;
                            break;
                        }
                    }
                }
            }
            double meanF1 = 0;
            double meanPrecision = 0;
            double meanRecall = 0;

            for (int i = 0; i < 3; i++) {
                double precision = (double) TP[i] / (TP[i] + FP[i]);
                double recall = (double) TP[i] / (TP[i] + FN[i]);
                double fmeasure = 2*precision*recall/(precision + recall);
                meanF1 += fmeasure * count[i];
                meanPrecision += precision * count[i];
                meanRecall += recall * count[i];
            }
            meanF1 /= classColumnData.length;
            meanPrecision /= classColumnData.length;
            meanRecall /= classColumnData.length;
            if(Double.isNaN(meanF1)){
                meanF1 = 0;
            }

            if(meanF1 > bestF1){
                bestF1 = meanF1;
                bestInterpretation = interpretation;
                bestPrecision = meanPrecision;
                bestRecall = meanRecall;
            }
        }
        return new Metrics(bestInterpretation, bestF1, bestPrecision, bestRecall);
    }
}
