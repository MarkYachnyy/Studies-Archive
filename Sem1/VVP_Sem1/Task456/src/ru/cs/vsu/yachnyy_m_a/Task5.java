package ru.cs.vsu.yachnyy_m_a;

import java.util.Scanner;

public class Task5 {
    public static void main(String[] args){
        System.out.print("Введите число:");
        int a = new Scanner(System.in).nextInt();
        System.out.println(draw(a));
    }
    public static String draw(int a){
        if(a % 2 == 1 || a < 2){
            return "Посторить рисунок с таким числом не получится";
        } else {
            StringBuilder res = new StringBuilder();
            for(int i = 0; i < a / 2; i++){
                StringBuilder s = new StringBuilder();
                for(int j = 0; j < a / 2 - i - 1; j++){
                    s.append(' ');
                }
                for(int j = 0; j <= i; j++){
                    s.append(j);
                }
                s.append('\n');
                res.append(s);
            }
            for(int i = a / 2 - 1; i >= 0; i--){
                StringBuilder s = new StringBuilder();
                for (int j = 0; j < a/2; j++) {
                    s.append(' ');
                }
                for (int j = i; j >= 0; j--) {
                    s.append(j);
                }
                s.append('\n');
                res.append(s);
            }
            return res.toString();
        }
    }
}
