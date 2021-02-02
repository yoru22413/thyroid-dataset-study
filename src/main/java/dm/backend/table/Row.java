package dm.backend.table;

import java.util.Arrays;

public class Row{
    Object[] data;

    public Row(Object[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return Arrays.toString(data);
    }
}
