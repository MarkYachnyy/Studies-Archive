package ru.vsu.cs.yachnyy_m_a;

import javafx.scene.control.TableFocusModel;
import ru.vsu.cs.yachnyy_m_a.logic.*;
import ru.vsu.cs.yachnyy_m_a.logic.factories.PriorityQueueFactory;
import ru.vsu.cs.yachnyy_m_a.logic.factories.QueueFactory;
import ru.vsu.cs.yachnyy_m_a.util.JTableUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

public class FormMain extends JFrame {

    private JPanel PanelMain;
    private JButton ButtonLoadCustomerList;
    private JButton ButtonProcess;
    private JTable TableInput;
    private JComboBox ComboBoxImplementation;
    private JTable TableFieldNames;
    private JTable TableOutput;

    private JFileChooser InputFileChooser;

    private ShopQueue shopQueue;
    private Timer timer;

    public FormMain() {
        this.setTitle("Task3_20");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(PanelMain);
        this.pack();

        InputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", "txt"));

        JTableUtils.initJTableForArray(TableFieldNames, 150, false, false, false, false);
        TableFieldNames.setRowHeight(40);
        JTableUtils.initJTableForArray(TableInput, 50, true, false, true, false);
        TableInput.setRowHeight(50);
        JTableUtils.initJTableForArray(TableOutput, 150, false, false, false, false);
        TableOutput.setRowHeight(40);

        JTableUtils.writeArrayToJTable(TableFieldNames, new String[]{"Пришёл", "Выбирал", "Товары", "Ушёл"});

        ButtonLoadCustomerList.addActionListener(event -> {
            if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                Customer[] input = Customer.loadCustomerArrayFromFile(InputFileChooser.getSelectedFile().getPath());
                JTableUtils.writeArrayToJTable(TableInput, Customer.toIntMatrix(input));
            }
        });

        ButtonProcess.addActionListener(event -> {
            try{
                Customer[] input = Customer.readCustomerArrayFromIntMatrix(JTableUtils.readIntMatrixFromJTable(TableInput));
                QueueFactory<Customer> nFactory = ComboBoxImplementation.getSelectedIndex() == 0 ? LinkedList::new : MyLinkedListQueue::new;
                PriorityQueueFactory<Customer> pFactory = ComboBoxImplementation.getSelectedIndex() == 0 ?
                        (comp) -> new PriorityQueue<>(Customer.CashComparator()) :
                        (comp) -> new MyPriorityQueue<>(Customer.CashComparator());
                shopQueue = new ShopQueue(input, nFactory, pFactory);
                List<CustomerData> outputData = shopQueue.getExitTimeList();
                int[][] output = new int[outputData.size()][4];
                for (int i = 0; i < output.length; i++) {
                    CustomerData data = outputData.get(i);
                    Customer customer = data.getCustomer();
                    output[i] = new int[]{customer.getArrivalTime(), customer.getChoosingTime(), customer.getGoodsCount(), data.getLeaving_time()};
                }
                JTableUtils.writeArrayToJTable(TableOutput, output);
            } catch (Exception e){
                SwingUtils.showErrorMessageBox(e);
            }

        });

        this.pack();
    }
}
