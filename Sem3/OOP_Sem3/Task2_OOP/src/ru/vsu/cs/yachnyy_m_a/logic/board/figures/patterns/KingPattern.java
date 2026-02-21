package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class KingPattern implements FigurePattern {
    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        if(current.getFigure().getType() != FigureType.KING) throw new IllegalGameMoveException("Trying to move " +
                current.getFigure().getType().name() + " with a KING pattern");
        PossibleCellSet res = new PossibleCellSet();
        for(Direction direction: Direction.values()){
            BoardCell next = current.getNeighbors().get(direction);
            if(next != null){
                if(next.getFigure() == null) {
                    res.addNonDefeatingCell(next);
                } else if (next.getFigure().getColor() != current.getFigure().getColor()){
                    res.addDefeatingCell(next);
                }
            }
        }
        return res;
    }
}
