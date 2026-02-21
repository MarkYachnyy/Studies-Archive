package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.mytree.AVLTree;
import ru.vsu.cs.yachnyy_m_a.trees.BinaryTree;
import ru.vsu.cs.yachnyy_m_a.trees.BinaryTreePainter;
import ru.vsu.cs.yachnyy_m_a.trees.SearchTreeMatchData;
import ru.vsu.cs.yachnyy_m_a.trees.SimpleBinaryTree;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JTextField TextFieldBracketStr;
    private JButton ButtonCheckTree;
    private JPanel PanelPaint;
    private JButton ButtonPaint;
    private JLabel LabelResult;

    private SimpleBinaryTree<Integer> tree;

    public FormMain(){
        this.setTitle("Превращение ДД в ДДП");
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();

        ButtonPaint.addActionListener(e -> {
//            try{
//                String bracketStr = TextFieldBracketStr.getText();
//                tree = new SimpleBinaryTree<>(Integer::parseInt, Objects::toString);
//                tree.fromBracketNotation(bracketStr);
//                paintTree(tree, null);
//                LabelResult.setText("-");
//            } catch (Exception exception){
//                SwingUtils.showErrorMessageBox(exception);
//            }
            AVLTree<Integer> tr = new AVLTree<>();
            for (int i = 0; i < 100; i++) {
                tr.put(new Random().nextInt(20));
            }
            for (int i = 0; i < 10; i++) {
                tr.remove(new Random().nextInt(20));
            }
            paintTree(tr, null);
            SwingUtils.showInfoMessageBox(tr.getMin() + "  " + tr.getMax());
        });

        ButtonCheckTree.addActionListener(e -> {
            try{
                SearchTreeMatchData data = SearchTreeMatchData.check(tree, Integer::compareTo);
                paintTree(tree, data);
                if(data.getOddPaths().size() == 0 && data.getNonUniquePaths().size() == 0){
                    LabelResult.setText("Дерево является двоичным деревом поиска");
                } else {
                    LabelResult.setText("Дерево "+(data.canTransform() ? "можно": "нельзя") + " превратить в ДДП удалением одного листа");
                }
            } catch (Exception exception){
                exception.printStackTrace();
                SwingUtils.showErrorMessageBox(exception);
            }
        });

        AVLTree<Integer> tr = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            tr.put(new Random().nextInt(20));
        }
        paintTree(tr, null);
    }

    private void paintTree(BinaryTree<Integer> t, SearchTreeMatchData data){
        try{
            Graphics2D graphics2D = (Graphics2D) PanelPaint.getGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0,0, PanelPaint.getWidth(), PanelPaint.getHeight());
            BinaryTreePainter.paint(t, PanelPaint.getGraphics(), data);
        } catch (Exception exception){
            SwingUtils.showErrorMessageBox(exception);
        }
    }
}
