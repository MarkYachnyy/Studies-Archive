package ru.cs.vsu.yachnyy_m_a;

import java.util.Locale;
import java.util.Scanner;

public class Task6 {
    public static void main(String[] args){
        Locale.setDefault(Locale.ROOT);

        Scanner scanner = new Scanner(System.in);
        double x = scanner.nextDouble();
        int n = scanner.nextInt();
        double e = scanner.nextDouble();
        Result sums = result(x, n, e);
        System.out.printf("Сумма первых n слагаемых: %s\nСумма слагаемых, модуль которых больше е: %s\nСумма слагаемых, по модулю больших e/10: %s\nЗначение, полученное с помощью методов класса Math: %s",sums.s1, sums.s2, sums.s3, sums.s4);

    }
    public static Result result(double x, int n, double e){
        double s1 = 0;
        double s2 = 0;
        double s3 = 0;
        double s4 = Math.log(x + Math.sqrt(Math.pow(x, 2) + 1));
        int i = 2;
        double ai = x;
        while(Math.abs(ai) > e/10 || i <= n){

            if(i <= n){
                s1 += ai;
            }
            if(Math.abs(ai) > e){
                s2 += ai;
            }
            if(Math.abs(ai) > e/10){
                s3 += ai;
            }

            ai = - ai * (2 * i - 3) * x * x / (2 * i - 2)  / (2 * i - 1) * (2 * i - 3);

            i++;
        }
        return new Result(s1, s2, s3, s4);
    }
}
