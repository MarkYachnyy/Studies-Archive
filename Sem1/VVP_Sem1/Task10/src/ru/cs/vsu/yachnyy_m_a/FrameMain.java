package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.ArrayUtils;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.TreeMap;

public class FrameMain extends JFrame {

    private JMenuBar menuBarMain;
    private JMenu menuLookAndFeel;

    private JTable TableFieldNames;
    private JTable TableInput;
    private JButton ButtonLoadInputFromFile;
    private JButton ButtonProcess;
    private JButton ButtonFillWithRandomStudents;
    private JButton ButtonSaveOutputIntoFile;
    private JPanel PanelMain;
    private JScrollPane ScrollPaneInput;
    private JTextArea TextAreaOutput;

    private JFileChooser InputFileChooser;
    private JFileChooser OutputFileChooser;

    public FrameMain() {
        this.setTitle("Task10");
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        TableFieldNames.setRowHeight(25);
        TableInput.setRowHeight(25);

        JTableUtils.initJTableForArray(TableFieldNames, 200, false, false, false, false);
        JTableUtils.initJTableForArray(TableInput, 200, true, false, true, false);
        JTableUtils.writeArrayToJTable(TableFieldNames, new String[]{"Имя", "Пол", "Курс", "Средний балл"});

        InputFileChooser = new JFileChooser();
        OutputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        OutputFileChooser.setCurrentDirectory(new File("."));
        FileFilter filter = new FileNameExtensionFilter("Text files", "txt");
        InputFileChooser.addChoosableFileFilter(filter);
        OutputFileChooser.addChoosableFileFilter(filter);

        OutputFileChooser.setAcceptAllFileFilterUsed(false);
        OutputFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        OutputFileChooser.setApproveButtonText("Save");

        menuBarMain = new JMenuBar();
        setJMenuBar(menuBarMain);
        menuLookAndFeel = new JMenu();
        menuLookAndFeel.setText("Вид");
        menuBarMain.add(menuLookAndFeel);
        SwingUtils.initLookAndFeelMenu(menuLookAndFeel);


        ButtonLoadInputFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                        Student[] arr = Student.readStudentArrayFromFile(InputFileChooser.getSelectedFile().getPath());
                        Student.writeStudentArrayToJTable(TableInput, arr);
                    }
                } catch (Exception exception) {
                    SwingUtils.showErrorMessageBox(exception);
                }
            }
        });

        ButtonFillWithRandomStudents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int length = TableInput.getRowCount();
                Student[] array = Student.randomStudentArray(length);
                Student.writeStudentArrayToJTable(TableInput, array);
            }
        });

        ButtonProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Student[] array = Student.readStudentArrayFromJTable(TableInput);
                String output = Task10.processToString(Arrays.asList(array));
                TextAreaOutput.setText(output);
            }
        });

        this.pack();
    }
}
