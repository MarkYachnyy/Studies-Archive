package ru.cs.vsu.yachnyy_m_a;

import javafx.fxml.LoadException;

import java.util.Arrays;
import java.util.Locale;

public class Task4 {
    public static void main(String[] args){
        Locale.setDefault(Locale.ROOT);
        System.out.printf("Последовательность x[1] + x[2] + ... образована по правилу:\n " +
                "x[1] = 0; x[2] = 5/8; x[i] = x[i-1] / 2 + 3 / 4 * x[i-2]; i = 3, 4, ...\n" +
                "Сумма такой последовавтельности будет равна: %f\n", s());
    }
    public static double s(){
        double pprev = 0;
        double prev = 5.0 / 8;
        double S = pprev + prev;
        for(int i = 3; i <= 20;i++){
            double x = prev / 2 + 3 * pprev / 4;
            System.out.println(x);
            S += x;
            pprev = prev;
            prev = x;
        }
        return S;
    }
}
