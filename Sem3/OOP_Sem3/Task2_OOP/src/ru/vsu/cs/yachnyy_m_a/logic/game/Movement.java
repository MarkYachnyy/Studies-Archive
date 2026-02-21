package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;

public class Movement {
    private BoardCell source;
    private BoardCell destination;

    public BoardCell getSource() {
        return source;
    }

    public BoardCell getDestination() {
        return destination;
    }

    public Movement(BoardCell source, BoardCell destination) {
        this.source = source;
        this.destination = destination;
    }
}
