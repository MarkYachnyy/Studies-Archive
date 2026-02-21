package ru.vsu.cs.yachnyy_m_a.logic.game;

import ru.vsu.cs.yachnyy_m_a.logic.board.BoardCell;
import ru.vsu.cs.yachnyy_m_a.logic.board.Board;
import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.FigureType;
import ru.vsu.cs.yachnyy_m_a.logic.board.figures.patterns.*;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoardAnalysis {

    private static Map<FigureType, FigurePattern> figurePatterns = new HashMap<>();

    static {
        figurePatterns.put(FigureType.KING, new KingPattern());
        figurePatterns.put(FigureType.QUEEN, new QueenPattern());
        figurePatterns.put(FigureType.BISHOP, new BishopPattern());
        figurePatterns.put(FigureType.KNIGHT, new KnightPattern());
        figurePatterns.put(FigureType.ROOK, new RookPattern());
        figurePatterns.put(FigureType.PAWN, new PawnPattern());
        figurePatterns.put(FigureType.CHAMPION, new ChampionPattern());
        figurePatterns.put(FigureType.WIZARD, new WizardPattern());
    }

    public static Map<BoardCell, PossibleCellSet> getPossibleTurns(Board board, Color color) {
        Set<BoardCell> allyFiguresLocation = getAllyFiguresLocation(board, color);
        Map<BoardCell, PossibleCellSet> res = new HashMap<>();
        for (BoardCell cell : allyFiguresLocation) {
            try {
                PossibleCellSet possibleCellSet = figurePatterns.get(cell.getFigure().getType()).getPossibleCells(cell);
                if(!possibleCellSet.isEmpty())res.put(cell, possibleCellSet);
            } catch (IllegalGameMoveException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    public static Set<BoardCell> getAllyFiguresLocation(Board board, Color color) {
        Set<BoardCell> res = new HashSet<>();
        getAllyFiguresLocation(board.getTopLeftCell(), new HashSet<>(), res, color);
        return res;
    }

    private static void getAllyFiguresLocation(BoardCell cell, Set<BoardCell> visited, Set<BoardCell> result, Color color) {
        visited.add(cell);
        if (cell.getFigure() != null && cell.getFigure().getColor() == color) {
            result.add(cell);
        }
        for (BoardCell nCell : cell.getNeighbors().values()) {
            if (nCell != null && !visited.contains(nCell)) {
                getAllyFiguresLocation(nCell, visited, result, color);
            }
        }
    }
}
