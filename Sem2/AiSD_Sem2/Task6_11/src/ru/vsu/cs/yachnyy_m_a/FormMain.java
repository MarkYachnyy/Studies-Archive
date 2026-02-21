package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.mytree.map.PutOrderMap;

import javax.swing.*;
import java.util.Map;
import java.util.Random;

public class FormMain extends JFrame {

    private JPanel PanelMain;
    private JButton ButtonAddPair;
    private JTextField TextFieldKey;
    private JTextField TextFieldValue;
    private JSpinner SpinnerRndPairCount;
    private JButton ButtonAddRandomPairs;
    private JTextArea TextAreaPairsOperation;
    private JTextArea TextAreaIterations;
    private JTextField TextFieldKeyToRemove;
    private JButton ButtonRemovePair;

    private PutOrderMap<Integer, String> map;

    public FormMain(){
        this.setTitle("PutOrderMap");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.pack();

        map = new PutOrderMap<>();
        TextAreaPairsOperation.setEditable(false);
        TextAreaIterations.setEditable(false);

        ButtonAddPair.addActionListener(event -> {
            int key = Integer.parseInt(TextFieldKey.getText());
            String value = TextFieldValue.getText();
            addPair(key, value);
            iterateMap();
        });

        ButtonRemovePair.addActionListener(event -> {
            int key = Integer.parseInt(TextFieldKeyToRemove.getText());
            removePair(key);
            iterateMap();
        });

        ButtonAddRandomPairs.addActionListener(e -> {
            Random rnd = new Random();
            int n = Integer.parseInt(SpinnerRndPairCount.getValue().toString());
            for (int i = 0; i < n; i++) {
                int key = rnd.nextInt(10);
                String value = randomString(rnd.nextInt(8) + 2);
                addPair(key,value);
            }
            iterateMap();
        });
    }

    private String randomString(int length){
        Random rnd = new Random();
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < length; i++) {
            res.append((char) (97 + rnd.nextInt(26)));
        }
        return res.toString();
    }

    private void addPair(int key, String value){
        map.put(key, value);
        TextAreaPairsOperation.setText(TextAreaPairsOperation.getText() + String.format("+[%s: %s]\n", key, value));
    }

    private void removePair(int key){
        map.remove(key);
        TextAreaPairsOperation.setText(TextAreaPairsOperation.getText() + String.format("-[%s]\n", key));
    }

    private void iterateMap(){
        StringBuilder res = new StringBuilder();
        for(Map.Entry<Integer, String> entry: map.entrySet()){
            res.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
        }
        TextAreaIterations.setText(res.toString());
    }
}
