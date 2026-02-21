package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class PawnPattern implements FigurePattern {
    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        Figure pawn = current.getFigure();
        if (pawn.getType() != FigureType.PAWN) throw new IllegalGameMoveException("TRYING TO MOVE PAWN");
        PossibleCellSet res = new PossibleCellSet();
        BoardCell frontCell = current.getNeighbors().get(pawn.getFront());
        if (frontCell != null && frontCell.getFigure() == null) {
            res.addNonDefeatingCell(frontCell);
            BoardCell farFrontCell = frontCell.getNeighbors().get(pawn.getFront());
            if (!pawn.hasMoved() && farFrontCell.getFigure() == null) {
                res.addNonDefeatingCell(farFrontCell);
            }
        }

        Direction[] attackDirections = current.getFigure().getFront() == Direction.NORTH ? new Direction[]{Direction.NORTH_EAST, Direction.NORTH_WEST} :
                new Direction[]{Direction.SOUTH_WEST, Direction.SOUTH_EAST};
        BoardCell cornerCell1 = current.getNeighbors().get(attackDirections[0]);
        if (cornerCell1 != null && cornerCell1.getFigure() != null && cornerCell1.getFigure().getColor() != pawn.getColor()) {
            res.addDefeatingCell(cornerCell1);
        }
        BoardCell cornerCell2 = current.getNeighbors().get(attackDirections[1]);
        if (cornerCell2 != null && cornerCell2.getFigure() != null && cornerCell2.getFigure().getColor() != pawn.getColor()) {
            res.addDefeatingCell(cornerCell2);
        }
        return res;
    }
}
