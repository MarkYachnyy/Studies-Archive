package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.DrawUtils;
import ru.vsu.cs.yachnyy_m_a.util.JTableUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JButton ButtonLoadMazeFromFile;
    private JButton ButtonFindPath;
    private JButton ButtonWriteMazeIntoFile;
    private JTabbedPane tabbedPane1;
    private JPanel PanelPaintMaze;
    private JScrollPane ScrollPanePaintMaze;
    private JTextPane TextPaneMaze;
    private JTextPane TextPanePath;
    private JTable TableEnterCoordinates;
    private JTable TableExitCoordinates;
    private JPanel PanelPaintArea;
    private JSpinner SpinnerCellSize;
    private JButton ButtonGetEnterAsSelectedCell;
    private JButton ButtonGetExitAsSelectedCell;


    private JFileChooser InputFileChooser;
    private JFileChooser OutputFileChooser;

    public static int CELL_SIZE = 60;
    public static final int CELL_BORDER = 3;
    private Maze maze;
    private Maze.Path path;
    private int[] selected_cell = null;

    public FormMain() {
        this.setContentPane(PanelMain);
        this.setTitle("Task1_24");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        JTableUtils.initJTableForArray(TableEnterCoordinates, 45, false, false, false, false);
        JTableUtils.initJTableForArray(TableExitCoordinates, 45, false, false, false, false);
        TableEnterCoordinates.setRowHeight(47);
        TableExitCoordinates.setRowHeight(47);

        InputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", ".txt"));

        OutputFileChooser = new JFileChooser();
        OutputFileChooser.setCurrentDirectory(new File("."));
        OutputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", ".txt"));

        PanelPaintMaze = new JPanel() {
            private Dimension paintSize = new Dimension(0, 0);

            @Override
            public void paintComponent(Graphics gr) {
                super.paintComponent(gr);

                int mazeWidth = maze == null ? 0 : (maze.getDimensionCount() == 2 ? CELL_SIZE * (maze.getDimensions()[1] + 2) : CELL_SIZE * 9);
                int mazeHeight = maze == null ? 0 : (maze.getDimensionCount() == 2 ? CELL_SIZE * (maze.getDimensions()[0] + 2) : CELL_SIZE * 7);
                paintSize = new Dimension(mazeWidth, mazeHeight);
                SwingUtils.setFixedSize(this, paintSize.width, paintSize.height);
                Graphics2D g2d = (Graphics2D) gr;
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, mazeWidth, mazeHeight);
                if (maze != null) {
                    if (maze.getDimensionCount() == 2) {
                        PaintMaze(gr, maze, path);
                    } else {
                        PaintVizNotAvailable(gr);
                    }
                }
            }
        };

        ScrollPanePaintMaze = new JScrollPane(PanelPaintMaze);
        PanelPaintArea.add(ScrollPanePaintMaze);

        ButtonLoadMazeFromFile.addActionListener(event -> {
            try {
                if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                    Scanner scanner = new Scanner(new File(InputFileChooser.getSelectedFile().getPath()));
                    StringBuilder input = new StringBuilder();
                    while (scanner.hasNext()) {
                        input.append(scanner.nextLine()).append('\n');
                    }
                    TextPaneMaze.setText(input.toString());
                    maze = Maze.fromString(input.toString());
                    PanelPaintMaze.repaint();
                    String[] blank_array = new String[maze.getDimensionCount()];
                    Arrays.fill(blank_array, "");
                    JTableUtils.writeArrayToJTable(TableExitCoordinates, blank_array);
                    JTableUtils.writeArrayToJTable(TableEnterCoordinates, blank_array);
                    selected_cell = null;
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });

        ButtonFindPath.addActionListener(event -> {
            try {
                selected_cell = null;
                int[] enter_coords = JTableUtils.readIntArrayFromJTable(TableEnterCoordinates);
                int[] exit_coords = JTableUtils.readIntArrayFromJTable(TableExitCoordinates);
                path = maze != null ? maze.findPath(enter_coords, exit_coords) : null;
                if (path != null) {
                    TextPanePath.setText(path.toString());
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }

        });

        ButtonWriteMazeIntoFile.addActionListener(event -> {
            try {
                if(OutputFileChooser.showSaveDialog(PanelMain) == JFileChooser.APPROVE_OPTION){
                    PrintStream stream = new PrintStream(new File(OutputFileChooser.getSelectedFile().getPath()));
                    stream.println(maze == null ? "" : maze.toString());
                    stream.close();
                }
            } catch (IOException e) {
                SwingUtils.showErrorMessageBox(e);
            }

        });

        TextPaneMaze.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                path = null;
                TextPanePath.setText("");
                selected_cell = null;
                try {
                    maze = Maze.fromString(TextPaneMaze.getText());
                    String[] blank_array = new String[maze.getDimensionCount()];
                    Arrays.fill(blank_array, "");
                    JTableUtils.writeArrayToJTable(TableExitCoordinates, blank_array);
                    JTableUtils.writeArrayToJTable(TableEnterCoordinates, blank_array);
                    PanelPaintMaze.repaint();
                } catch (Exception exception) {
                    maze = null;
                }
            }
        });

        PanelPaintMaze.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (maze == null || maze.getDimensionCount() != 2) return;
                int x = e.getX();
                int y = e.getY();
                int i = y / CELL_SIZE - 1;
                int j = x / CELL_SIZE - 1;
                if (i < 0 || j < 0 || i >= maze.getDimensions()[0] || j >= maze.getDimensions()[1]) return;
                int[] coords = new int[]{i,j};
                if(e.getButton() == MouseEvent.BUTTON1){
                    if(selected_cell == null) {
                        selected_cell = coords;
                    } else if(Arrays.equals(coords, selected_cell)){
                        selected_cell = null;
                    } else {
                        if(Math.abs(selected_cell[0] - coords[0]) + Math.abs(selected_cell[1] - coords[1]) == 1){
                            if(maze.containsWall(coords, selected_cell)){
                                maze.removeWall(coords, selected_cell);
                            } else {
                                maze.addWall(coords, selected_cell);
                            }
                            selected_cell = null;
                            TextPaneMaze.setText(maze.toString());
                        } else {
                            selected_cell = coords;
                        }
                    }
                }
            }
        });

        ButtonGetEnterAsSelectedCell.addActionListener(event -> {
            if(selected_cell != null){
                JTableUtils.writeArrayToJTable(TableEnterCoordinates, selected_cell);
                selected_cell = null;
            }
        });

        ButtonGetExitAsSelectedCell.addActionListener(event -> {
            if(selected_cell != null){
                JTableUtils.writeArrayToJTable(TableExitCoordinates, selected_cell);
                selected_cell = null;
            }
        });

        SpinnerCellSize.setValue(CELL_SIZE);

        SpinnerCellSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                CELL_SIZE = Integer.parseInt(SpinnerCellSize.getValue().toString());
            }
        });

        this.pack();
    }

    private void PaintMaze(Graphics gr, Maze maze, Maze.Path path) {
        Font font = new Font("Arial", Font.PLAIN, CELL_SIZE / 2);
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < maze.getDimensions()[0]; i++) {
            DrawUtils.drawStringInCenter(g2d, font, String.valueOf(i), 0, (i + 1) * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        for (int j = 0; j < maze.getDimensions()[0]; j++) {
            DrawUtils.drawStringInCenter(g2d, font, String.valueOf(j), (j + 1) * CELL_SIZE, 0, CELL_SIZE, CELL_SIZE);
        }
        for (int i = 0; i < maze.getDimensions()[0]; i++) {
            for (int j = 0; j < maze.getDimensions()[1]; j++) {
                int x = CELL_SIZE + CELL_SIZE * j;
                int y = CELL_SIZE + CELL_SIZE * i;
                if (path != null) {
                    int index = path.indexOf(new int[]{i, j});
                    if (index >= 0) {
                        g2d.setColor(Color.GREEN);
                        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        g2d.setColor(Color.WHITE);
                        DrawUtils.drawStringInCenter(g2d, font, String.valueOf(index), x, y, CELL_SIZE, CELL_SIZE);
                        if (index == 0 || index == path.size() - 1) {
                            g2d.setColor(Color.RED);
                            g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                            g2d.setColor(Color.YELLOW);
                            DrawUtils.drawStringInCenter(g2d, font, index == 0 ? "A" : "B", x, y, CELL_SIZE, CELL_SIZE);
                        }
                    }
                }
                if (Arrays.equals(new int[]{i, j}, selected_cell)) {
                    g2d.setColor(new Color(182, 225, 252));
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                if (maze.containsWall(new int[]{i, j}, new int[]{i, j - 1})) {
                    g2d.fillRect(x, y, CELL_BORDER, CELL_SIZE);
                }
                if (maze.containsWall(new int[]{i - 1, j}, new int[]{i, j})) {
                    g2d.fillRect(x, y, CELL_SIZE, CELL_BORDER);
                }
                if (maze.containsWall(new int[]{i, j}, new int[]{i, j + 1})) {
                    g2d.fillRect(x + CELL_SIZE - CELL_BORDER, y, CELL_BORDER, CELL_SIZE);
                }
                if (maze.containsWall(new int[]{i + 1, j}, new int[]{i, j})) {
                    g2d.fillRect(x, y + CELL_SIZE - CELL_BORDER, CELL_SIZE, CELL_BORDER);
                }
            }
        }
    }

    private void PaintVizNotAvailable(Graphics gr) {
        Font font = new Font("Arial", Font.PLAIN, CELL_SIZE / 2);
        Graphics2D g2d = (Graphics2D) gr;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                int x = CELL_SIZE + CELL_SIZE * j;
                int y = CELL_SIZE + CELL_SIZE * i;
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
        g2d.setColor(Color.BLACK);
        DrawUtils.drawStringInCenter(g2d, font, "Только для 2D лабиринтов", 0, 0, CELL_SIZE * 9, CELL_SIZE * 7);
    }
}
