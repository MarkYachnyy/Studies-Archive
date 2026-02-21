package ru.vsu.cs.yachnyy_m_a.graphics;

import ru.vsu.cs.yachnyy_m_a.logic.board.Board;
import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.Movement;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class

DrawBoardPanel extends JPanel {

    public static final int CELL_SIZE = 60;
    private static final HashMap<FigureType, Character> FIGURE_CHAR;
    public static final int BORDER = 10;
    private static final Color BG_COLOR_1 = new Color(240, 140, 71);
    private static final Color BG_COLOR_2 = new Color(255, 207, 159);

    static {
        FIGURE_CHAR = new HashMap<>();
        FIGURE_CHAR.put(FigureType.KING, '♚');
        FIGURE_CHAR.put(FigureType.QUEEN, '♛');
        FIGURE_CHAR.put(FigureType.BISHOP, '♝');
        FIGURE_CHAR.put(FigureType.KNIGHT, '♞');
        FIGURE_CHAR.put(FigureType.ROOK, '♜');
        FIGURE_CHAR.put(FigureType.PAWN, '♟');
        FIGURE_CHAR.put(FigureType.WIZARD, '★');
        FIGURE_CHAR.put(FigureType.CHAMPION, '✜');
    }


    private Board board;
    private Movement movement;

    public DrawBoardPanel(Board board) {
        super();
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (this.getWidth() > 0 && this.getHeight() > 0) {
            g2d.setColor(Color.RED);
        }
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        drawCell(g2d, board.getTopLeftCell(), BORDER, BORDER, BG_COLOR_2, new HashSet<>());
        drawCoordinateDigitsColumn(g2d, BORDER, BORDER + CELL_SIZE);
        drawCoordinateDigitsColumn(g2d, BORDER + CELL_SIZE * 11, BORDER + CELL_SIZE);
        drawCoordinateLettersRow(g2d, BORDER + CELL_SIZE, BORDER);
        drawCoordinateLettersRow(g2d, BORDER + CELL_SIZE, BORDER + CELL_SIZE * 11);
    }

    private static final HashMap<Direction, int[]> DIRECTION_DELTAS;

    static {
        DIRECTION_DELTAS = new HashMap<>();
        DIRECTION_DELTAS.put(Direction.SOUTH, new int[]{0, CELL_SIZE});
        DIRECTION_DELTAS.put(Direction.NORTH, new int[]{0, -CELL_SIZE});
        DIRECTION_DELTAS.put(Direction.WEST, new int[]{-CELL_SIZE, 0});
        DIRECTION_DELTAS.put(Direction.EAST, new int[]{CELL_SIZE, 0});
        DIRECTION_DELTAS.put(Direction.SOUTH_EAST, new int[]{CELL_SIZE, CELL_SIZE});
        DIRECTION_DELTAS.put(Direction.SOUTH_WEST, new int[]{-CELL_SIZE, CELL_SIZE});
        DIRECTION_DELTAS.put(Direction.NORTH_EAST, new int[]{CELL_SIZE, -CELL_SIZE});
        DIRECTION_DELTAS.put(Direction.NORTH_WEST, new int[]{-CELL_SIZE, -CELL_SIZE});
    }

    private void drawCell(Graphics2D g2d, BoardCell cell, int x, int y, Color bgColor, Set<BoardCell> visited) {
        g2d.setColor(bgColor);
        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        if (movement != null && cell == movement.getSource()) {
            g2d.setColor(new Color(141, 182, 0, 120));
            g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        } else if (movement != null && cell == movement.getDestination()) {
            g2d.setColor(new Color(255, 255, 0, 120));
            g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
        Figure figure = cell.getFigure();
        if (figure != null) {
            g2d.setColor(figure.getColor());
            DrawUtils.drawStringInCenter(g2d, new Font("Arial Unicode MS", Font.PLAIN, CELL_SIZE * 4 / 5),
                    String.valueOf(FIGURE_CHAR.get(figure.getType())), x, y, CELL_SIZE, CELL_SIZE);
        }
        visited.add(cell);
        for (Direction direction : Direction.values()) {
            BoardCell neighbor = cell.getNeighbors().get(direction);
            if (neighbor != null && !visited.contains(neighbor)) {
                boolean diagonal = direction == Direction.NORTH_WEST || direction == Direction.NORTH_EAST || direction == Direction.SOUTH_EAST || direction == Direction.SOUTH_WEST;
                Color newBgColor = diagonal ? bgColor : (bgColor == BG_COLOR_1 ? BG_COLOR_2 : BG_COLOR_1);
                drawCell(g2d, neighbor, x + DIRECTION_DELTAS.get(direction)[0], y + DIRECTION_DELTAS.get(direction)[1], newBgColor, visited);
            }
        }
    }

    private void drawCoordinateDigitsColumn(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            DrawUtils.drawStringInCenter(g2d, new Font("Arial Unicode MS", Font.PLAIN, CELL_SIZE / 2), String.valueOf(i), x, y, CELL_SIZE, CELL_SIZE);
            y += CELL_SIZE;
        }
    }

    private void drawCoordinateLettersRow(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < 10; i++) {
            DrawUtils.drawStringInCenter(g2d, new Font("Arial Unicode MS", Font.PLAIN, CELL_SIZE / 2), String.valueOf((char) ('A' + i)), x, y, CELL_SIZE, CELL_SIZE);
            x += CELL_SIZE;
        }
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }
}
