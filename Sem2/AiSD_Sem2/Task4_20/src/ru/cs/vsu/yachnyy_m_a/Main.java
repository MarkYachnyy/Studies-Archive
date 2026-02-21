package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import java.util.Random;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        SwingUtils.setDefaultFont("Microsoft Sans Serif", 20);
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FormMain().setVisible(true);
            }
        });
    }
}
