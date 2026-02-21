package ru.cs.vsu.yachnyy_m_a.util;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

public class ListUtils {

    public static List<Integer> readIntegerListFromFile(String fileName) {
        int[] array = ArrayUtils.readIntArrayFromFile(fileName);
        if(array==null){
            return null;
        } else {
            return Arrays.asList(intArrToInteger(array));
        }
    }

    public static List<Integer> readIntegerListFromConsole() {
        int[] array = ArrayUtils.readIntArrayFromConsole();
        return Arrays.asList(intArrToInteger(array));
    }

    private static Integer[] intArrToInteger(int[] arr) {
        Integer[] res = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    public static List<Integer> readIntegerListFromJTable(JTable table) {
        int[] arr = new int[]{};
        try {
            arr = JTableUtils.readIntArrayFromJTable(table);
        } catch (Exception e) {
            SwingUtils.showErrorMessageBox(e);
        }
        return Arrays.asList(intArrToInteger(arr));
    }

    public static int[] toIntArray(List<Integer> list) {
        int[] result = new int[list.size()];
        int i = -1;
        for (Integer a : list) {
            i++;
            result[i] = a;
        }
        return result;
    }

    public static String toString(List<Integer> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int i = -1;
        for (Integer a : list) {
            i++;
            stringBuilder.append(a);
            if (i < list.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
