package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public abstract interface FigurePattern {
    public abstract PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException;
}
