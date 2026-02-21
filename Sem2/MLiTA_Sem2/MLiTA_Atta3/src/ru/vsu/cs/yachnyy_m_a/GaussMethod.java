package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class GaussMethod {

    public static String[] solve(double[][] input) {
        int N = input.length;
        double[][] matrix = copy(input);
        toStairsForm(matrix);
        double[][] coefs = coefsMatrix(matrix);
        return  coefs == null ? null : toAnswers(coefs, true);
    }

    private static double[][] coefsMatrix(double[][] stairs_matrix){
        int N = stairs_matrix.length;
        double[][] coefs = new double[N][N + 1];
        for (int i = N - 1; i >= 0; i--) {
            coefs[i][N] = stairs_matrix[i][N];
            for (int j = i + 1; j < N; j++) {
                double mul = stairs_matrix[i][j];
                for (int j1 = 0; j1 <= N; j1++) {
                    coefs[i][j1] -= coefs[j][j1] * mul;
                }
            }
            round(coefs);
            if (stairs_matrix[i][i] == 0) {
                for (int j = i + 1; j <= N; j++) {
                    if (coefs[i][j] != 0) {
                        return null;
                    }
                }
                coefs[i][i] = 1;
            } else {
                double div = stairs_matrix[i][i];
                for (int j = i + 1; j <= N; j++) {
                    coefs[i][j] /= div;
                }
            }
            round(coefs);
        }
        return coefs;
    }

    public static String[][] findVector(double[][] input){
        int N = input.length;
        ArrayList<Integer> indexes = new ArrayList<>();
        toStairsForm(input);
        double[][] coefs = coefsMatrix(input);
        if (coefs == null) return null;
        for (int i = 0; i < N; i++) {
            if(coefs[i][i] == 1){
                indexes.add(i);
            }
        }
        String[][] res = new String[indexes.size()][];
        for (int i = 0; i < indexes.size(); i++){
            double[][] coefs_i = copy(coefs);
            for (int i1 = 0; i1 < indexes.size(); i1++) {
                coefs_i[indexes.get(i1)][indexes.get(i1)] = 0;
            }
            coefs_i[indexes.get(i)][N] = 1;
            for (int i1 = N-1; i1 >= 0; i1--) {
                for (int j1 = 0; j1 < N; j1++) {
                    coefs_i[i1][N] += coefs_i[i1][j1]*coefs_i[j1][N];
                    coefs_i[i1][j1] = 0;
                }
            }
            res[i] = toAnswers(coefs_i, false);
        }
        return res;
    }

    private static String[] toAnswers(double[][] coefs, boolean showVariableNames) {
        int N = coefs.length;
        String[] res = new String[N];
        for (int i = 0; i < N; i++) {
            res[i] = "";
            if (coefs[i][i] == 1) {
                res[i] = "x" + (i + 1) + " ∈ R";
            } else {
                boolean z = true;
                for (int j = i + 1; j < N; j++) {
                    if (coefs[i][j] != 0) {
                        z = false;
                        char sign = coefs[i][j] < 0 ? '-' : '+';
                        String abs = Math.abs(coefs[i][j]) == 1 ? "" : (coefs[i][j] % 1 == 0 ? (int) Math.abs(coefs[i][j]) + "" : String.valueOf(Math.abs(coefs[i][j])));
                        res[i] += sign + abs + "*x" + (j + 1);
                    }
                }
                if (coefs[i][N] == 0) {
                    if (z) {
                        res[i] += '0';
                    } else {
                        if(res[i].charAt(0) == '+') res[i] = res[i].substring(1);
                    }
                } else {
                    res[i] = (coefs[i][N] % 1 == 0 ? (int)coefs[i][N]+"":coefs[i][N]) + res[i];
                }
                if(showVariableNames)res[i] = "x" + (i + 1) + " = " + res[i];
            }
        }
        return res;
    }

    private static void toStairsForm(double[][] matrix) {
        for (int j = 0; j < matrix.length - 1; j++) {
            boolean flag = false;
            for (int i = j; i < matrix.length; i++) {
                if (matrix[i][j] != 0) {
                    swapRows(matrix, i, j);
                    flag = true;
                    break;
                }
            }
            if (flag) {
                for (int i = j + 1; i < matrix.length; i++) {
                    if (matrix[i][j] != 0) {
                        double div = matrix[i][j];
                        for (int k = j; k < matrix[0].length; k++) {
                            matrix[i][k] /= div;
                            matrix[i][k] *= matrix[j][j];
                            matrix[i][k] -= matrix[j][k];
                        }
                    }
                }
                round(matrix);
            }
        }
    }

    private static void swapRows(double[][] matrix, int a, int b) {
        for (int j = 0; j < matrix[0].length; j++) {
            double tmp = matrix[a][j];
            matrix[a][j] = matrix[b][j];
            matrix[b][j] = tmp;
        }
    }

    private static void round(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                double r = Math.abs(matrix[i][j] % 0.1);
                if (Math.min(r, 0.1 - r) < 1e-6) {
                    matrix[i][j] = Float.parseFloat(String.format("%.2f", matrix[i][j]));
                }
            }
        }
    }

    private static double[][] copy(double[][] original){
        double[][] res = new double[original.length][original[0].length];
        for (int i = 0; i < res.length; i++) {
            System.arraycopy(original[i], 0, res[i], 0, res[0].length);
        }
        return res;
    }
}
