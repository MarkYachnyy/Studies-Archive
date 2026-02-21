package ru.vsu.cs.yachnyy_m_a.logic.board;

import ru.vsu.cs.yachnyy_m_a.logic.board.figures.Figure;

import java.util.HashMap;
import java.util.Map;

public class BoardCell {

    private final String coordinate;
    private Map<Direction, BoardCell> neighbors;
    private Figure figure;

    protected BoardCell(Figure figure, String coordinate) {
        this.figure = figure;
        this.coordinate = coordinate;
        neighbors = new HashMap<>();
    }

    public Map<Direction, BoardCell> getNeighbors() {
        return neighbors;
    }

    public Figure getFigure() {
        return figure;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public String getCoordinate() {
        return coordinate;
    }

    public int hashCode(){
        return coordinate.hashCode();
    }
}

