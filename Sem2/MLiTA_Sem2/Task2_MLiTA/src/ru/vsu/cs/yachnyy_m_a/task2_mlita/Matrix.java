package ru.vsu.cs.yachnyy_m_a.task2_mlita;

import java.util.function.Consumer;

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

    public static int[][] removeColumn(int[][] matrix, int J){
        int[][] res = new int[matrix.length][matrix[0].length - 1];
        for (int i = 0; i < res.length; i++) {
            for(int j = 0; j < res[0].length; j++){
                res[i][j] = matrix[i][j < J - 1? j : j + 1];
            }
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
        } else if(matrix.length == 1){
            return matrix[0][0];
        }
        long res = 0;
        for (int j = 1; j <= matrix.length; j++) {
            res += matrix[0][j - 1] * (1 - 2 * ((j + 1) % 2)) * determinantInner(subMatrix(matrix, 1, j), consumer);
        }
        return res;
    }
}
