package ru.cs.vsu.yachnyy_m_a;


import org.apache.commons.cli.*;
import ru.cs.vsu.yachnyy_m_a.util.ListUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Main {

    public static final String PROGRAM_NAME_IN_HELP = "program (-h | -w  -i <in-file> [-o <out-file>])";

    public static void main(String[] args) {
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption("h", "help", false, "Show help");
        cmdLineOptions.addOption("w", "window", false, "Use window user interface");
        cmdLineOptions.addOption("i", "input-file", true, "Input file");
        cmdLineOptions.addOption("o", "output-file", true, "Output file");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmdLine = null;
        try {
            cmdLine = parser.parse(cmdLineOptions, args);
        } catch (Exception e) {
            new HelpFormatter().printHelp(PROGRAM_NAME_IN_HELP, cmdLineOptions);
            System.exit(1);
        }

        if (cmdLine.hasOption("h")) {
            new HelpFormatter().printHelp(PROGRAM_NAME_IN_HELP, cmdLineOptions);
            System.exit(1);
        }
        if (cmdLine.hasOption("w")) {
            winMain();
        } else {
            try{
                if (!cmdLine.hasOption("i")) {
                    new HelpFormatter().printHelp(PROGRAM_NAME_IN_HELP, cmdLineOptions);
                    System.exit(1);
                }
                String inputFilename = cmdLine.getOptionValue("i");
                List<Student> list = Arrays.asList(Student.readStudentArrayFromFile(inputFilename));
                if (list == null) {
                    System.err.printf("Can't read list from \"%s\"%n", inputFilename);
                    System.exit(2);
                }

                String output = Task10.processToString(list);

                PrintStream out = (cmdLine.hasOption("o")) ? new PrintStream(cmdLine.getOptionValue("o")) : System.out;
                out.println(output);
                out.close();
            } catch (Exception e){
                SwingUtils.showErrorMessageBox(e);
            }

        }
    }

    private static void winMain() {
        Locale.setDefault(Locale.ROOT);
        SwingUtils.setDefaultFont("Microsoft Sans Serif", 18);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameMain().setVisible(true);
            }
        });
    }
}
