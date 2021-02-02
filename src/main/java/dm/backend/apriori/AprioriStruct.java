package dm.backend.apriori;

import dm.backend.table.Table;

import java.util.HashMap;
import java.util.HashSet;

public class AprioriStruct {
    private IntValueSet[] sets;
    public int length;

    public AprioriStruct(Table t){
        int h = t.height(), w = t.width();
        length = h;
        sets = new IntValueSet[h];
        for (int i = 0; i < sets.length; i++) {
            sets[i] = new IntValueSet();
        }
        for(int i=0;i<w;i++){
            for(int j=0;j<h;j++){
                sets[j].add(new IntValue(i, t.intColumn(i).get(j)));
            }
        }
    }

    public AprioriStruct(IntValueSet[] sets){
        length = sets.length;
        this.sets = sets;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < sets.length; i++) {
            s.append(sets[i]).append("\n");
        }
        return s.toString();
    }

    public IntValueSet get(int i){
        return sets[i];
    }

    public IntValueSet[] getAll(){
        return sets;
    }
}
