package ru.vsu.cs.yachnyy_m_a.task2_mlita;

import ru.vsu.cs.yachnyy_m_a.task2_mlita.util.ArrayUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JButton ButtonLoadMatrixFromFile;
    private JButton ButtonSolveSystem;
    private JLabel LabelAnswer;
    private JTextArea TextAreaSystem;
    private JButton ButtonWriteMatrixManually;

    private JFileChooser InputFileChooser;

    private int[][] matrix;

    public FormMain() {
        this.setTitle("Решение СЛУ методом Крамера");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.pack();

        InputFileChooser = new JFileChooser();
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", "txt"));
        InputFileChooser.setCurrentDirectory(new File("."));

        TextAreaSystem.setEditable(false);

        ButtonLoadMatrixFromFile.addActionListener(event -> {
            if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                matrix = ArrayUtils.readIntArray2FromFile(InputFileChooser.getSelectedFile().getPath());
                SetTextInInputArea();
                LabelAnswer.setText("-");
            }
        });

        ButtonSolveSystem.addActionListener(event -> {
            double[] solution = new LinearEquationSystem(matrix).solve();
            if (solution == null) {
                LabelAnswer.setText("Система не имеет решений");
            } else if (solution[0] == Double.POSITIVE_INFINITY) {
                LabelAnswer.setText("Система имеет бесконечно много решений");
            } else {
                LabelAnswer.setText(IntStream.range(0, solution.length).
                        mapToObj(i -> "x" + (i + 1) + " = " + (solution[i] % 1 == 0 ? String.valueOf((int) solution[i]) : solution[i])).
                        collect(Collectors.joining("; ")));
            }
        });

        ButtonWriteMatrixManually.addActionListener(event -> {
            new ManualMatrixInputDialogue(m -> {
                this.matrix = m;
                this.SetTextInInputArea();
            }).setVisible(true);
        });

        this.pack();
    }

    private void SetTextInInputArea() {
        StringBuilder res = new StringBuilder();
        for (int[] row : matrix) {
            StringBuilder row_str = new StringBuilder();
            row_str.append(IntStream.range(0, row.length - 1).
                    filter(i -> row[i] != 0).
                    mapToObj(i -> (i > 0 ? " " + (row[i] < 0 ? '-' : '+') + " " : (row[i] < 0 ? "-" : "")) + (Math.abs(row[i]) == 1 ? "" : Math.abs(row[i]) + "*") + "x" + (i + 1)).
                    collect(Collectors.joining())).append(" = ").append(row[row.length - 1]).append('\n');
            res.append(row[0] == 0 ? row_str.substring(3) : row_str);
        }
        TextAreaSystem.setText(res.toString());
    }
}

