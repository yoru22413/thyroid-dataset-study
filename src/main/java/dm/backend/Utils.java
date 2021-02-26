package dm.backend;

import java.util.Arrays;


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

    public static double computeMetrics(int[] classColumnData, int[] clusterIndexPoints){
        classColumnData = Arrays.copyOf(classColumnData, classColumnData.length);
        int numClusters = 0;
        for (int i = 0; i < clusterIndexPoints.length; i++) {
            if (numClusters <= clusterIndexPoints[i]){
                numClusters++;
            }
        }

        int numClasses = 0;
        for (int i = 0; i < classColumnData.length; i++) {
            if (numClasses <= classColumnData[i]){
                numClasses++;
            }
        }

        int[] clusters = new int[numClusters];
        for (int i = 0; i < clusters.length; i++) {
            clusters[i] = i;
        }
        int[] countClass = new int[numClasses];
        int[] countCluster = new int[numClusters];
        for (int i = 0; i < classColumnData.length; i++) {
            countClass[classColumnData[i]]++;
        }
        for (int i = 0; i < clusterIndexPoints.length; i++) {
            countCluster[clusterIndexPoints[i]]++;
        }
        double[] results = new double[numClasses];
        for (int i = 0; i < numClasses; i++) {
            double maxF1score = -1;
            for (int j = 0; j < numClusters; j++) {
                int[] countCC = new int[numClasses];
                for (int k = 0; k < clusterIndexPoints.length; k++) {
                    if(clusterIndexPoints[k] == j){
                        countCC[classColumnData[k]]++;
                    }
                }
                double precision = (double)countCC[i]/countCluster[j];
                double recall = (double)countCC[i]/countClass[i];
                double f1score = 2*precision*recall/(precision+recall);
                if(Double.isNaN(f1score)){
                    f1score = 0;
                }
                if (f1score > maxF1score){
                    maxF1score = f1score;
                }
            }
            results[i] = maxF1score;
        }
        double sum = 0;
        for (int i = 0; i < results.length; i++) {
                sum += (double) countClass[i]/classColumnData.length * results[i];
        }
        return sum;
    }
}
