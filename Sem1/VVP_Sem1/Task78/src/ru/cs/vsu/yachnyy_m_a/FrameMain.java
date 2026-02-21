package ru.cs.vsu.yachnyy_m_a;

import javax.swing.*;

public class FrameMain extends JFrame{

    private JTextField textField1;
    private JPanel PanelMain;

    public FrameMain(){
        this.setTitle("calc");
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();
    }
}
