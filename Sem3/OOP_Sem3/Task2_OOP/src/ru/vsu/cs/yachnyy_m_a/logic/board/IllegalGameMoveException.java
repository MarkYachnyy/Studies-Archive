package ru.vsu.cs.yachnyy_m_a.logic.board;

public class IllegalGameMoveException extends Exception{
    public IllegalGameMoveException(String message) {
        super(message);
    }

    public IllegalGameMoveException() {
        this("");
    }
}
