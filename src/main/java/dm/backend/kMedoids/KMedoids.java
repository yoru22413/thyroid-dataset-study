package dm.backend.kMedoids;


import dm.backend.table.IntegerColumn;
import dm.backend.table.Row;
import dm.backend.table.Table;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class KMedoids {
    private final int numClusters;
    public Table table;
    public int[] indexMedoids;
    public int[] indexPoints;
    public double cost = 0;

    public KMedoids(Table table, int numClusters) {
        this.table = table;
        this.numClusters = numClusters;
        for (int i = 0; i < table.width(); i++) {
            table.setColumn(i, table.column(i).toDoubleColumn());
        }
    }

    private static double distance(Row r1, Row r2){
        double d = 0;
        Object[] d1 = r1.getData();
        Object[] d2 = r2.getData();
        for (int i = 0; i < r1.getData().length; i++) {
            d += Math.abs((double)d1[i] - (double)d2[i]);
        }
        return d;
    }

    private static int[] index(ArrayList<Row> points, ArrayList<Row> medoids){
        int[] index = new int[points.size()];
        double t;
        for (int i = 0; i < index.length; i++) {
            double minD = distance(points.get(i), medoids.get(0));
            int argmin = 0;
            int j;
            for (j = 1; j < medoids.size(); j++) {
                t = distance(points.get(i), medoids.get(j));
                if(t < minD){
                    minD = t;
                    argmin = j;
                }
            }
            index[i] = argmin;
        }
        return index;
    }

    public static double cost(ArrayList<Row> points, ArrayList<Row> medoids){
        int[] index = index(points, medoids);
        double cost = 0;
        for (int i = 0; i < index.length; i++) {
            cost += distance(points.get(i), medoids.get(index[i]));
        }
        return cost;
    }

    public static <T> ArrayList<T> pickSample(ArrayList<T> population, int nSamplesNeeded, Random r) {
        ArrayList<T> ret = new ArrayList<>();
        ArrayList<Integer> index = new ArrayList<>();
        for (int i = 0; i < population.size(); i++) {
            index.add(i);
        }
        while (nSamplesNeeded > 0) {
            int rand = r.nextInt(index.size());
            ret.add(population.get(index.get(rand)));
            index.remove(index.remove(rand));
            nSamplesNeeded--;
        }
        return ret;
    }

    public static <T> T pickOneElement(ArrayList<T> population, Random r){
        return population.get(r.nextInt(population.size()));
    }

    public void run(){
        ArrayList<Row> bestCopy = null;
        double minCost = -1;
        Random random = new Random();
        ArrayList<Row> pointsO = new ArrayList<>();
        for (int i = 0; i < table.height(); i++) {
            pointsO.add(table.getRow(i));
        }
        ArrayList<Row> points = new ArrayList<>(pointsO);
        ArrayList<Row> medoids = pickSample(points, numClusters, random);
        points.removeAll(medoids);
        double costGlobalBefore = 0;
        double cost = 0, cost_after_replace = 0;
        while(costGlobalBefore > minCost) {
            costGlobalBefore = cost(points, medoids);
            for (int i = 0; i < medoids.size(); i++) {
                for (int j = 0; j < points.size(); j++) {
                    Row medoid = medoids.get(i);
                    Row neighbor = points.get(j);
                    cost = cost(points, medoids);
                    points.add(medoid);
                    points.remove(neighbor);
                    medoids.remove(medoid);
                    medoids.add(neighbor);
                    cost_after_replace = cost(points, medoids);
                    if(cost <= cost_after_replace){
                        points.add(neighbor);
                        points.remove(medoid);
                        medoids.add(medoid);
                        medoids.remove(neighbor);
                    }
                    else{
                        bestCopy = new ArrayList<>(medoids);
                        minCost = cost_after_replace;
                    }
                }
            }
        }
        indexMedoids = new int[numClusters];
        for (int i = 0; i < numClusters; i++) {
            indexMedoids[i] = pointsO.indexOf(bestCopy.get(i));
        }
        indexPoints = index(pointsO, bestCopy);
        this.cost = minCost;
    }
}
