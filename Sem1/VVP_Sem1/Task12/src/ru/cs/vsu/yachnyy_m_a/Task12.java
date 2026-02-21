package ru.cs.vsu.yachnyy_m_a;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class Task12 {

    private static ArrayList<Integer> next_c = new ArrayList<>();

    public static void genAll(int[] elements, int k, Consumer<int[]> callback) {
        if (k == 1) {
            for (int i : elements) {
                next_c.add(i);
                callback.accept(integerListToIntArray(next_c));
                next_c.remove(next_c.size() - 1);
            }
        } else {
            for (int i = 0; i <= elements.length - k; i++) {
                next_c.add(elements[i]);
                genAll(slice(elements, i+1), k-1, callback);
                next_c.remove(next_c.size()-1);
            }
        }
    }

    private static int[] integerListToIntArray(List<Integer> list) {
        int[] res = new int[list.size()];
        int i = -1;
        for (Integer integer : list) {
            i++;
            res[i] = integer;
        }
        return res;
    }

    private static int[] slice(int[] source, int start) {
        int[] res = new int[source.length - start];
        if (source.length - start >= 0) System.arraycopy(source, start, res, 0, source.length - start);
        return res;
    }

    /*public void generate(int num, int last, Consumer<int[]> callback) {
        if (num == m) {
            for (int i = 0; i < m; i++)
                cout << a[i];
            cout << endl;
        }
        for (int i = last + 1; i <= n; i++) {
            a.push_back(i);
            generate(num + 1, i);
            a.pop_back();
        }


    }*/
}
