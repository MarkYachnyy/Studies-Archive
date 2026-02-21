package ru.vsu.cs.yachnyy_m_a.task2_mlita;

import ru.vsu.cs.yachnyy_m_a.task2_mlita.util.SwingUtils;

import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        SwingUtils.setDefaultFont("Microsoft sans serif", 20);
        Locale.setDefault(Locale.ROOT);
        java.awt.EventQueue.invokeLater(() -> new FormMain().setVisible(true));
    }
}