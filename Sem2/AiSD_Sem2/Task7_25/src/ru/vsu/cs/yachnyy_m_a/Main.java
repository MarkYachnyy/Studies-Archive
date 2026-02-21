package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static java.awt.Frame.MAXIMIZED_BOTH;

public class Main {

    public static void main(String[] args) {
        SwingUtils.setDefaultFont("Microsoft sans serif", 18);
        Locale.setDefault(Locale.ROOT);
        java.awt.EventQueue.invokeLater(()->{
            FormMain frame = new FormMain();
            frame.setVisible(true);
            frame.setExtendedState(MAXIMIZED_BOTH);
        });
        //(0,1), (1,2), (2,3), (3, 0)

    }
}
