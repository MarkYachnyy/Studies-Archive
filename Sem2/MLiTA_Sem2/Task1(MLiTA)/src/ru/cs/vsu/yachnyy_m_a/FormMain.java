package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.ArrayUtils;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.text.ParseException;

public class FormMain extends JFrame{
    private JPanel PanelMain;
    private JTable TableMatrix;
    private JProgressBar ProgressBar;
    private JButton ButtonLoadMatrixFromFile;
    private JButton ButtonCountDeterminant;
    private JLabel LabelTimePassed;
    private JLabel LabelPercentage;
    private JLabel LabelResult;
    private JButton ButtonRandMatrix;

    private JFileChooser InputFileChooser;

    private int timePassed = 0;

    private Timer timer = new Timer(1000, e -> {
        timePassed++;
        int min = timePassed/60;
        int sec = timePassed%60;
        LabelTimePassed.setText((min > 0 ? (min+"м ") : "") + sec + "с");
    });

    private float percentage = 0;

    private Timer TimerProgressRefresh = new Timer(100, e -> {
        LabelPercentage.setText(String.format("%.2f",percentage * 100) + "%");
        ProgressBar.setValue((int) (percentage * 100));
    });

    public FormMain(){
        this.setTitle("Определитель матрицы");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.pack();

        JTableUtils.initJTableForArray(TableMatrix, 50, false, false, true, true);
        TableMatrix.setRowHeight(50);

        InputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text files", "txt"));

        ProgressBar.setStringPainted(false);
        ProgressBar.setValue(0);

        ButtonLoadMatrixFromFile.addActionListener(e -> {
            if(InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                JTableUtils.writeArrayToJTable(TableMatrix, ArrayUtils.readIntArray2FromFile(InputFileChooser.getSelectedFile().getPath()));
                ProgressBar.setValue(0);
                LabelPercentage.setText("0%");
                LabelTimePassed.setText("0c");
                LabelTimePassed.setForeground(Color.BLACK);
                LabelResult.setText("|A| = ?");
            }
        });

        ButtonCountDeterminant.addActionListener(e -> {
            LabelTimePassed.setForeground(Color.BLACK);
            LabelResult.setText("|A| = ?");
            Thread thread = new Thread(() -> {
                try {
                    long res = Matrix.determinant(JTableUtils.readIntMatrixFromJTable(TableMatrix), p ->{
                        this.percentage = p;
                    });
                    LabelResult.setText("|A| = " + res);
                    LabelTimePassed.setForeground(Color.GREEN);
                    timer.stop();
                    TimerProgressRefresh.stop();
                    LabelPercentage.setText("100%");
                    ProgressBar.setValue(100);
                } catch (ParseException ex) {
                    ex.printStackTrace();
                }
            });
            timePassed = 0;
            timer.start();
            percentage = 0;
            TimerProgressRefresh.start();
            thread.start();
        });

        ButtonRandMatrix.addActionListener(e -> {
            int[][] randMatrix = ArrayUtils.createRandomIntMatrix(TableMatrix.getRowCount(), TableMatrix.getColumnCount(), -10, 10);
        });
        this.pack();
    }
}
