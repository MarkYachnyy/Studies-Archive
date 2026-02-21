package ru.cs.vsu.yachnyy_m_a;

import java.util.Comparator;
import java.util.Random;

public class IntroSort {

    private static <T extends Comparable<T>> int partition(T[] array, int left, int right){
        int l = left;
        int r = right - 1;

        T x = med3(array, left, right);

        while (l <= r) {
            while (array[l].compareTo(x) < 0) {
                l++;
            }
            while (array[r].compareTo(x) > 0) {
                r--;
            }
            if (l <= r) {
                T tmp = array[r];
                array[r] = array[l];
                array[l] = tmp;

                if(array[r].compareTo(x) == 0){
                    l++;
                } else {
                    r--;
                    if(array[l].compareTo(x) != 0) l++;
                }
            }
        }

        if (l == right) {
            l--;
        }
        return l;
    }

    private static <T extends Comparable<T>> T med3(T[] array, int left, int right){
        T a = array[left];
        T b = array[(left + right) / 2];
        T c = array[right - 1];
        if(a.compareTo(b) > 0){
            T tmp = b;
            b = a;
            a = tmp;
        }
        if(b.compareTo(c) > 0){
            T tmp = c;
            c = b;
            b = tmp;
        }
        if(a.compareTo(b) > 0){
            T tmp = b;
            b = a;
            a = tmp;
        }
        return b;
    }

    private static <T extends Comparable<T>> void siftDown(T[] data, int left, int k, int n) {
        T value = data[k];
        while (true) {
            int childIndex = left + 2 * (k - left) + 1;

            if (childIndex >= left + n) {
                break;
            }

            if (childIndex + 1 < left + n && data[childIndex + 1].compareTo(data[childIndex]) > 0) {
                childIndex++;
            }

            if (value.compareTo(data[childIndex]) > 0) {
                break;
            }

            data[k] = data[childIndex];
            k = childIndex;
        }
        data[k] = value;
    }


    private static <T extends Comparable<T>> void heapSort(T[] data, int left, int right) {
        int heapSize = right - left;

        for (int i = left + heapSize / 2; i >= left; i--) {
            siftDown(data, left,  i, heapSize);
        }

        while (heapSize > 1) {

            T tmp = data[left + heapSize - 1];
            data[left + heapSize - 1] = data[left];
            data[left] = tmp;

            heapSize--;

            siftDown(data, left, left, heapSize);
        }
    }
    public static <T extends Comparable<T>> void sort(T[] array, double max_depth){
        sort(array, 0, array.length, max_depth);
    }
    public static <T extends Comparable<T>> void sort(T[] array){
        sort(array, 0, array.length, Math.log(array.length) / Math.log(2));
    }
    private static <T extends Comparable<T>> void sort(T[] array, int left, int right, double depth){
        if(right - left < 2) return;
        if(depth > 0){
            int x = partition(array, left, right);
            sort(array, left, x, depth - 1);
            sort(array, x, right, depth - 1);
        } else {
            heapSort(array, left, right);
        }
    }
}
