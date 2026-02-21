package ru.vsu.cs.yachnyy_m_a;

import java.util.*;

public class EigenValues {
    private static double[][][] LU(double[][] matrix) {
        int N = matrix.length;
        double[][] L = new double[N][N], U = new double[N][N];

        for (int i = 0; i < N; i++) {
            L[i][i] = 1;
            for (int j = 0; j < N; j++) {
                if (i <= j) {
                    U[i][j] += matrix[i][j];
                    for (int k = 0; k <= i - 1; k++) {
                        U[i][j] -= L[i][k] * U[k][j];
                    }
                } else {
                    L[i][j] += matrix[i][j];
                    for (int k = 0; k <= j - 1; k++) {
                        L[i][j] -= L[i][k] * U[k][j];
                    }
                    if (U[j][j] != 0) {
                        L[i][j] /= U[j][j];
                    } else {
                        return null;
                    }
                }
            }
        }

        return new double[][][]{L, U};
    }

    public static double[] values(double[][] matrix) {
        double[][] A = matrix;
        for (int i = 0; i < 1000; i++) {
            double[][][] LU = LU(A);
            if (LU != null) {
                A = Matrix.multiply(LU[1], LU[0]);
            } else {
                return null;
            }
        }
        double[] res = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            res[i] = Double.parseDouble(String.format("%.2f", A[i][i]));
        }
        return res;
    }

    public static List<String[]> vectors(double[][] matrix){
        double[] values = values(matrix);
        return values == null ? null : vectors(matrix, values);
    }

    private static List<String[]> vectors(double[][] matrix, double[] values) {
        Set<Double> completed_values = new HashSet<>();
        ArrayList<String[]> res = new ArrayList<>();
        int N = matrix.length;
        for (double val : values) {
            if(completed_values.contains(val)) continue;
            double[][] eq_sys = new double[N][N + 1];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N + 1; j++) {
                    eq_sys[i][j] = j < N ? matrix[i][j] : 0;
                }
                eq_sys[i][i] -= val;
            }
            String[][] vectors = GaussMethod.findVector(eq_sys);
            res.addAll(Arrays.asList(vectors));
            completed_values.add(val);
        }
        return res;
    }
}
