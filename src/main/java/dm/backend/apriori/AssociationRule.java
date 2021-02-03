package dm.backend.apriori;

public class AssociationRule implements Comparable<AssociationRule>{
    double confidence;
    IntValueSet leftside, rightside;

    public AssociationRule(IntValueSet leftside, IntValueSet rightside) {
        this.confidence = 0;
        this.leftside = leftside;
        this.rightside = rightside;
    }

    @Override
    public String toString() {
        return leftside + " --> " + rightside + "  " + confidence;
    }

    @Override
    public int compareTo(AssociationRule o) {
        return leftside.size() + rightside.size() - o.leftside.size() - o.rightside.size();
    }
}
