package dm.backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Permutations {
    public static List<int[]> generate(int[] data) {
        List<int[]> combinations = new ArrayList<>();
        combinations.add(Arrays.copyOf(data, data.length));
        int[] indexes = new int[data.length];
        int[] copy;
        int i = 0;
        while (i < data.length) {
            if (indexes[i] < i) {
                copy = Arrays.copyOf(data, data.length);
                swap(copy, i % 2 == 0 ?  0: indexes[i], i);
                combinations.add(copy);
                indexes[i]++;
                i = 0;
            }
            else {
                indexes[i] = 0;
                i++;
            }
        }
        return combinations;
    }

    private static void swap(int[] input, int a, int b) {
        int tmp = input[a];
        input[a] = input[b];
        input[b] = tmp;
    }
}
