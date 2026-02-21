package ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.game.PossibleCellSet;

public class QueenPattern implements FigurePattern{
    @Override
    public PossibleCellSet getPossibleCells(BoardCell current) throws IllegalGameMoveException {
        if(current.getFigure().getType() != FigureType.QUEEN) throw new IllegalGameMoveException();
        PossibleCellSet res = new PossibleCellSet();
        for(Direction direction: Direction.values()){
            BoardCell next = current.getNeighbors().get(direction);
            while(next != null && next.getFigure() == null){
                res.addNonDefeatingCell(next);
                next = next.getNeighbors().get(direction);
            }
            if(next != null && next.getFigure().getColor() != current.getFigure().getColor()) res.addDefeatingCell(next);
        }
        return res;
    }
}
