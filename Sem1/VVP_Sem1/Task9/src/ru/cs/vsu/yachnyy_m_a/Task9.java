package ru.cs.vsu.yachnyy_m_a;

import javax.print.attribute.IntegerSyntax;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Task9 {
    public static void process(List<Integer> list) {
        int Pos1 = 0;
        int A1 = 0;
        int Pos2 = 0;
        int A2 = 0;
        int cmin = list.size();
        int i1 = -1;
        for (Integer a1 : list) {
            i1++;
            int i2 = -1;
            for (Integer a2 : list) {
                i2++;
                if (i2 > i1) {
                    if ((a2 - a1) % (i2 - i1) == 0) {
                        int c = 0;
                        int d = (a2 - a1) / (i2 - i1);
                        int j = -1;
                        for (int a3 : list) {
                            j++;
                            int aj = a1 + d * (j - i1);
                            if (a3 != aj) {
                                c++;
                            }
                        }
                        if (c < cmin) {
                            Pos1 = i1;
                            Pos2 = i2;
                            A1 = a1;
                            A2 = a2;
                            cmin = c;
                        }
                    }
                }
            }
        }
        int d = (A2 - A1) / (Pos2 - Pos1);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, A1 + d * (i - Pos1));
        }
    }
}