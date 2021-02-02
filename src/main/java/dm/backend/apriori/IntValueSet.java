package dm.backend.apriori;

import java.util.Arrays;
import java.util.HashSet;

public class IntValueSet extends HashSet<IntValue>{

    public IntValueSet(IntValueSet intValues) {
        super(intValues);
    }

    public IntValueSet(IntValue[] intValues) {
        super(Arrays.asList(intValues));
    }

    public IntValueSet() {

    }

}
