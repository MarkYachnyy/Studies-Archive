package ru.vsu.cs.yachnyy_m_a.logic.board.figures;

import ru.vsu.cs.yachnyy_m_a.logic.board.Direction;

import java.awt.*;
import java.util.Collections;

public class Figure {
    private FigureType type;
    private Color color;
    private Direction front;

    private boolean hasMoved;

    public Figure(FigureType type, Color color, Direction front) {
        this.type = type;
        this.color = color;
        this.front = front;
        this.hasMoved = false;
    }

    public FigureType getType() {
        return type;
    }

    public Color getColor() {
        return color;
    }

    public Direction getFront() {
        return front;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean val){
        this.hasMoved = val;
    }
}
