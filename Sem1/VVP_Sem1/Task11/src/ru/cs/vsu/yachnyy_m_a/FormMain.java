package ru.cs.vsu.yachnyy_m_a;

import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

public class FormMain extends JFrame{
    private JPanel PanelMain;
    private JButton ButtonLoadInputFromFile;
    private JButton ButtonProcess;
    private JTextArea TextAreaOutput;
    private JTextArea TextAreaInput;
    private JButton ButtonSaveOutputIntoFile;

    private JFileChooser InputFileChooser;
    private JFileChooser OutputFileChooser;

    private JMenuBar menuBarMain;
    private JMenu menuLookAndFeel;

    public FormMain(){
        this.setTitle("Task10");
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

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

        TextAreaInput.setLineWrap(true);
        TextAreaInput.setWrapStyleWord(true);
        TextAreaOutput.setLineWrap(true);
        TextAreaOutput.setWrapStyleWord(true);

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
                    try{
                        StringBuilder input = new StringBuilder();
                        Scanner scanner = new Scanner(new File(InputFileChooser.getSelectedFile().getPath()));
                        while (scanner.hasNext()){
                            input.append(scanner.nextLine());
                        }
                        TextAreaInput.setText(input.toString());
                    } catch (Exception exception){
                        SwingUtils.showErrorMessageBox(exception);
                    }
                }
            }
        });

        ButtonProcess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String output = Task11.processWithRegex(TextAreaInput.getText());
                TextAreaOutput.setText(output);
            }
        });

        ButtonSaveOutputIntoFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(OutputFileChooser.showSaveDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                    try{
                        PrintStream printStream = new PrintStream(OutputFileChooser.getSelectedFile().getPath());
                        printStream.println(TextAreaOutput.getText());
                    } catch (Exception exception){
                        SwingUtils.showErrorMessageBox(exception);
                    }
                }

            }
        });

        this.pack();
    }
}
