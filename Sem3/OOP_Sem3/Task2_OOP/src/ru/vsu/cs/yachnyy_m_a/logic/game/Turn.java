package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;

public class Turn {
    private BoardCell source;
    private BoardCell destination;
    private Figure movedFigure;
    private Figure defeatedFigure;
    private boolean isFiguresFirstMove;

    public Turn(BoardCell source, BoardCell destination, Figure movedFigure, Figure defeatedFigure, boolean isFiguresFirstMove) {
        this.source = source;
        this.destination = destination;
        this.movedFigure = movedFigure;
        this.defeatedFigure = defeatedFigure;
        this.isFiguresFirstMove = isFiguresFirstMove;
    }

    public BoardCell getSource() {
        return source;
    }

    public BoardCell getDestination() {
        return destination;
    }

    public Figure getMovedFigure() {
        return movedFigure;
    }

    public Figure getDefeatedFigure() {
        return defeatedFigure;
    }

    public boolean isFiguresFirstMove() {
        return isFiguresFirstMove;
    }
}
