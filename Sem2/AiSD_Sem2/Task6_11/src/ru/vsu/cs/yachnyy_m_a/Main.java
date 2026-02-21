package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        SwingUtils.setDefaultFont("Arial", 20);
        SwingUtils.setShowMessageDefaultErrorHandler();
        java.awt.EventQueue.invokeLater(()->{
            new FormMain().setVisible(true);
        });
    }
}
