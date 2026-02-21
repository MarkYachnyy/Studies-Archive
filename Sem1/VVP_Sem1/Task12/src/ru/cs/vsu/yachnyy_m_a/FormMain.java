package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.ArrayUtils;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.function.Consumer;

public class FormMain extends JFrame{
    private JPanel PanelMain;
    private JTable TableInput;
    private JButton ButtonLoadInputFromFile;
    private JButton ButtonProcess;
    private JTextArea TextAreaOutput;
    private JButton ButtonWriteInputToFile;
    private JTextField TextFieldInputK;

    private JFileChooser InputFileChooser;
    private JFileChooser OutputFileChooser;

    private JMenuBar menuBarMain;
    private JMenu menuLookAndFeel;

    public FormMain(){
        setTitle("Task12");
        setContentPane(PanelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();

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

        JTableUtils.initJTableForArray(TableInput, 40, false, true, false, true);
        TableInput.setRowHeight(40);

        menuBarMain = new JMenuBar();
        setJMenuBar(menuBarMain);
        menuLookAndFeel = new JMenu();
        menuLookAndFeel.setText("Вид");
        menuBarMain.add(menuLookAndFeel);
        SwingUtils.initLookAndFeelMenu(menuLookAndFeel);

        ButtonLoadInputFromFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                    int[] arr = ArrayUtils.readIntArrayFromFile(InputFileChooser.getSelectedFile().getPath());
                    JTableUtils.writeArrayToJTable(TableInput, arr);
                }
            }
        });

        ButtonProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StringBuilder builder = new StringBuilder();
                try{
                    int k = Integer.parseInt(TextFieldInputK.getText());
                    Task12.genAll(JTableUtils.readIntArrayFromJTable(TableInput), k, new Consumer<int[]>() {
                        @Override
                        public void accept(int[] ints) {
                            builder.append(ArrayUtils.toString(ints));
                            builder.append('\n');
                        }
                    });

                } catch (Exception exception){
                    SwingUtils.showErrorMessageBox(exception);
                }
                TextAreaOutput.setText(builder.toString());
            }
        });

        ButtonWriteInputToFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OutputFileChooser.showSaveDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                    try{
                        PrintStream printStream = new PrintStream(OutputFileChooser.getSelectedFile());
                        printStream.println(TextAreaOutput.getText());
                        printStream.close();
                    } catch (Exception exception){
                        SwingUtils.showErrorMessageBox(exception);
                    }
                }
            }
        });
        pack();
    }

}
