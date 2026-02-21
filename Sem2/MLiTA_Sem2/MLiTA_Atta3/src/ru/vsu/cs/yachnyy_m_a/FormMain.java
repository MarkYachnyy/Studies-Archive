package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.ArrayUtils;
import ru.vsu.cs.yachnyy_m_a.util.JTableUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FormMain extends JFrame{
    private JPanel PanelMain;
    private JTable TableInput;
    private JButton ButtonLoadMatrixFromFile;
    private JButton ButtonSolveGauss;
    private JButton ButtonFindEigenValuesButton;
    private JButton ButtonFindEigenVectors;
    private JTextPane TextAreaOutput;

    private JFileChooser InputFileChooser;

    public FormMain(){
        this.setTitle("3 аттестация");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.pack();

        JTableUtils.initJTableForArray(TableInput, 50, false, false, true, true);
        TableInput.setRowHeight(50);

        InputFileChooser = new JFileChooser();
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", "txt"));
        InputFileChooser.setCurrentDirectory(new File("."));

        TextAreaOutput.setEditable(false);

        ButtonLoadMatrixFromFile.addActionListener(event -> {
            if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                int[][] matrix = ArrayUtils.readIntArray2FromFile(InputFileChooser.getSelectedFile().getPath());
                JTableUtils.writeArrayToJTable(TableInput, matrix);
                TextAreaOutput.setText("-");
            }
        });

        ButtonSolveGauss.addActionListener(event -> {
            try {
                double[][] matrix = JTableUtils.readDoubleMatrixFromJTable(TableInput);
                if(matrix[0].length - matrix.length != 1){
                    SwingUtils.showInfoMessageBox("Неподходящий размер матрицы");
                } else{
                    String[] answers = GaussMethod.solve(matrix);
                    TextAreaOutput.setText(answers == null ? "Система несовместна" : String.join("\n", answers));
                }
            } catch (Exception e){
                SwingUtils.showErrorMessageBox(e);
            }

        });

        ButtonFindEigenValuesButton.addActionListener(event -> {
            try {
                double[][] matrix = JTableUtils.readDoubleMatrixFromJTable(TableInput);
                if(matrix[0].length - matrix.length != 0){
                    SwingUtils.showInfoMessageBox("Неподходящий размер матрицы");
                } else{
                    double[] values = EigenValues.values(matrix);
                    String text;
                    if(values == null){
                        text = "Невозможно получить значения LU-методом";
                    } else {
                        text = IntStream.range(0, values.length).boxed().map(i -> "λ" + (i+1) + " = " + (values[i] % 1 == 0 ? ""+(int)values[i] : values[i])).collect(Collectors.joining("\n"));
                    }
                    TextAreaOutput.setText(text);
                }
            } catch (Exception e){
                SwingUtils.showErrorMessageBox(e);
            }
        });

        ButtonFindEigenVectors.addActionListener(event -> {
            try {
                double[][] matrix = JTableUtils.readDoubleMatrixFromJTable(TableInput);
                if(matrix[0].length - matrix.length != 0){
                    SwingUtils.showInfoMessageBox("Неподходящий размер матрицы");
                } else{
                    String text;
                    List<String[]> vectors = EigenValues.vectors(matrix);
                    if(vectors == null){
                        text = "Невозможно получить значения LU-методом";
                    } else {
                        text = IntStream.range(0, vectors.size()).boxed().map(i -> "v"+(i+1)+" = (" + Arrays.stream(vectors.get(i)).collect(Collectors.joining(", ")) + ")").collect(Collectors.joining("\n"));
                    }
                    TextAreaOutput.setText(text);
                }
            } catch (Exception e){
                SwingUtils.showErrorMessageBox(e);
            }
        });
    }
}
