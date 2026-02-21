package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class KnightPattern implements FigurePattern {
    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        if (current.getFigure().getType() != FigureType.KNIGHT) throw new IllegalGameMoveException("Trying to move " +
                current.getFigure().getType().name() + " with a KNIGHT pattern");

        PossibleCellSet res = new PossibleCellSet();

        Direction[][] paths = new Direction[][]{
                {Direction.NORTH, Direction.NORTH_WEST},
                {Direction.NORTH, Direction.NORTH_EAST},
                {Direction.WEST, Direction.NORTH_WEST},
                {Direction.WEST, Direction.SOUTH_WEST},
                {Direction.SOUTH, Direction.SOUTH_WEST},
                {Direction.SOUTH, Direction.SOUTH_EAST},
                {Direction.EAST, Direction.NORTH_EAST},
                {Direction.EAST, Direction.SOUTH_EAST},

                {Direction.NORTH_WEST, Direction.NORTH},
                {Direction.NORTH_EAST, Direction.NORTH},
                {Direction.NORTH_WEST, Direction.WEST},
                {Direction.SOUTH_WEST, Direction.WEST},
                {Direction.SOUTH_WEST, Direction.SOUTH},
                {Direction.SOUTH_EAST, Direction.SOUTH},
                {Direction.NORTH_EAST, Direction.EAST},
                {Direction.SOUTH_EAST, Direction.EAST}
        };

        outer:
        for (Direction[] path : paths) {
            BoardCell next = current;
            for (Direction direction : path) {
                next = next.getNeighbors().get(direction);
                if (next == null) continue outer;
            }
            if (next.getFigure() == null) {
                res.addNonDefeatingCell(next);
            } else if (next.getFigure().getColor() != current.getFigure().getColor()) {
                res.addDefeatingCell(next);
            }
        }


        return res;
    }
}
