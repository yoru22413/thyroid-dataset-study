package dm.backend.table;

public abstract class Column {
    String name;
    Column(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static IntegerColumn discretization(IntegerColumn col, int n){
        IntegerColumn res = new IntegerColumn(new int[col.size()], col.name);
        double max = (double) col.max();
        double min = (double) col.min();
        double step = (max - min)/n;
        double x;
        for(int i=0;i<col.size();i++){
            x = (double)col.get(i);
            res.set(i, (int) Math.floor((x - min)/step));
        }
        return res;
    }

    public abstract int size();
    public abstract Number get(int i);
    public abstract void set(int i, Number x);
    public abstract Number max();
    public abstract Number min();
    public abstract IntegerColumn discretization(int n);
    public Column toIntegerColumn(){
        return null;
    }
    public Column toDoubleColumn(){
        return null;
    }
}
