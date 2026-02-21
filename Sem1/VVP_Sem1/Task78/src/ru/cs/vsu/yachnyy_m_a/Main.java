package ru.cs.vsu.yachnyy_m_a;

import java.awt.*;
import java.util.Locale;

public class Main {
    public static void main(String[] z){
        Locale.setDefault(Locale.ROOT);

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FrameMain().setVisible(true);
            }
        });
    }

}
