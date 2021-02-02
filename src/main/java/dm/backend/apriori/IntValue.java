package dm.backend.apriori;

public class IntValue {
    int index;
    int value;

    public IntValue(int index, int value) {
        this.index = index;
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IntValue)){
            return false;
        }
        IntValue nv = (IntValue)obj;
        return nv.index == index && nv.value == value;
    }

    @Override
    public String toString() {
        return "(" + index + ", " + value + ")";
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(index)+Integer.hashCode(value);
    }
}
