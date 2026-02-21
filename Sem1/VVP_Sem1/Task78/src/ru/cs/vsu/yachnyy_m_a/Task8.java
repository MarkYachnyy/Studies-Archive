package ru.cs.vsu.yachnyy_m_a;

public class Task8 {

    public static void main(String[] args) {
        int[][] matrix = ArrayUtils.readIntArray2FromConsole();
        Answer8 answer8 = solve(matrix);
        System.out.printf("(%s, %s, %s, %s)", answer8.i, answer8.j, answer8.h, answer8.w);
    }

/*
0 1 1 1 0 0 0 0 0
0 1 1 1 0 0 1 1 0
0 1 1 1 0 0 1 1 0
0 0 0 0 1 0 1 1 0
1 1 1 0 1 0 0 0 0
1 1 1 0 0 0 0 1 0
0 0 0 0 1 1 1 1 1
1 1 1 0 0 0 0 1 0
*/

    public static Answer8 solve(int[][] matrix0) {
        int[][] matrix = surroundBy0(matrix0);
        int[][] sums = sumMatrix(matrix);
        int n = matrix.length;
        int m = matrix[0].length;
        int max = 0;
        Answer8 answer8 = new Answer8(-1, -1, -1, -1);
        for (int h = 1; h <= n - 2; h++) {
            for (int w = 1; w <= m - 2; w++) {
                for (int i = 1; i < n - h; i++) {
                    for (int j = 1; j < m - w; j++) {
                        int sumInner = RectSum(sums, i, j, h, w);
                        int sumOuter = RectSum(sums, i - 1, j - 1, h + 2, w + 2);
                        if ((sumInner == w * h) && (sumInner == sumOuter)) {
                            if (w * h > max) {
                                max = w * h;
                                answer8 = new Answer8(i - 1, j - 1, w, h);
                            } else if (w * h == max) {
                                if (i < answer8.i) {
                                    answer8 = new Answer8(i - 1, j - 1, w, h);
                                } else if (i == answer8.i) {
                                    answer8 = new Answer8(i - 1, j - 1, w, h);
                                }
                            }
                        }
                    }
                }
            }
        }
        return answer8;
    }

    public static int RectSum(int[][] sums, int i, int j, int h, int w) {
        int sum = 0;
        sum += sums[i + h - 1][j + w - 1];
        if (i > 0) {
            sum -= sums[i - 1][j + w - 1];
        }
        if (j > 0) {
            sum -= sums[i + h - 1][j - 1];
        }
        if (i > 0 && j > 0) {
            sum += sums[i - 1][j - 1];
        }
        return sum;
    }

    public static int[][] BoolToIntMatrix(boolean[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] res = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (matrix[i][j]) {
                    res[i][j] = 1;
                } else {
                    res[i][j] = 0;
                }
            }
        }
        return res;
    }

    public static int[][] surroundBy0(int[][] matrix0) {
        int n = matrix0.length + 2;
        int m = matrix0[0].length + 2;
        int[][] matrix = new int[matrix0.length + 2][matrix0[0].length + 2];
        for (int j = 0; j < m; j++) {
            matrix[0][j] = 0;
            matrix[n - 1][j] = 0;
        }
        for (int i = 1; i < n - 1; i++) {
            matrix[i][0] = 0;
            matrix[i][m - 1] = 0;
            for (int j = 1; j < m - 1; j++) {
                matrix[i][j] = matrix0[i - 1][j - 1];
            }
        }
        return matrix;
    }

    public static int[][] sumMatrix(int[][] matrix) {
        int n = matrix.length;
        int m = matrix[0].length;
        int[][] res = new int[n][m];
        res[0][0] = matrix[0][0];
        for (int i = 1; i < n; i++) {
            res[i][0] = res[i - 1][0] + matrix[i][0];
        }
        for (int j = 1; j < m; j++) {
            res[0][j] = res[0][j - 1] + matrix[0][j];
        }
        for (int j = 1; j < m; j++) {
            for (int i = 1; i < n; i++) {
                res[i][j] = matrix[i][j] + res[i - 1][j] + res[i][j - 1] - res[i - 1][j - 1];
            }
        }
        return res;
    }
}