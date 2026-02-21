package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.ArrayUtils;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JTable TableInput;
    private JButton ButtonLoadListFromFile;
    private JButton ButtonRandList;
    private JButton ButtonSort;
    private JTable TableOutput;

    private JFileChooser InputFileChooser;

    private static final int CELL_SIZE = 50;

    public FormMain(){
        this.setTitle("Task4_20");
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTableUtils.initJTableForArray(TableInput, CELL_SIZE, false, true, false, true);
        JTableUtils.initJTableForArray(TableOutput, CELL_SIZE, false, true, false, false);
        TableInput.setRowHeight(CELL_SIZE);
        TableOutput.setRowHeight(CELL_SIZE);

        InputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", "txt"));

        ButtonLoadListFromFile.addActionListener(e -> {
            if(InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                int[] arr = ArrayUtils.readIntArrayFromFile(InputFileChooser.getSelectedFile().getPath());
                JTableUtils.writeArrayToJTable(TableInput, arr);
            }
        });

        ButtonSort.addActionListener(e -> {
            try{
                Integer[] arr = IntToIntegerArray(JTableUtils.readIntArrayFromJTable(TableInput));
                IntroSort.sort(arr);
                JTableUtils.writeArrayToJTable(TableOutput, IntegerToIntArr(arr));
            } catch (Exception exception){
                SwingUtils.showErrorMessageBox(exception);
            }
        });

        ButtonRandList.addActionListener(e -> {
            int[] randArr = ArrayUtils.createRandomIntArray(TableInput.getColumnCount(), 30);
            JTableUtils.writeArrayToJTable(TableInput, randArr);
        });

        this.pack();
    }

    private int[] IntegerToIntArr(Integer[] arr){
        int[] res = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }

    private Integer[] IntToIntegerArray(int[] arr){
        Integer[] res = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = arr[i];
        }
        return res;
    }
}

