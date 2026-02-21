package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;

import java.util.HashSet;
import java.util.Set;

public class PossibleCellSet {
    private Set<BoardCell> nonDefeatingCells = new HashSet<>();
    private Set<BoardCell> defeatingCells = new HashSet<>();

    public Set<BoardCell> getNonDefeatingCells() {
        return nonDefeatingCells;
    }

    public Set<BoardCell> getDefeatingCells() {
        return defeatingCells;
    }

    public Set<BoardCell> getAllCells(){
        HashSet<BoardCell> res = new HashSet<>(nonDefeatingCells);
        res.addAll(defeatingCells);
        return res;
    }

    public void addNonDefeatingCell(BoardCell cell){
        this.nonDefeatingCells.add(cell);
    }

    public void addDefeatingCell(BoardCell cell){
        this.defeatingCells.add(cell);
    }

    public boolean isEmpty(){
        return nonDefeatingCells.isEmpty() && defeatingCells.isEmpty();
    }
}
