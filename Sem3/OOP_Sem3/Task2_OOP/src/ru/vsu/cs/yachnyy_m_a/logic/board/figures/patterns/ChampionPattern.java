package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class ChampionPattern implements FigurePattern {

    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        if (current.getFigure().getType() != FigureType.CHAMPION) throw new IllegalGameMoveException("Trying to move " +
                current.getFigure().getType().name() + " with a CHAMPION pattern");

        PossibleCellSet res = new PossibleCellSet();
        for (Direction direction : new Direction[]{Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_WEST, Direction.SOUTH_EAST}) {
            BoardCell next = current.getNeighbors().get(direction);
            if (next != null) {
                next = next.getNeighbors().get(direction);
                if (next != null) {
                    if (next.getFigure() == null) {
                        res.addNonDefeatingCell(next);
                    } else if (next.getFigure().getColor() != current.getFigure().getColor()) {
                        res.addDefeatingCell(next);
                    }
                }
            }
        }

        for (Direction direction : new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST}) {
            BoardCell next = current.getNeighbors().get(direction);
            if (next != null) {
                if (next.getFigure() == null) {
                    res.addNonDefeatingCell(next);
                } else if (next.getFigure().getColor() != current.getFigure().getColor()) {
                    res.addDefeatingCell(next);
                }
                next = next.getNeighbors().get(direction);
                if (next != null) {
                    if (next.getFigure() == null) {
                        res.addNonDefeatingCell(next);
                    } else if (next.getFigure().getColor() != current.getFigure().getColor()) {
                        res.addDefeatingCell(next);
                    }
                }
            }
        }

        return res;
    }
}
