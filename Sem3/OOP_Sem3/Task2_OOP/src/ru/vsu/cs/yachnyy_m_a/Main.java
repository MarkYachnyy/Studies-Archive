package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.graphics.FormMain;
import ru.vsu.cs.yachnyy_m_a.logic.game.Game;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Game game = new Game(Color.RED, Color.BLACK);
        java.awt.EventQueue.invokeLater(() -> new FormMain().setVisible(true));
    }
}