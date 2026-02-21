package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        SwingUtils.setDefaultFont("Arial", 20);
        Locale.setDefault(Locale.ROOT);
        java.awt.EventQueue.invokeLater(()->{
            new FormMain().setVisible(true);
        });

    }
}
