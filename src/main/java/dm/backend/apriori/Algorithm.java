package dm.backend.apriori;

import java.util.*;

public class Algorithm {
    public static IntValueSet[] apriori(AprioriStruct aps, double minSup){
        HashSet<IntValueSet> frequentPatterns = new HashSet<>();
        int k = 1;
        int minSupInt = (int) Math.round(minSup*aps.length);
        HashMap<IntValueSet, Integer> count = Algorithm.firstCount(aps);
        ArrayList<IntValueSet> toRemove = new ArrayList<>();
        for (Map.Entry<IntValueSet, Integer> entry :
                count.entrySet()) {
                if (entry.getValue() < minSupInt) {
                    toRemove.add(entry.getKey());
                }
            }
        count.keySet().removeAll(toRemove);
        frequentPatterns.addAll(count.keySet());
        IntValueSet[] l = count.keySet().toArray(new IntValueSet[0]);

        while(true){
            HashMap<IntValueSet, Integer> newCandidates = new HashMap<>();
            for (int i = 0; i < l.length; i++) {
                for (int j = i+1; j < l.length; j++) {
                    IntValueSet copy = new IntValueSet(l[i]);
                    boolean subsetsAreAllFrequent = true;
                    copy.addAll(l[j]);
                    if(copy.size() == k+1){
                        for (Subsets it = new Subsets(copy); it.hasNext(); ) {
                            IntValueSet ivs = it.next();
                            if(!frequentPatterns.contains(ivs)){
                                subsetsAreAllFrequent = false;
                                break;
                            }
                        }
                        if(subsetsAreAllFrequent) {
                            newCandidates.put(copy, 0);
                        }
                    }
                }
            }
            for (IntValueSet ivs :
                    aps.getAll()) {
                for (Map.Entry<IntValueSet, Integer> entry :
                        newCandidates.entrySet()) {
                    if(ivs.containsAll(entry.getKey())){
                        entry.setValue(entry.getValue() + 1);
                    }
                }
            }
            toRemove = new ArrayList<>();
            for (Map.Entry<IntValueSet, Integer> entry :
                    newCandidates.entrySet()){
                if(entry.getValue() < minSupInt){
                    toRemove.add(entry.getKey());
                }
            }
            newCandidates.keySet().removeAll(toRemove);
            if(newCandidates.size() == 0){
                break;
            }
            l = newCandidates.keySet().toArray(new IntValueSet[0]);
            frequentPatterns.addAll(newCandidates.keySet());
            k++;
        }
        return frequentPatterns.toArray(new IntValueSet[0]);
    }

    public static IntValueSet[] apriori(AprioriStruct aps, int minSup){
        double minSupDouble = (double) minSup / aps.length;
        return apriori(aps, minSupDouble);
    }

    private static HashMap<IntValueSet, Integer> firstCount(AprioriStruct aps){
        HashMap<IntValue, Integer> count = new HashMap<>();
        for (IntValueSet ivs :
                aps.getAll()) {
            for (IntValue iv:
                    ivs) {
                if(!count.containsKey(iv)) {
                    count.put(iv, 1);
                }
                else{
                    count.put(iv, count.get(iv) + 1);
                }
            }
        }
        HashMap<IntValueSet, Integer> count2 = new HashMap<>();
        for (Map.Entry<IntValue, Integer> entry : count.entrySet()) {
            IntValueSet ivs = new IntValueSet();
            ivs.add(entry.getKey());
            count2.put(ivs, entry.getValue());
        }
        return count2;
    }
}
