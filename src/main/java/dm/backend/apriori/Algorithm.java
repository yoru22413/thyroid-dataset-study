package dm.backend.apriori;

import java.util.*;

public class Algorithm {
    public int k;
    public int minSup;
    public HashMap<IntValueSet, Integer> frequentPatterns;
    public ArrayList<IntValueSet> toRemove;
    public IntValueSet[] l;
    public AprioriStruct aps;
    public HashMap<IntValueSet, Integer> count;
    public boolean finished = false;
    public HashMap<IntValueSet, Integer> countCopy;
    public HashSet<AssociationRule> allAssociationRules;
    public HashSet<AssociationRule> toRemoveAR;
    public HashSet<AssociationRule> associationRulesSatisf;

    public double minConf;

    public Algorithm(AprioriStruct aps, int minSup) {
        this.minSup = minSup;
        this.aps = aps;
        k = 0;
        frequentPatterns = new HashMap<>();
        count = new HashMap<>();
    }
    public Algorithm(AprioriStruct aps, double minSup) {
        this(aps, (int)Math.round(minSup*aps.length));
    }

    public void execute(){
        while(!finished){
            step();
        }
    }

    public void step(){
        if(finished){
            try {
                throw new Exception("The Apriori Algorithm is finished!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(k == 0){
            firstCount();
            k++;
            toRemove = new ArrayList<>();
            for (Map.Entry<IntValueSet, Integer> entry :
                    count.entrySet()) {
                if (entry.getValue() < minSup) {
                    toRemove.add(entry.getKey());
                }
            }
            countCopy = new HashMap<>(count);
            count.keySet().removeAll(toRemove);
            frequentPatterns.putAll(count);
            l = count.keySet().toArray(new IntValueSet[0]);
            if(count.size() == 0){
                finished = true;
            }
        }
        else{
            afterFirstStep();
        }

    }

    private void afterFirstStep() {
        count = new HashMap<>();
        for (int i = 0; i < l.length; i++) {
            for (int j = i+1; j < l.length; j++) {
                IntValueSet copy = new IntValueSet(l[i]);
                Set<IntValueSet> fp = frequentPatterns.keySet();
                boolean subsetsAreAllFrequent = true;
                copy.addAll(l[j]);
                if(copy.size() == k+1){
                    for (Subsets it = new Subsets(copy); it.hasNext(); ) {
                        IntValueSet ivs = it.next();
                        if(!fp.contains(ivs)){
                            subsetsAreAllFrequent = false;
                            break;
                        }
                    }
                    if(subsetsAreAllFrequent) {
                        count.put(copy, 0);
                    }
                }
            }
        }
        for (IntValueSet ivs :
                aps.getAll()) {
            for (Map.Entry<IntValueSet, Integer> entry :
                    count.entrySet()) {
                if(ivs.containsAll(entry.getKey())){
                    entry.setValue(entry.getValue() + 1);
                }
            }
        }
        toRemove = new ArrayList<>();
        for (Map.Entry<IntValueSet, Integer> entry :
                count.entrySet()){
            if(entry.getValue() < minSup){
                toRemove.add(entry.getKey());
            }
        }
        countCopy = new HashMap<>(count);
        count.keySet().removeAll(toRemove);
        if(count.size() == 0){
            finished = true;
        }
        l = count.keySet().toArray(new IntValueSet[0]);
        frequentPatterns.putAll(count);
        k++;
    }

    private void firstCount(){
        HashMap<IntValue, Integer> count2 = new HashMap<>();
        for (IntValueSet ivs :
                aps.getAll()) {
            for (IntValue iv:
                    ivs) {
                if(!count2.containsKey(iv)) {
                    count2.put(iv, 1);
                }
                else{
                    count2.put(iv, count2.get(iv) + 1);
                }
            }
        }
        count = new HashMap<>();
        for (Map.Entry<IntValue, Integer> entry : count2.entrySet()) {
            IntValueSet ivs = new IntValueSet();
            ivs.add(entry.getKey());
            count.put(ivs, entry.getValue());
        }
    }

    public void associationRules(){
        toRemoveAR = new HashSet<>();
        allAssociationRules = new HashSet<>();
        for (Map.Entry<IntValueSet, Integer> entry :
                frequentPatterns.entrySet()) {
            for (int i = 1; i < entry.getKey().size(); i++) {
                    IntValueSet all = entry.getKey();
                    List<IntValue[]> combinations = Combinations.generate(entry.getKey().toArray(new IntValue[0]), i);
                for (IntValue[] iv :
                        combinations) {
                    IntValueSet allCopy = (IntValueSet) all.clone();
                    allCopy.removeAll(Arrays.asList(iv));
                    AssociationRule rule = new AssociationRule(new IntValueSet(iv), allCopy);
                    rule.confidence = (double) entry.getValue() / frequentPatterns.get(rule.leftside);
                    if(rule.confidence < minConf){
                        toRemoveAR.add(rule);
                    }
                    allAssociationRules.add(rule);
                }

            }
        }
        associationRulesSatisf = (HashSet<AssociationRule>) allAssociationRules.clone();
        associationRulesSatisf.removeAll(toRemoveAR);
    }

}

