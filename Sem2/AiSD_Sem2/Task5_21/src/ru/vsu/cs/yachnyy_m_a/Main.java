package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.awt.*;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        Locale.setDefault(Locale.ROOT);
        SwingUtils.setDefaultFont("Arial", 20);
        EventQueue.invokeLater(() -> {
            FormMain formMain = new FormMain();
            formMain.setVisible(true);
            formMain.setExtendedState(Frame.MAXIMIZED_BOTH);
        });
    }
}