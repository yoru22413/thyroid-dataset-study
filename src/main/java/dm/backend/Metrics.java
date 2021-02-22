package dm.backend;

import java.util.Arrays;

public class Metrics {
    public int[] interpretation;
    public double fmeasure, precision, recall;

    public Metrics(int[] interpretation, double fmeasure, double precision, double recall) {
        this.interpretation = interpretation;
        this.fmeasure = fmeasure;
        this.precision = precision;
        this.recall = recall;
    }

    @Override
    public String toString() {
        return Arrays.toString(interpretation) + " F1=" + fmeasure + " Precision=" + precision + " Recall=" + recall;
    }
}
