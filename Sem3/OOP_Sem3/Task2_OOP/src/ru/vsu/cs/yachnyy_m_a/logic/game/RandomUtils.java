package ru.vsu.cs.yachnyy_m_a.logic.game;

import java.util.Collection;
import java.util.Random;

public class RandomUtils {
    public static <T> T randomElement(Collection<T> input) {
        int resI = new Random().nextInt(input.size());
        int i = 0;
        for (T elem : input) {
            if (i == resI) return elem;
            i++;
        }
        return null;
    }
}
