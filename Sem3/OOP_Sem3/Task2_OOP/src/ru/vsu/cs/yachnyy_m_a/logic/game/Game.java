package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Board;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;

import java.awt.*;
import java.util.Stack;

public class Game {

    private Board board;
    private Stack<Turn> turns;
    private Color playingNow;

    public Game() {
        this(Color.WHITE, Color.BLACK);
    }

    public Game(Color color1, Color color2) {
        board = new Board(color1, color2);
        playingNow = color1;
        turns = new Stack<>();
    }

    public boolean turn(Movement movement) throws IllegalGameMoveException {
        BoardCell source = movement.getSource();
        BoardCell destination = movement.getDestination();
        if(source.getFigure().getColor() != playingNow) throw new IllegalGameMoveException("Figures of this color are not allowed to move right now");
        Figure defeated = destination.getFigure();
        destination.setFigure(source.getFigure());
        source.setFigure(null);
        turns.add(new Turn(source, destination, destination.getFigure(), defeated, !destination.getFigure().hasMoved()));
        destination.getFigure().setHasMoved(true);

        if(playingNow == board.getColor1()){
            playingNow = board.getColor2();
        } else if(playingNow == board.getColor2()){
            playingNow = board.getColor1();
        }

        return defeated != null && defeated.getType() == FigureType.KING;
    }

    public Board getBoard() {
        return board;
    }

    public Color playingNow(){
        return this.playingNow;
    }

    public int turnCount() {
        return turns.size();
    }
}
