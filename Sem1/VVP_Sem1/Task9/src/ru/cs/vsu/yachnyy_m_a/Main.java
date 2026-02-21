package ru.cs.vsu.yachnyy_m_a;

import org.apache.commons.cli.*;
import ru.cs.vsu.yachnyy_m_a.util.ListUtils;
import ru.cs.vsu.yachnyy_m_a.util.SwingUtils;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Main {
    public static final String PROGRAM_NAME_IN_HELP = "program (-h | -w  -i <in-file> [-o <out-file>])";

    public static void main(String[] args){
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
            if (!cmdLine.hasOption("i")) {
                new HelpFormatter().printHelp(PROGRAM_NAME_IN_HELP, cmdLineOptions);
                System.exit(1);
            }
            String inputFilename = cmdLine.getOptionValue("i");
            List<Integer> list = ListUtils.readIntegerListFromFile(inputFilename);
            if (list == null) {
                System.err.printf("Can't read list from \"%s\"%n", inputFilename);
                System.exit(2);
            }

            Task9.process(list);
            try{
                PrintStream out = (cmdLine.hasOption("o")) ? new PrintStream(cmdLine.getOptionValue("o")) : System.out;
                out.println(ListUtils.toString(list));
                out.close();
            } catch (Exception exception){
                SwingUtils.showErrorMessageBox(exception);
            }
        }
    }

    public static void winMain() {
        Locale.setDefault(Locale.ROOT);
        SwingUtils.setDefaultFont("Microsoft Sans Serif", 18);
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrameMain().setVisible(true);
            }
        });
    }
}