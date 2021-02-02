package dm.backend.apriori;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Subsets implements Iterator {
    private long i = 1;
    private long max;
    IntValue[] elements;
    public Subsets(Set set){
        elements = (IntValue[]) set.toArray(new IntValue[0]);
        max = 1 << elements.length;
    }
    @Override
    public boolean hasNext() {
        return i < max-1;
    }

    @Override
    public IntValueSet next() {
        IntValueSet set = new IntValueSet();
        String s = Long.toBinaryString(i);
        int o = elements.length - s.length();
        for (int j = 0; j < s.length(); j++) {
            if(s.charAt(j) == '1'){
                set.add(elements[j + o]);
            }
        }
        i++;
        return set;
    }
}
