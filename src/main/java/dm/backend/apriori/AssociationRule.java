package dm.backend.apriori;

public class AssociationRule implements Comparable<AssociationRule>{
    public double confidence;
    public IntValueSet leftside, rightside;

    public AssociationRule(IntValueSet leftside, IntValueSet rightside) {
        this.confidence = 0;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    @Override
    public String toString() {
        String l = leftside.toString();
        l = l.substring(1, l.length()-1);
        String r = rightside.toString();
        r = r.substring(1, r.length()-1);
        return l + " --> " + r;
    }

    @Override
    public int compareTo(AssociationRule o) {
        //return leftside.size() + rightside.size() - o.leftside.size() - o.rightside.size();
        if(leftside.size() == o.leftside.size() && rightside.size() == o.rightside.size()){
            return 0;
        }
        if (leftside.size() > o.leftside.size()){
            return 1;
        }
        if(leftside.size() == o.leftside.size() && rightside.size() > o.rightside.size()){
            return 1;
        }
        return -1;
    }
}
