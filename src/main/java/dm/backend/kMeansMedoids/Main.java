package dm.backend.kMeansMedoids;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        long start, end;
        ArrayList<Point> points = new ArrayList<>();
        ArrayList<Float> features;
        Point point;
        PointSet ps;
        int k = 3;
        String path = "C:\\Users\\HALIMA\\Desktop\\DM\\Thyroid_Dataset.txt";

        try {
            InputStream is = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                features = new ArrayList<>();
                String[] featureS = line.split("\\,");
                for(String feature : featureS){
                    features.add(Float.valueOf(feature));
                }

                features.remove(0);
                point = new Point(features);
                points.add(point);
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        ps = new PointSet(points, k);
        start = System.nanoTime();
        //ps.k_means(0);
        //ps.printClusters(0);
        //ps.cluster_id_print(0);

        //ps.k_medoids(0);
        //ps.printClusters(1);
        //ps.cluster_id_print(1);
        end = System.nanoTime();

        System.out.println("Execution Time: " + (end - start)/(1e+9)+"s...");
    }
    }