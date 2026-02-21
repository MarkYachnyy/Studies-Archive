package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;

import java.awt.*;
import java.util.*;

public class RandomizedPlayerBot {
    private Game game;
    private Color color;

    public RandomizedPlayerBot(Game game, Color color) {
        this.game = game;
        this.color = color;
    }

    public Movement randomTurn() {
        Map<BoardCell, PossibleCellSet> possibleTurns = BoardAnalysis.getPossibleTurns(game.getBoard(), color);
        Set<BoardCell> attackingCells = new HashSet<>();
        possibleTurns.keySet().stream().filter(cell -> !possibleTurns.get(cell).getDefeatingCells().isEmpty()).forEach(attackingCells::add);
        Set<BoardCell> possibleSources = attackingCells.isEmpty() ? possibleTurns.keySet() : attackingCells;
        BoardCell source = RandomUtils.randomElement(possibleSources);
        BoardCell destination = RandomUtils.randomElement(attackingCells.isEmpty() ? possibleTurns.get(source).getNonDefeatingCells() :
                possibleTurns.get(source).getDefeatingCells());

        return new Movement(source, destination);
    }

    public Color getColor() {
        return color;
    }

}
