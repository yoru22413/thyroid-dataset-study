package dm.backend.table;

public class DoubleColumn extends Column{
    private final double[] data;

    public DoubleColumn(double[] data, String name){
        super(name);
        this.data = data;
    }
    public DoubleColumn(String[] data, String name){
        super(name);
        this.data = new double[data.length];
        for(int i=0;i<data.length;i++){
            this.data[i] = Double.parseDouble(data[i]);
        }
    }

    public double mean(){
        double mean = 0;
        for(int i=0;i<data.length;i++){
            mean += data[i];
        }
        mean /= data.length;
        return mean;
    }

    public double var(double mean){
        double var = 0;
        double t = 0;
        for(int i=0;i<data.length;i++){
            t = data[i] - mean;
            var += t*t;
        }
        var /= data.length;
        return var;
    }

    public double var(){
        double mean = mean();
        double var = 0;
        double t = 0;
        for(int i=0;i<data.length;i++){
            t = data[i] - mean;
            var += t*t;
        }
        var /= data.length;
        return var;
    }

    public Double min(){
        double min = data[0];
        for(int i=1;i<data.length;i++){
            if (data[i] < min){
                min = data[i];
            }
        }
        return min;
    }

    public Double max(){
        double max = data[0];
        for(int i=1;i<data.length;i++){
            if (data[i] > max){
                max = data[i];
            }
        }
        return max;
    }

    public int size() {
        return data.length;
    }

    @Override
    public Double get(int i) {
        return data[i];
    }

    @Override
    public void set(int i, Number x) {
        data[i] = (double) x;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(int i=0;i<data.length-1;i++){
            s.append(data[i]).append(", ");
        }
        s.append(data[data.length-1]);
        return s.toString();
    }

    public IntegerColumn discretization(int n){
        IntegerColumn res = new IntegerColumn(new int[data.length], name);
        double max = max();
        double min = min();
        double step = (max - min)/n;
        double x;
        for(int i=0;i<data.length;i++){
            x = get(i);
            if(x == max){
                res.set(i, n-1);
            }
            else {
                res.set(i, (int) Math.floor((x - min) / step));
            }
        }
        return res;
    }

    public IntegerColumn toIntegerColumn(){
        IntegerColumn col = new IntegerColumn(new int[data.length], name);
        for (int i = 0; i < data.length; i++) {
            col.set(i, get(i).intValue());
        }
        return col;
    }

    public DoubleColumn toDoubleColumn(){
        return this;
    }
}
