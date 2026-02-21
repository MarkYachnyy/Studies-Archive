package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.ArrayUtils;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        this.setTitle("Task2_2");
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

        ButtonSort.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    int[] arr = JTableUtils.readIntArrayFromJTable(TableInput);
                    MyLinkedList<Integer> list = new MyLinkedList<>(IntToIntegerArray(arr));
                    list.bubbleSort(Integer::compareTo);
                    int[] output = new int[list.size()];
                    int i = 0;
                    for(Integer integer: list){
                        output[i] = integer;
                        i++;
                    }
                    JTableUtils.writeArrayToJTable(TableOutput, output);
                } catch (Exception exception){
                    SwingUtils.showErrorMessageBox(exception);
                }
            }
        });

        ButtonRandList.addActionListener(e -> {
            int[] randArr = ArrayUtils.createRandomIntArray(TableInput.getColumnCount(), 50);
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
