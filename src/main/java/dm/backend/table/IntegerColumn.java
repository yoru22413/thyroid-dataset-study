package dm.backend.table;

public class IntegerColumn extends Column{
    private final int[] data;

    public IntegerColumn(int[] data, String name){
        super(name);
        this.data = data;
    }

    public IntegerColumn(String[] data, String name){
        super(name);
        this.data = new int[data.length];
        for(int i=0;i<data.length;i++){
            this.data[i] = Integer.parseInt(data[i]);
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

    public Integer min(){
        int min = data[0];
        for(int i=1;i<data.length;i++){
            if (data[i] < min){
                min = data[i];
            }
        }
        return min;
    }

    public Integer max(){
        int max = data[0];
        for(int i=1;i<data.length;i++){
            if (data[i] > max){
                max = data[i];
            }
        }
        return max;
    }

    @Override
    public int size() {
        return data.length;
    }

    @Override
    public Integer get(int i) {
        return data[i];
    }

    @Override
    public void set(int i, Number x) {
        data[i] = (int) x;
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
        double max = (double) max();
        double min = (double) min();
        double step = (max - min)/n;
        for(int i=0;i<data.length;i++){
            res.set(i, (int) Math.floor((data[i] - min)/step));
        }
        return res;
    }

    public DoubleColumn toDoubleColumn(){
        DoubleColumn col = new DoubleColumn(new double[data.length], name);
        for (int i = 0; i < data.length; i++) {
            col.set(i, get(i));
        }
        return col;
    }

    public IntegerColumn toIntegerColumn(){
        return this;
    }
}
