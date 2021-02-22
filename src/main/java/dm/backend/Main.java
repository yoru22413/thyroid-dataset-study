package dm.backend;

import dm.backend.apriori.*;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Table t = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"});
        t.removeColumn(0);
        for (int i = 0; i < t.width(); i++) {
            t.setColumn(i, t.column(i).discretization(5));
        }
        AprioriStruct aps = new AprioriStruct(t);
        Algorithm algo = new Algorithm(aps, 0.4);
        algo.execute();
        IntValueSet[] sets = algo.frequentPatterns.keySet().toArray(new IntValueSet[0]);
        for (IntValueSet set :
                sets) {
            System.out.println(set);
        }
        sets = new IntValueSet[]{
            new IntValueSet(new IntValue[]{
                    new IntValue(0, 1),
                    new IntValue(0, 2),
                    new IntValue(0, 3),
                    new IntValue(0, 4),
                    new IntValue(0, 5),
                    new IntValue(0, 6)
            }),
            new IntValueSet(new IntValue[]{

                    new IntValue(0, 2),
                    new IntValue(0, 3),
                    new IntValue(0, 4),
                    new IntValue(0, 5),
                    new IntValue(0, 6),
                    new IntValue(0, 7)
            }),
            new IntValueSet(new IntValue[]{
                    new IntValue(0, 1),
                    new IntValue(0, 4),
                    new IntValue(0, 5),
                    new IntValue(0, 8),
            }),
            new IntValueSet(new IntValue[]{
                    new IntValue(0, 1),
                    new IntValue(0, 4),
                    new IntValue(0, 6),
                    new IntValue(0, 9),
                    new IntValue(0, 10),
            }),
            new IntValueSet(new IntValue[]{
                    new IntValue(0, 2),
                    new IntValue(0, 4),
                    new IntValue(0, 5),
                    new IntValue(0, 10),
                    new IntValue(0, 11),
            })
        };
        System.out.println("=====================================");
        aps = new AprioriStruct(sets);
        algo = new Algorithm(aps, 0.6);
        algo.execute();
        sets = algo.frequentPatterns.keySet().toArray(new IntValueSet[0]);
        for (IntValueSet set :
                sets) {
            System.out.println(set);
        }
        algo.minConf = 0.8;
        algo.associationRules();
        for (AssociationRule rule :
                algo.associationRulesSatisf) {
            System.out.println(rule);
        }
        int[] a = new int[]{1,1,1,2,2,2,3,3,3}, b = new int[]{0,1,0,1,1,2,2,2,2};
        Metrics m = Utils.computeMetrics(a, b);
        System.out.println(m);
    }
}
