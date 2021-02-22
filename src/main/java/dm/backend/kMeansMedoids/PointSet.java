package dm.backend.kMeansMedoids;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PointSet {
    //attributs..
    private ArrayList<Point> points;
    private int nbrInstances;
    private int nbrFeatures;
    private int k;
    private ArrayList<ArrayList<Point>> c_clusters;
    private ArrayList<ArrayList<Point>> m_clusters;
    private ArrayList<Point> centroids;
    private ArrayList<Point> medoids;

    //the arrays you asked for..
    public ArrayList<Integer> c_cluster_id;
    public ArrayList<Integer> m_cluster_id;
    private float cost;

    //init..
    public PointSet(ArrayList<Point> points, int k){
        this.points = points;
        this.nbrInstances = points.size();
        this.nbrFeatures = points.get(0).getNbrFeatures();
        this.k = k;
        this.c_clusters = this.initClusters();
        this.m_clusters = this.initClusters();
        this.centroids = this.generateRandomCentroids();
        this.medoids = this.generateRandomCentroids();
        this.c_cluster_id = new ArrayList<>();
        this.m_cluster_id = new ArrayList<>();
    }

    //getters..
    public ArrayList<Point> getPoints() {
        return points;
    }

    public int getNbrInstances() {
        return nbrInstances;
    }

    public int getNbrFeatures() {
        return nbrFeatures;
    }

    public ArrayList<ArrayList<Point>> getC_clusters() {
        return c_clusters;
    }

    public ArrayList<ArrayList<Point>> getM_clusters() {
        return m_clusters;
    }

    public ArrayList<Point> getCentroids() {
        return centroids;
    }

    public ArrayList<Point> getMedoids() {
        return medoids;
    }

    public int getK() {
        return k;
    }

    public ArrayList<Integer> getC_cluster_id() {
        return c_cluster_id;
    }

    public ArrayList<Integer> getM_cluster_id() {
        return m_cluster_id;
    }

    //setters..
    public void setPoints(ArrayList<Point> points) {
        this.points = points;
    }

    public void setNbrInstances(int nbrInstances) {
        this.nbrInstances = nbrInstances;
    }

    public void setNbrFeatures(int nbrFeatures) {
        this.nbrFeatures = nbrFeatures;
    }

    public void setC_clusters(ArrayList<ArrayList<Point>> c_clusters) {
        this.c_clusters = c_clusters;
    }

    public void setM_clusters(ArrayList<ArrayList<Point>> m_clusters) {
        this.m_clusters = m_clusters;
    }

    public void setCentroids(ArrayList<Point> centroids) {
        this.centroids = centroids;
    }

    public void setMedoids(ArrayList<Point> medoids) {
        this.medoids = medoids;
    }

    public void setK(int k) {
        this.k = k;
    }

    public void setC_cluster_id(ArrayList<Integer> c_cluster_id) {
        this.c_cluster_id = c_cluster_id;
    }

    public void setM_cluster_id(ArrayList<Integer> m_cluster_id) {
        this.m_cluster_id = m_cluster_id;
    }

    /************Methods************/
    public float cost(ArrayList<Point> points, ArrayList<ArrayList<Point>> clusters){
        float cost = 0;
        for(int i = 0; i < this.k; i++){
            for(Point point : clusters.get(i)) {
                //points is either the centroids or the medoids set depending on the method calling..
                cost += points.get(i).absoluteDistance(point);
            }
        }
        return cost;
    }

    //calculates the nearest point to a given point for centroids/medoids selection, returns its index..
    public int nearestPoint(Point point, ArrayList<Point> points){
        int p, min;

        List<Integer> distanceVect = new ArrayList<>();

        for(Point x : points){
            distanceVect.add(point.absoluteDistance(x));
        }

        min = Collections.min(distanceVect);
        p = distanceVect.indexOf(min);
        return p;
    }

    public ArrayList<Float> mean(ArrayList<Point> points){
        ArrayList<Float> featuresC = new ArrayList<>();
        int nbrInstances = points.size();
        float m;

        for(int i = 0; i < this.nbrFeatures; i++){
            m = 0;
            for(Point point : points){
                m += point.getFeatures().get(i);
            }
            m /= nbrInstances;
            featuresC.add(m);
        }
        return featuresC;
    }

    public void k_means(){
        float distance, cost, newCost;
        int c;

        Point newCentroid;
        ArrayList<Point> newCentroids;

        while(true){
            //Affects every point to the nearest centroids's cluster..
            this.c_clusters = this.initClusters();
            for(Point point : this.points){
              c = this.nearestPoint(point, this.centroids);
              point.setC_cluster(c);
              this.c_clusters.get(c).add(point);
            }

            cost = this.cost(this.centroids, this.c_clusters);

            //Calculates every mean point of the previous centroids clusters and takes them as the new centroids..
            newCentroids = new ArrayList<>();
            for(ArrayList<Point> cluster : this.c_clusters){
                newCentroid = new Point(this.mean(cluster));
                newCentroids.add(newCentroid);
            }

            newCost = this.cost(newCentroids, this.c_clusters);

            //if the cost doesn't change three times in a row (centroids stagnation) the search stops..
            distance = cost - newCost;

            this.centroids = new ArrayList<>(newCentroids);

            if(distance == 0)
                this.cost = cost;
                break;
        }

        for(Point point : this.points){
            this.c_cluster_id.add((point.getC_cluster()));
        }
    }

    public void k_medoids() {
        float cost, newCost, distance;
        int c, randomCluster, stgn = 0;

        while (true) {
            //Affects every point to the nearest medoid's cluster..
            this.m_clusters = this.initClusters();
            for (Point point : this.points) {
                c = this.nearestPoint(point, this.medoids);
                point.setM_cluster(c);
                this.m_clusters.get(c).add(point);
            }

            cost = this.cost(this.medoids, this.m_clusters);

            //selects a random medoid's cluster to replace the former..
            randomCluster = ThreadLocalRandom.current().nextInt(0, this.k);

            //bestMedoid takes the selected cluster,
            //replaces its medoid with the point that minimizes the cost the most if it exists
            //and returns the new current cost..
            newCost = this.bestMedoid(randomCluster, cost);

            //if the cost doesn't change three times in a row (medoids stagnation) the search stops..
            distance = cost - newCost;
            if(distance == 0)
                this.cost = cost;
                break;

        }

        for(Point point : this.points){
            this.m_cluster_id.add((point.getM_cluster()));
        }
    }

    public float bestMedoid(int clusterIndex, float currentCost){
        int m;
        float min;
        List<Float> cosTemp = new ArrayList<>();
        ArrayList<Point> cluster = this.m_clusters.get(clusterIndex), medoidsTemp;

        //calculates and stocks the cost after every replacement..
        for(Point point : cluster) {
            medoidsTemp = new ArrayList<>(this.medoids);
            medoidsTemp.set(clusterIndex, point);
            cosTemp.add(this.cost(this.medoids, this.m_clusters));
        }

        //takes the minimum cost and its corresponding medoid..
        min = Collections.min(cosTemp);
        m = cosTemp.indexOf(min);

        //replaces the selected medoid if cost minimized
        //or the same cost but different new medoid
        //else return the current cost..
        if(min < currentCost || ((min == currentCost) && !cluster.get(m).equals(this.medoids.get(clusterIndex)))){
            medoidsTemp = new ArrayList<>(this.medoids);
            medoidsTemp.set(clusterIndex, cluster.get(m));
            this.medoids = new ArrayList<>(medoidsTemp);
        }
        else
            min = currentCost;
        return min;
    }

    /************Utils************/
    //takes random points from the pointset for centroids and medoids initialisation..
    public ArrayList<Point> generateRandomCentroids(){
        ArrayList<Point> centroids = new ArrayList<>();
        Set<Integer> randoms = new HashSet<>();
        int i = 0, random;
        while(i < this.k){
            random = ThreadLocalRandom.current().nextInt(0, this.nbrInstances);
            if(randoms.add(random)) {
                centroids.add(this.points.get(random));
                i++;
            }
        }
        return centroids;
    }

    public ArrayList<ArrayList<Point>> initClusters(){
        ArrayList<Point> points;
        ArrayList<ArrayList<Point>> clusters = new ArrayList<>();
        for(int i = 0; i < this.k; i++){
            points = new ArrayList<>();
            clusters.add(points);
        }
        return clusters;
    }

    public void printClusters(int cm){
        ArrayList<ArrayList<Point>> clusters;
        if(cm == 0)
            clusters = this.c_clusters;
        else
            clusters = this.m_clusters;
        int nbrClusters = clusters.size(), clusterSize, totalSize = 0;
        ArrayList<Point> cluster;
        for(int i = 0; i < nbrClusters; i++) {
            cluster = clusters.get(i);
            clusterSize = cluster.size();
            totalSize += clusterSize;
            System.out.println("\n-----------------------------------------");
            System.out.println("Cluster {"+(i+1)+"}: SIZE: "+clusterSize);
            for(Point point : cluster){
                System.out.println(point.getFeatures());
            }
            System.out.println("-----------------------------------------");
        }
        System.out.println("TotalSize: "+totalSize);
    }

    public void cluster_id_print(int cm){
        ArrayList<Integer> cluster_id;
        if(cm == 0)
            cluster_id = this.c_cluster_id;
        else
            cluster_id = this.m_cluster_id;

        for(int i = 0; i < this.nbrInstances; i++){
            System.out.print("\t"+cluster_id.get(i));
        }
    }

}
