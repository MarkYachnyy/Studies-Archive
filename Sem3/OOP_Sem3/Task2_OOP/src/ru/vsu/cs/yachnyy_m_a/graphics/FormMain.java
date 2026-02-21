package ru.vsu.cs.yachnyy_m_a.graphics;

import ru.vsu.cs.yachnyy_m_a.logic.board.IllegalGameMoveException;
import ru.vsu.cs.yachnyy_m_a.logic.game.Game;
import ru.vsu.cs.yachnyy_m_a.logic.game.Movement;
import ru.vsu.cs.yachnyy_m_a.logic.game.RandomizedPlayerBot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormMain extends JFrame {

    private JPanel DrawPanelContainer;
    private JButton PauseButton;
    private JPanel PanelMain;
    private JLabel GameStatusLabel;
    private JLabel TurnCountLabel;
    private DrawBoardPanel DrawPanel;

    private Game game;
    private RandomizedPlayerBot bot1;
    private RandomizedPlayerBot bot2;
    private Movement nextMovement;
    private RandomizedPlayerBot playingNow;

    private Timer gameTimer = new Timer(50, a -> {
        if (nextMovement == null) {
            nextMovement = playingNow.randomTurn();
            DrawPanel.setMovement(nextMovement);
        } else {
            DrawPanel.setMovement(null);
            try {
                boolean won = game.turn(nextMovement);
                if (won) finishGame();
                playingNow = playingNow == bot1 ? bot2 : bot1;
                this.nextMovement = null;
            } catch (IllegalGameMoveException e) {
                e.printStackTrace();
            }
        }
        DrawPanel.repaint();
    });

    public FormMain() {
        setGame(new Game(Color.RED, Color.BLUE));

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.setTitle("OmegaChess");
        pack();

    }

    private void setGame(Game game){
        this.game = game;
        this.bot1 = new RandomizedPlayerBot(game, game.getBoard().getColor1());
        this.bot2 = new RandomizedPlayerBot(game, game.getBoard().getColor2());
        this.playingNow = bot1;
        if(DrawPanelContainer.getComponentCount() > 0) DrawPanelContainer.remove(0);
        DrawPanel = new DrawBoardPanel(game.getBoard());
        DrawPanelContainer.add(new JScrollPane(DrawPanel));
        SwingUtils.setFixedSize(DrawPanel, DrawBoardPanel.CELL_SIZE * 12 + DrawBoardPanel.BORDER * 2, DrawBoardPanel.CELL_SIZE * 12 + DrawBoardPanel.BORDER * 2);
        PauseButton.removeActionListener(pauseButtonListener2);
        PauseButton.addActionListener(pauseButtonListener1);
        PauseButton.setText("ЗАПУСТИТЬ");
        pack();
    }

    private void finishGame() {
        gameTimer.stop();
        PauseButton.removeActionListener(pauseButtonListener1);
        PauseButton.addActionListener(pauseButtonListener2);
        GameStatusLabel.setForeground(playingNow.getColor());
        GameStatusLabel.setText("ИГРА ОКОНЧЕНА");
        GameStatusLabel.setBackground(playingNow == bot1 ? bot2.getColor() : bot1.getColor());
        GameStatusLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 30));
        TurnCountLabel.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
        TurnCountLabel.setText("количество ходов: " + game.turnCount());
        PauseButton.setText("ПЕРЕЗАПУСТИТЬ");
        pack();
    }

    private ActionListener pauseButtonListener1 = e -> {
        if (gameTimer.isRunning()) {
            gameTimer.stop();
            PauseButton.setText("ВОЗОБНОВИТЬ");
        } else {
            PauseButton.setText("ПАУЗА");
            gameTimer.restart();
        }
        DrawPanel.repaint();
    };

    private ActionListener pauseButtonListener2 = e -> {
        setGame(new Game(game.getBoard().getColor1(), game.getBoard().getColor2()));
        DrawPanel.repaint();
        TurnCountLabel.setText("");
        GameStatusLabel.setText("");
        gameTimer = new Timer(20, a -> {
            if (nextMovement == null) {
                nextMovement = playingNow.randomTurn();
                DrawPanel.setMovement(nextMovement);
            } else {
                DrawPanel.setMovement(null);
                try {
                    boolean won = game.turn(nextMovement);
                    if (won) finishGame();
                    playingNow = playingNow == bot1 ? bot2 : bot1;
                    this.nextMovement = null;
                } catch (IllegalGameMoveException ex) {
                    ex.printStackTrace();
                }
            }
            DrawPanel.repaint();
        });
        pack();
    };
}
