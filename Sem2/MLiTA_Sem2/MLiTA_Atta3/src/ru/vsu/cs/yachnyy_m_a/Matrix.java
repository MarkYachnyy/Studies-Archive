package ru.vsu.cs.yachnyy_m_a;

public class Matrix {
    public static double[][] multiply(double[][] A, double[][] B){
        double[][] result = new double[A.length][B[0].length];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                for (int k = 0; k < B.length; k++) {
                    result[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        return result;
    }
}
