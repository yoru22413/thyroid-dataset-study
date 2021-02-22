package dm.backend;

import dm.backend.apriori.IntValue;

import java.util.ArrayList;
import java.util.List;

public class Combinations {
    public static List<IntValue[]> generate(IntValue[] data, int r) {
        List<IntValue[]> combinations = new ArrayList<>();
        rec(combinations, new IntValue[r], 0, data.length-1, 0, data);
        return combinations;
    }

    private static void rec(List<IntValue[]> combinations, IntValue[] data, int start, int end, int index, IntValue[] dataAll) {
        if (index == data.length) {
            IntValue[] combination = data.clone();
            combinations.add(combination);
        } else if (start <= end) {
            data[index] = dataAll[start];
            rec(combinations, data, start + 1, end, index + 1, dataAll);
            rec(combinations, data, start + 1, end, index, dataAll);
        }
    }
}
