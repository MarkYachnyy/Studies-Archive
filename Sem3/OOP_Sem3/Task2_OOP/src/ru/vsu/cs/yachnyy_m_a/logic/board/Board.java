package ru.vsu.cs.yachnyy_m_a.logic.board;


import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;

import java.awt.*;
import java.util.Map;

public class Board {

    private final BoardCell topLeftCell;
    private final Color color1;
    private final Color color2;

    public Board(Color color1, Color color2) {
        if (color1 == color2) throw new IllegalArgumentException("Trying to create a board with figures of same color");
        this.color2 = color2;
        this.color1 = color1;
        BoardCell[][] square_field = new BoardCell[10][10];

        setSymmetricalFigures(square_field, FigureType.CHAMPION, color1, color2, 0, 0);
        setSymmetricalFigures(square_field, FigureType.ROOK, color1, color2, 0, 1);
        setSymmetricalFigures(square_field, FigureType.KNIGHT, color1, color2, 0, 2);
        setSymmetricalFigures(square_field, FigureType.BISHOP, color1, color2, 0, 3);

        square_field[0][4] = new BoardCell(new Figure(FigureType.QUEEN, color1, Direction.SOUTH), "E9");
        square_field[9][4] = new BoardCell(new Figure(FigureType.QUEEN, color2, Direction.NORTH), "E0");
        square_field[0][5] = new BoardCell(new Figure(FigureType.KING, color1, Direction.SOUTH), "F9");
        square_field[9][5] = new BoardCell(new Figure(FigureType.KING, color2, Direction.NORTH), "F0");

        for (int j = 0; j <= 4; j++) {
            setSymmetricalFigures(square_field, FigureType.PAWN, color1, color2, 1, j);
        }

        for (int i = 2; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                setSymmetricalEmptyCells(square_field, i, j);
            }
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Map<Direction, BoardCell> top_left = square_field[i][j].getNeighbors();
                Map<Direction, BoardCell> top_right = square_field[i][j + 1].getNeighbors();
                Map<Direction, BoardCell> bottom_left = square_field[i + 1][j].getNeighbors();
                Map<Direction, BoardCell> bottom_right = square_field[i + 1][j + 1].getNeighbors();

                top_left.put(Direction.SOUTH, square_field[i + 1][j]);
                top_left.put(Direction.EAST, square_field[i][j + 1]);
                top_left.put(Direction.SOUTH_EAST, square_field[i + 1][j + 1]);

                top_right.put(Direction.WEST, square_field[i][j]);
                top_right.put(Direction.SOUTH_WEST, square_field[i + 1][j]);

                bottom_left.put(Direction.NORTH_EAST, square_field[i][j + 1]);
                bottom_left.put(Direction.NORTH, square_field[i][j]);

                bottom_right.put(Direction.NORTH_WEST, square_field[i][j]);
            }
            square_field[i][9].getNeighbors().put(Direction.SOUTH, square_field[i + 1][9]);
            square_field[i + 1][9].getNeighbors().put(Direction.NORTH, square_field[i][9]);
        }
        for (int j = 0; j < 9; j++) {
            square_field[9][j].getNeighbors().put(Direction.EAST, square_field[9][j + 1]);
            square_field[9][j + 1].getNeighbors().put(Direction.WEST, square_field[9][j + 1]);
        }

        BoardCell top_left = new BoardCell(new Figure(FigureType.WIZARD, color1, Direction.SOUTH), "W4");
        top_left.getNeighbors().put(Direction.SOUTH_EAST, square_field[0][0]);
        square_field[0][0].getNeighbors().put(Direction.NORTH_WEST, top_left);

        BoardCell top_right = new BoardCell(new Figure(FigureType.WIZARD, color1, Direction.SOUTH), "W3");
        top_right.getNeighbors().put(Direction.SOUTH_WEST, square_field[0][9]);
        square_field[0][9].getNeighbors().put(Direction.NORTH_EAST, top_right);

        BoardCell bottom_right = new BoardCell(new Figure(FigureType.WIZARD, color2, Direction.NORTH), "W2");
        bottom_right.getNeighbors().put(Direction.NORTH_WEST, square_field[9][9]);
        square_field[9][9].getNeighbors().put(Direction.SOUTH_EAST, bottom_right);

        BoardCell bottom_left = new BoardCell(new Figure(FigureType.WIZARD, color2, Direction.NORTH), "W1");
        bottom_left.getNeighbors().put(Direction.NORTH_EAST, square_field[9][0]);
        square_field[9][0].getNeighbors().put(Direction.SOUTH_WEST, bottom_left);

        topLeftCell = top_left;
    }

    public BoardCell getTopLeftCell() {
        return topLeftCell;
    }

    public Color getColor1() {
        return color1;
    }

    public Color getColor2() {
        return color2;
    }

    private void setSymmetricalFigures(BoardCell[][] square_field, FigureType figureType, Color color1, Color color2, int i, int j) {
        Figure figureUpper = new Figure(figureType, color1, Direction.SOUTH);
        square_field[i][j] = new BoardCell(figureUpper, getCoordinate(i, j));
        figureUpper = new Figure(figureType, color1, Direction.SOUTH);
        square_field[i][9 - j] = new BoardCell(figureUpper, getCoordinate(i, 9 - j));
        Figure figureLower = new Figure(figureType, color2, Direction.NORTH);
        square_field[9 - i][j] = new BoardCell(figureLower, getCoordinate(9 - i, j));
        figureLower = new Figure(figureType, color2, Direction.NORTH);
        square_field[9 - i][9 - j] = new BoardCell(figureLower, getCoordinate(9 - i, 9 - j));
    }

    private void setSymmetricalEmptyCells(BoardCell[][] square_field, int i, int j) {
        square_field[i][j] = new BoardCell(null, getCoordinate(i, j));
        square_field[i][9 - j] = new BoardCell(null, getCoordinate(i, 9 - j));
        square_field[9 - i][j] = new BoardCell(null, getCoordinate(9 - i, j));
        square_field[9 - i][9 - j] = new BoardCell(null, getCoordinate(9 - i, 9 - j));
    }

    private String getCoordinate(int row, int col) {
        char letter = (char) ('A' + col);
        int number = 9 - row;
        return String.valueOf(letter) + number;
    }

}
