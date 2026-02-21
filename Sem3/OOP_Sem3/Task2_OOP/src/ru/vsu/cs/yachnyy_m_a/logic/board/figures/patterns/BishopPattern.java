package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class BishopPattern implements FigurePattern {
    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        if (current.getFigure().getType() != FigureType.BISHOP) throw new IllegalGameMoveException("Trying to move " +
                current.getFigure().getType().name() + " with a BISHOP pattern");
        PossibleCellSet res = new PossibleCellSet();
        for (Direction direction : new Direction[]{Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_EAST, Direction.NORTH_WEST}) {
            BoardCell next = current.getNeighbors().get(direction);
            while (next != null && next.getFigure() == null) {
                res.addNonDefeatingCell(next);
                next = next.getNeighbors().get(direction);
            }
            if (next != null && next.getFigure().getColor() != current.getFigure().getColor()) res.addDefeatingCell(next);
        }
        return res;
    }
}
