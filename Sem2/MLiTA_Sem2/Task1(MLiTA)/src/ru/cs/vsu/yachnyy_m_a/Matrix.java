package ru.cs.vsu.yachnyy_m_a;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Matrix {

    public static int[][] subMatrix(int[][] matrix, int I, int J) {
        int[][] res = new int[matrix.length - 1][matrix.length - 1];
        int i1 = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (i == I - 1) continue;
            int j1 = 0;
            for (int j = 0; j < matrix.length; j++) {
                if (j == J - 1) continue;
                res[i1][j1] = matrix[i][j];
                j1++;
            }
            i1++;
        }
        return res;
    }

    public static long determinant(int[][] matrix) {
        return determinant(matrix, null);
    }

    public static long determinant(int[][] matrix, Consumer<Float> consumer) {
        all = 1;
        for (int i = 3; i <= matrix.length ; i++) {
            all *= i;
        }
        completed = 0;
        return determinantInner(matrix, consumer);
    }

    private static long all = 1;
    private static long completed = 0;

    private static long determinantInner(int[][] matrix, Consumer<Float> consumer) {
        if (matrix.length == 2) {
            completed++;
            if (consumer != null) consumer.accept((float) (1d * completed / all));
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        long res = 0;
        for (int j = 1; j <= matrix.length; j++) {
            res += matrix[0][j - 1] * (1 - 2 * ((j + 1) % 2)) * determinantInner(subMatrix(matrix, 1, j), consumer);
        }
        return res;
    }
}
