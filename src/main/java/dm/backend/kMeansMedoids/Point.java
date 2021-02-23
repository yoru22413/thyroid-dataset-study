package dm.backend.kMeansMedoids;

import java.util.ArrayList;

public class Point {
    //attributs..
    private ArrayList<Float> features;
    private int nbrFeatures;
    private int c_cluster;
    private int m_cluster;

    //init..
    public Point(){
        this.features = new ArrayList<>();
        this.nbrFeatures = 0;
        this.c_cluster = 0;
        this.m_cluster = 0;
    }

    public Point(ArrayList<Float> features){
        this.features = features;
        this.nbrFeatures = features.size();
        this.c_cluster = 0;
        this.m_cluster = 0;
    }

    //getters..
    public ArrayList<Float> getFeatures(){
        return this.features;
    }

    public int getNbrFeatures(){
        return this.nbrFeatures;
    }

    public int getC_cluster() {
        return c_cluster;
    }

    public int getM_cluster() {
        return m_cluster;
    }

    //setters..
    public void setFeatures(ArrayList<Float> features) {
        this.features = features;
    }

    public void setNbrFeatures(int nbrFeatures) {
        this.nbrFeatures = nbrFeatures;
    }

    public void setC_cluster(int c_cluster) {
        this.c_cluster = c_cluster;
    }

    public void setM_cluster(int m_cluster) {
        this.m_cluster = m_cluster;
    }

    //methods..
    public float absoluteDistance(Point p){
        float ed = 0;
        for(int i = 0; i < this.nbrFeatures; i++){
            ed += Math.abs(this.features.get(i) - p.getFeatures().get(i));
        }
        return ed;
    }

}
