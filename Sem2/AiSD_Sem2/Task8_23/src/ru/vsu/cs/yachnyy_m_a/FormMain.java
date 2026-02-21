package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.DrawUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.HashSet;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JButton ButtonAddResistor;
    private JPanel PanelPaintArea;
    private JSpinner SpinnerNewResistance;
    private JPanel PanelPaintBatteryArea;
    private JSpinner SpinnerResistance;
    private JLabel LabelResistorAmperage;
    private JLabel LabelResistorVoltage;
    private JButton ButtonFlipResistor;
    private JSpinner SpinnerVoltage;
    private JPanel PanelPaintCircuit;
    private JPanel PanelPaintBattery;

    private ElectricCircuit circuit;
    private HashMap<Integer, Point> resistor_positions;
    private int highlighted_resistor_id;
    private int highlighted_node;
    private int highlighted_node_2;
    private HashMap<Integer, Point> node_positions;

    int pos_node = 0;
    int neg_node = 0;
    Point pos_node_position = null;
    Point neg_node_position = null;

    private boolean switch_activated = false;
    private boolean mouse_on_switch = false;
    private int voltage = 10;
    private int selected_resistor_id = 0;
    private HashMap<Integer, Double> amperage_map = null;
    private double amperage = 0;

    private boolean isShortCircuit() {
        return amperage == Double.POSITIVE_INFINITY;
    }

    private static final int RESISTOR_WIDTH = 150;
    private static final int RESISTOR_HEIGHT = 75;
    private static final int LINES_THICKNESS = 3;
    private static final int NODE_RADIUS = 10;
    private static final int PROXIMITY_RADIUS = NODE_RADIUS + 6;
    private static final int CLEMM_RADIUS = 20;
    private static final int CLEMM_INITIAL_OFFSET = 50;
    private static final int WIRE_THICKNESS = 6;

    private static final int SWITCH_WIDTH = 200;
    private static final int SWITCH_HEIGHT = 100;
    private static final int AMPERMETER_RADIUS = 40;


    public FormMain() {
        this.setContentPane(PanelMain);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        resistor_positions = new HashMap<>();
        node_positions = new HashMap<>();
        highlighted_resistor_id = 0;
        highlighted_node = 0;
        highlighted_node_2 = 0;
        circuit = new ElectricCircuit();


        PanelPaintCircuit = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                SwingUtils.setFixedSize(PanelPaintCircuit, PanelPaintArea.getWidth() - 10, PanelPaintArea.getHeight() - 10);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, PanelPaintCircuit.getWidth(), PanelPaintCircuit.getHeight());
                g2d.setColor(new Color(182, 225, 252));

                if (highlighted_resistor_id > 0) {
                    g2d.fillRect(resistor_positions.get(highlighted_resistor_id).x - 10, resistor_positions.get(highlighted_resistor_id).y - 10, RESISTOR_WIDTH + 20, RESISTOR_HEIGHT + 20);
                }
                if (highlighted_node > 0) {
                    fillCircle(g2d, node_positions.get(highlighted_node), PROXIMITY_RADIUS);
                }
                if (highlighted_node_2 > 0) {
                    g2d.setColor(new Color(188, 245, 188));
                    fillCircle(g2d, node_positions.get(highlighted_node_2), PROXIMITY_RADIUS);
                }
                g2d.setColor(Color.BLACK);
                for (Point p : node_positions.values()) {
                    fillCircle(g2d, p, NODE_RADIUS);
                }
                for (int res_id : resistor_positions.keySet()) {
                    paintResistor(res_id, g2d);
                }
                drawClemmas(g2d);
            }
        };

        PanelPaintCircuit.setIgnoreRepaint(true);
        addCircuitComponentMotionListener(PanelPaintCircuit);
        JScrollPane pane = new JScrollPane(PanelPaintCircuit);
        PanelPaintArea.add(pane);
        this.pack();

        ButtonAddResistor.addActionListener(event -> {
            int new_id = circuit.addResistor(Integer.parseInt(SpinnerNewResistance.getValue().toString()));
            resistor_positions.put(new_id, new Point((PanelPaintArea.getWidth() - 10 - RESISTOR_WIDTH) / 2, 5));
            recalculateAmperageMap();
            PanelPaintCircuit.repaint();
        });

        PanelPaintCircuit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int res_id = resistorAtPoint(e.getPoint());
                if (res_id > 0) {
                    selectResistor(res_id);
                }
            }
        });
        PanelPaintCircuit.addMouseListener(PanelPaintCircuitDoubleClickListener);


        paintBatteryPanel();
        resetSelectedResistor();


        SpinnerResistance.addChangeListener(e -> {
            if (Integer.parseInt(SpinnerResistance.getValue().toString()) < 0) SpinnerResistance.setValue(0);
            if (selected_resistor_id > 0)
                circuit.getEdgeById(selected_resistor_id).setWeight(Integer.parseInt(SpinnerResistance.getValue().toString()));
            recalculateAmperageMap();
            selectResistor(selected_resistor_id);
            PanelPaintCircuit.repaint();
        });

        SpinnerNewResistance.setValue(5);
        SpinnerVoltage.setValue(10);

        SpinnerVoltage.addChangeListener(e -> {
            if (Integer.parseInt(SpinnerVoltage.getValue().toString()) < 0) SpinnerVoltage.setValue(0);
            voltage = Integer.parseInt(SpinnerVoltage.getValue().toString());
            recalculateAmperageMap();
            if(selected_resistor_id > 0) selectResistor(selected_resistor_id);
        });

        SpinnerNewResistance.addChangeListener(e -> {
            if (Integer.parseInt(SpinnerNewResistance.getValue().toString()) < 0) SpinnerNewResistance.setValue(0);
        });

        ButtonFlipResistor.addActionListener(event -> {
            if (selected_resistor_id > 0) {
                ElectricCircuit.Resistor resistor = circuit.getEdgeById(selected_resistor_id);
                int v1 = resistor.getV1();
                int v2 = resistor.getV2();
                if (circuit.isDeadEnd(v1)) node_positions.remove(v1);
                if (circuit.isDeadEnd(v2)) node_positions.remove(v2);
                resistor.flip();
                recalculateAmperageMap();
                PanelPaintCircuit.repaint();
            }
        });
    }

    private void fillCircle(Graphics2D g2d, Point p, int radius) {
        g2d.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
    }

    private void drawClemmas(Graphics2D g2d) {

        if (pos_node == 0) {
            pos_node_position = new Point(CLEMM_INITIAL_OFFSET, CLEMM_INITIAL_OFFSET);
        } else if (pos_node > 0) {
            pos_node_position = node_positions.get(pos_node);
        }
        if (neg_node == 0) {
            neg_node_position = new Point(PanelPaintArea.getWidth() - CLEMM_INITIAL_OFFSET - 10, CLEMM_INITIAL_OFFSET);
        } else if (neg_node > 0) {
            neg_node_position = node_positions.get(neg_node);
        }

        g2d.setColor(Color.RED);
        drawWireBetweenPoints(g2d, new Point[]{new Point(CLEMM_INITIAL_OFFSET, 0), new Point(CLEMM_INITIAL_OFFSET, pos_node_position.y), pos_node_position}, false, false);
        fillCircle(g2d, pos_node_position, CLEMM_RADIUS);
        g2d.setColor(Color.BLUE);
        drawWireBetweenPoints(g2d, new Point[]{new Point(PanelPaintCircuit.getWidth() - CLEMM_INITIAL_OFFSET, 0), new Point(PanelPaintArea.getWidth() - CLEMM_INITIAL_OFFSET - 10, neg_node_position.y), neg_node_position}, false, false);
        fillCircle(g2d, neg_node_position, CLEMM_RADIUS);
        g2d.setColor(Color.WHITE);

        DrawUtils.drawStringInCenter(g2d, new Font("Arial", Font.PLAIN, CLEMM_RADIUS * 2 - 5), "+",
                new Rectangle(pos_node_position.x - CLEMM_RADIUS, pos_node_position.y - CLEMM_RADIUS, CLEMM_RADIUS * 2, CLEMM_RADIUS * 2));
        DrawUtils.drawStringInCenter(g2d, new Font("Arial", Font.PLAIN, CLEMM_RADIUS * 2 - 5), "-",
                new Rectangle(neg_node_position.x - CLEMM_RADIUS, neg_node_position.y - CLEMM_RADIUS, CLEMM_RADIUS * 2, CLEMM_RADIUS * 2));

        g2d.setColor(Color.BLACK);
        g2d.fillRect(CLEMM_INITIAL_OFFSET - WIRE_THICKNESS * 2, 0, WIRE_THICKNESS * 4, WIRE_THICKNESS * 2);
        g2d.fillRect(PanelPaintCircuit.getWidth() - CLEMM_INITIAL_OFFSET - WIRE_THICKNESS * 2, 0, WIRE_THICKNESS * 4, WIRE_THICKNESS * 2);
    }

    private void paintResistor(int res_id, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        Point p = resistor_positions.get(res_id);
        ElectricCircuit.Resistor r = circuit.getEdgeById(res_id);
        connectResistorToNodes(res_id, g2d);
        g2d.fillRect(p.x, p.y, RESISTOR_WIDTH, RESISTOR_HEIGHT);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(p.x + LINES_THICKNESS, p.y + LINES_THICKNESS, RESISTOR_WIDTH - 2 * LINES_THICKNESS, RESISTOR_HEIGHT - 2 * LINES_THICKNESS);
        g2d.setColor(Color.BLACK);
        Font font = new Font("Arial", Font.PLAIN, RESISTOR_HEIGHT / 2);
        DrawUtils.drawStringInCenter(g2d, font, r.getWeight() + "Ω", new Rectangle(p.x, p.y, RESISTOR_WIDTH, RESISTOR_HEIGHT));
    }

    private void connectResistorToNodes(int res_id, Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        ElectricCircuit.Resistor r = circuit.getEdgeById(res_id);
        Point pr = resistor_positions.get(res_id);
        if (!node_positions.containsKey(r.getV1())) {
            node_positions.put(r.getV1(), new Point(pr.x - 30, pr.y + RESISTOR_HEIGHT / 2));
            PanelPaintCircuit.repaint();
        }
        if (!node_positions.containsKey(r.getV2())) {
            node_positions.put(r.getV2(), new Point(pr.x + RESISTOR_WIDTH + 30, pr.y + RESISTOR_HEIGHT / 2));
            PanelPaintCircuit.repaint();
        }
        Point p1 = node_positions.get(r.getV1());
        Point p2 = node_positions.get(r.getV2());
        Point[] key_points_1;
        if (p1.x < pr.x) {
            key_points_1 = new Point[]{new Point(pr.x, pr.y + RESISTOR_HEIGHT / 2), new Point(p1.x, pr.y + RESISTOR_HEIGHT / 2), p1};
        } else {
            key_points_1 = new Point[]{new Point(pr.x, pr.y + RESISTOR_HEIGHT / 2), new Point(pr.x - 20, pr.y + RESISTOR_HEIGHT / 2),
                    new Point(pr.x - 20, p1.y < pr.y + RESISTOR_HEIGHT / 2 ? pr.y - 20 : pr.y + RESISTOR_HEIGHT + 20),
                    new Point(p1.x, p1.y < pr.y + RESISTOR_HEIGHT / 2 ? pr.y - 20 : pr.y + RESISTOR_HEIGHT + 20), p1};
        }
        boolean show_flow = switch_activated && amperage_map != null && !isShortCircuit() && amperage_map.get(res_id) != 0;
        drawWireBetweenPoints(g2d, key_points_1, show_flow, show_flow && (!(amperage_map.get(res_id) < 0)));
        Point[] key_points_2;
        if (p2.x > pr.x + RESISTOR_WIDTH) {
            key_points_2 = new Point[]{new Point(pr.x + RESISTOR_WIDTH, pr.y + RESISTOR_HEIGHT / 2), new Point(p2.x, pr.y + RESISTOR_HEIGHT / 2), p2};
        } else {
            key_points_2 = new Point[]{new Point(pr.x + RESISTOR_WIDTH, pr.y + RESISTOR_HEIGHT / 2), new Point(pr.x + 20 + RESISTOR_WIDTH, pr.y + RESISTOR_HEIGHT / 2),
                    new Point(pr.x + 20 + RESISTOR_WIDTH, p2.y < pr.y + RESISTOR_HEIGHT / 2 ? pr.y - 20 : pr.y + RESISTOR_HEIGHT + 20),
                    new Point(p2.x, p2.y < pr.y + RESISTOR_HEIGHT / 2 ? pr.y - 20 : pr.y + RESISTOR_HEIGHT + 20), p2};
        }
        drawWireBetweenPoints(g2d, key_points_2, show_flow, show_flow && amperage_map.get(res_id) < 0);
    }

    private void drawWireBetweenPoints(Graphics2D g2d, Point[] points, boolean show_flow, boolean reverse_direction) {
        for (int i = 0; i < points.length - 1; i++) {
            drawWireBetween(g2d, points[i], points[i + 1], show_flow, reverse_direction);
        }
    }

    private void drawWireBetween(Graphics2D g2d, Point p1, Point p2, boolean showFlow, boolean reverse_direction) {
        if (p1.y == p2.y) {
            g2d.fillRect(Math.min(p1.x, p2.x) - WIRE_THICKNESS / 2, p1.y - WIRE_THICKNESS / 2, Math.abs(p1.x - p2.x) + WIRE_THICKNESS, WIRE_THICKNESS);
            if (showFlow && Math.abs(p1.x - p2.x) > 20) {
                Direction direction = (p1.x < p2.x) ^ reverse_direction ? Direction.RIGHT : Direction.LEFT;
                drawFlowDirectionArrow(g2d, new Point((p1.x + p2.x) / 2, p1.y), direction);
            }
        }
        if (p1.x == p2.x) {
            g2d.fillRect(p1.x - WIRE_THICKNESS / 2, Math.min(p1.y, p2.y) - WIRE_THICKNESS / 2, WIRE_THICKNESS, Math.abs(p1.y - p2.y) + WIRE_THICKNESS);
            if (showFlow && Math.abs(p1.y - p2.y) > 20) {
                Direction direction = (p1.y < p2.y) ^ reverse_direction ? Direction.DOWN : Direction.UP;
                drawFlowDirectionArrow(g2d, new Point(p1.x, (p1.y + p2.y) / 2), direction);
            }
        }
    }

    private void drawFlowDirectionArrow(Graphics2D g2d, Point p, Direction direction) {
        Color old_color = g2d.getColor();
        g2d.setColor(Color.YELLOW);
        int dx = direction == Direction.LEFT ? 1 : direction == Direction.RIGHT ? -1 : 0;
        int dy = direction == Direction.UP ? 1 : direction == Direction.DOWN ? -1 : 0;
        for (int i = 0; i < 5; i++) {
            Point p0 = new Point(p.x + dx * i, p.y + dy * i);
            Point p1 = new Point(p0.x + (direction == Direction.LEFT ? 5 : direction == Direction.RIGHT ? -5 : WIRE_THICKNESS / 2),
                    p0.y + (direction == Direction.UP ? 5 : direction == Direction.DOWN ? -5 : WIRE_THICKNESS / 2));
            Point p2 = new Point(p0.x + (direction == Direction.LEFT ? 5 : direction == Direction.RIGHT ? -5 : -WIRE_THICKNESS / 2),
                    p0.y + (direction == Direction.UP ? 5 : direction == Direction.DOWN ? -5 : -WIRE_THICKNESS / 2));
            g2d.drawLine(p0.x, p0.y, p1.x, p1.y);
            g2d.drawLine(p0.x, p0.y, p2.x, p2.y);
        }
        g2d.setColor(old_color);
    }


    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }

    public void paintBatteryPanel() {
        PanelPaintBattery = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                SwingUtils.setFixedSize(PanelPaintBattery, PanelPaintBatteryArea.getWidth() - 10, PanelPaintBatteryArea.getHeight() - 10);
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, PanelPaintBatteryArea.getWidth(), PanelPaintBatteryArea.getHeight());
                if (mouse_on_switch) {
                    g2d.setColor(new Color(182, 225, 252));
                    g2d.fillRect(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2 - 10, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2 - 10, SWITCH_WIDTH + 20, SWITCH_HEIGHT + 20);
                }
                g2d.setColor(Color.RED);
                drawWireBetweenPoints(g2d, new Point[]{new Point(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2, PanelPaintBattery.getHeight() / 2),
                        new Point(CLEMM_INITIAL_OFFSET, PanelPaintBattery.getHeight() / 2), new Point(CLEMM_INITIAL_OFFSET, PanelPaintBatteryArea.getHeight())}, false, false);
                g2d.setColor(Color.BLUE);
                drawWireBetweenPoints(g2d, new Point[]{new Point(PanelPaintBattery.getWidth() / 2 + SWITCH_WIDTH / 2, PanelPaintBattery.getHeight() / 2),
                        new Point(PanelPaintBattery.getWidth() - CLEMM_INITIAL_OFFSET, PanelPaintBattery.getHeight() / 2), new Point(PanelPaintBattery.getWidth() - CLEMM_INITIAL_OFFSET, PanelPaintBatteryArea.getHeight())}, false, false);
                g2d.setColor(Color.GRAY);
                g2d.fillRect(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2, SWITCH_WIDTH, SWITCH_HEIGHT);
                g2d.setColor(switch_activated ? new Color(84, 255, 159) : new Color(0, 100, 0));
                g2d.fillRect(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2 + 10, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2 + 10, SWITCH_WIDTH / 2 - 10, SWITCH_HEIGHT - 20);
                g2d.setColor(!switch_activated ? Color.RED : new Color(92, 0, 0));
                g2d.fillRect(PanelPaintBattery.getWidth() / 2, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2 + 10, SWITCH_WIDTH / 2 - 10, SWITCH_HEIGHT - 20);
                g2d.setColor(Color.BLACK);
                g2d.fillRect(CLEMM_INITIAL_OFFSET - WIRE_THICKNESS * 2, PanelPaintBatteryArea.getHeight() - WIRE_THICKNESS * 2, WIRE_THICKNESS * 4, WIRE_THICKNESS * 2);
                g2d.fillRect(PanelPaintBattery.getWidth() - CLEMM_INITIAL_OFFSET - WIRE_THICKNESS * 2, PanelPaintBatteryArea.getHeight() - WIRE_THICKNESS * 2, WIRE_THICKNESS * 4, WIRE_THICKNESS * 2);
                fillCircle(g2d, new Point(PanelPaintBattery.getWidth() / 4, PanelPaintBattery.getHeight() / 2), AMPERMETER_RADIUS);
                g2d.setColor(Color.WHITE);
                fillCircle(g2d, new Point(PanelPaintBattery.getWidth() / 4, PanelPaintBattery.getHeight() / 2), AMPERMETER_RADIUS - 2);
                g2d.setColor(Color.BLACK);
                String amperage_s = amperage_map == null ? "0.00A" : isShortCircuit() ? "!" : (String.format("%.2f", !switch_activated ? 0d : amperage) + "A");
                DrawUtils.drawStringInCenter(g2d, new Font("Arial", Font.PLAIN, AMPERMETER_RADIUS / 2), amperage_s,
                        new Rectangle(PanelPaintBattery.getWidth() / 4 - AMPERMETER_RADIUS, PanelPaintBattery.getHeight() / 2 - AMPERMETER_RADIUS, AMPERMETER_RADIUS * 2, AMPERMETER_RADIUS * 2));
            }
        };
        PanelPaintBattery.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (new Rectangle(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2, SWITCH_WIDTH, SWITCH_HEIGHT).contains(e.getPoint())) {
                    switch_activated = !switch_activated;
                    PanelPaintBattery.repaint();
                    PanelPaintCircuit.repaint();
                }
            }
        });
        PanelPaintBattery.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouse_on_switch = new Rectangle(PanelPaintBattery.getWidth() / 2 - SWITCH_WIDTH / 2, PanelPaintBattery.getHeight() / 2 - SWITCH_HEIGHT / 2, SWITCH_WIDTH, SWITCH_HEIGHT).contains(e.getPoint());
                PanelPaintBattery.repaint();
            }
        });
        PanelPaintBatteryArea.add(new JScrollPane(PanelPaintBattery));
    }

    private int resistorAtPoint(Point p) {
        for (int res_id : resistor_positions.keySet()) {
            Point p1 = resistor_positions.get(res_id);
            if (new Rectangle(p1.x, p1.y, RESISTOR_WIDTH, RESISTOR_HEIGHT).contains(p)) return res_id;
        }
        return 0;
    }

    private int nodeAtPoint(Point p, int except) {
        for (int node_id : node_positions.keySet()) {
            if (node_id == except) continue;
            Point p1 = node_positions.get(node_id);
            if (new Rectangle(p1.x - PROXIMITY_RADIUS, p1.y - PROXIMITY_RADIUS, PROXIMITY_RADIUS * 2, PROXIMITY_RADIUS * 2).contains(p)) {
                return node_id;
            }
        }
        Point pc = pos_node_position;
        if (except != -1 && pos_node == 0 && new Rectangle(pc.x - CLEMM_RADIUS, pc.y - CLEMM_RADIUS, CLEMM_RADIUS * 2, CLEMM_RADIUS * 2).contains(p)) {
            return -1;
        }
        pc = neg_node_position;
        if (except != -2 && neg_node == 0 && new Rectangle(pc.x - CLEMM_RADIUS, pc.y - CLEMM_RADIUS, CLEMM_RADIUS * 2, CLEMM_RADIUS * 2).contains(p)) {
            return -2;
        }
        return 0;
    }

    private void removeNonPresentNodes() {
        for (int node : new HashSet<>(node_positions.keySet())) {
            if (circuit.adjacentEdges(node) == null) node_positions.remove(node);
        }
    }

    private void removeNonPresentResistors() {
        for (int res_id : new HashSet<>(resistor_positions.keySet())) {
            if (circuit.getEdgeById(res_id) == null) resistor_positions.remove(res_id);
        }
        removeNonPresentNodes();
    }

    private void delete_node(int node_id) {
        if (node_id > 0) {
            circuit.deleteNode(node_id);
            removeNonPresentNodes();
            recalculateAmperageMap();
            PanelPaintCircuit.repaint();
        }
    }

    private void delete_resistor(int res_id) {
        if (res_id > 0) {
            ElectricCircuit.Resistor r = circuit.getEdgeById(res_id);
            if (circuit.isDeadEnd(r.getV1()) && r.getV1() == pos_node || circuit.isDeadEnd(r.getV2()) && r.getV2() == pos_node)
                pos_node = 0;
            if (circuit.isDeadEnd(r.getV1()) && r.getV1() == neg_node || circuit.isDeadEnd(r.getV2()) && r.getV2() == neg_node)
                neg_node = 0;
            circuit.deleteEdgeById(res_id);
            if (circuit.isDeadEnd(r.getV1())) node_positions.remove(r.getV1());
            if (circuit.isDeadEnd(r.getV2())) node_positions.remove(r.getV2());
            removeNonPresentResistors();
            recalculateAmperageMap();
            PanelPaintCircuit.repaint();
        }
    }

    private void addCircuitComponentMotionListener(JComponent component) {
        ComponentMotionListener rectangleMotionListener = new ComponentMotionListener() {

            private int moving_resistor_id = 0;
            private int moving_node_id = 0;
            private int new_connect_node_id = 0;
            private boolean mousePressed = false;

            private int prime_node_x = 0;
            private int prime_node_y = 0;

            private MouseAdapter mouseAdapter = new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {

                    if (resistorAtPoint(e.getPoint()) != selected_resistor_id) resetSelectedResistor();

                    mousePressed = true;
                    moving_resistor_id = resistorAtPoint(e.getPoint());
                    if (moving_resistor_id == 0) {
                        moving_node_id = nodeAtPoint(e.getPoint(), 0);
                        highlighted_node = nodeAtPoint(e.getPoint(), 0);
                        if (moving_node_id > 0) {
                            prime_node_x = node_positions.get(moving_node_id).x;
                            prime_node_y = node_positions.get(moving_node_id).y;
                        } else if (moving_node_id == -1) {
                            prime_node_x = pos_node_position.x;
                            prime_node_y = pos_node_position.y;
                            pos_node = -1;
                        } else if (moving_node_id == -2) {
                            prime_node_x = neg_node_position.x;
                            prime_node_y = neg_node_position.y;
                            neg_node = -2;
                        }
                    } else {
                        highlighted_resistor_id = moving_resistor_id;
                    }
                    component.repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (moving_node_id > 0 && circuit.isDeadEnd(moving_node_id)) {
                        node_positions.get(moving_node_id).x = prime_node_x;
                        node_positions.get(moving_node_id).y = prime_node_y;
                    }
                    mousePressed = false;
                    moving_resistor_id = 0;
                    if (resistorAtPoint(e.getPoint()) != selected_resistor_id) highlighted_resistor_id = 0;
                    highlighted_node = 0;
                    if (new_connect_node_id > 0) {
                        if (moving_node_id == -1) {
                            pos_node = new_connect_node_id == neg_node ? 0 : new_connect_node_id;
                        } else if (moving_node_id == -2) {
                            neg_node = new_connect_node_id == pos_node ? 0 : new_connect_node_id;
                        } else {
                            ElectricCircuit.Resistor r = circuit.firstAdjacentEdge(moving_node_id);
                            if (r.getV1() == moving_node_id) {
                                r.setV1(new_connect_node_id);
                            } else {
                                r.setV2(new_connect_node_id);
                            }
                            if (moving_node_id == pos_node) {
                                pos_node = new_connect_node_id;
                            }
                            if (moving_node_id == neg_node) {
                                neg_node = new_connect_node_id;
                            }
                            removeNonPresentNodes();
                        }
                    }
                    moving_node_id = 0;
                    highlighted_node_2 = 0;
                    new_connect_node_id = 0;
                    if (pos_node < 0) pos_node = 0;
                    if (neg_node < 0) neg_node = 0;
                    recalculateAmperageMap();
                    component.repaint();
                }

            };

            private MouseMotionListener mouseMotionListener = new MouseMotionListener() {

                int oldX;
                int oldY;

                @Override
                public void mouseDragged(MouseEvent e) {

                    int dx = e.getX() - oldX;
                    int dy = e.getY() - oldY;
                    if (moving_resistor_id > 0) {
                        ElectricCircuit.Resistor resistor = circuit.getEdgeById(moving_resistor_id);
                        resistor_positions.get(moving_resistor_id).x += dx;
                        resistor_positions.get(moving_resistor_id).y += dy;
                        if (circuit.isDeadEnd(resistor.getV1())) {
                            node_positions.get(resistor.getV1()).x += dx;
                            node_positions.get(resistor.getV1()).y += dy;
                        }
                        if (circuit.isDeadEnd(resistor.getV2())) {
                            node_positions.get(resistor.getV2()).x += dx;
                            node_positions.get(resistor.getV2()).y += dy;
                        }
                    } else if (moving_node_id != 0) {
                        if (moving_node_id > 0) {
                            node_positions.get(moving_node_id).x += dx;
                            node_positions.get(moving_node_id).y += dy;
                        } else if (moving_node_id == -1) {
                            pos_node_position.x += dx;
                            pos_node_position.y += dy;
                        } else if (moving_node_id == -2) {
                            neg_node_position.x += dx;
                            neg_node_position.y += dy;
                        }
                        if (moving_node_id < 0 || circuit.isDeadEnd(moving_node_id)) {
                            int node_to_connect = nodeAtPoint(e.getPoint(), moving_node_id);
                            highlighted_node_2 = node_to_connect > 0 ? node_to_connect : 0;
                            new_connect_node_id = node_to_connect > 0 ? node_to_connect : 0;
                        }
                    }
                    component.repaint();
                    oldX = e.getX();
                    oldY = e.getY();
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!mousePressed) oldX = e.getX();
                    if (!mousePressed) oldY = e.getY();
                }
            };

            @Override
            public MouseAdapter mouseAdapter() {
                return mouseAdapter;
            }

            @Override
            public MouseMotionListener mouseMotionListener() {
                return mouseMotionListener;
            }
        };

        component.addMouseListener(rectangleMotionListener.mouseAdapter());
        component.addMouseMotionListener(rectangleMotionListener.mouseMotionListener());
    }

    private interface ComponentMotionListener {

        MouseAdapter mouseAdapter();

        MouseMotionListener mouseMotionListener();

    }

    private MouseAdapter PanelPaintCircuitDoubleClickListener = new MouseAdapter() {

        private Thread thread = null;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (thread == null || !thread.isAlive()) {
                thread = new Thread(() -> {
                    try {
                        Thread.sleep(200);
                    } catch (Exception exception) {

                    }
                });
                thread.start();
            } else {
                resetSelectedResistor();
                int res_id = resistorAtPoint(e.getPoint());
                if (res_id > 0) {
                    delete_resistor(res_id);
                } else {
                    int node = nodeAtPoint(e.getPoint(), 0);
                    if (node == pos_node) {
                        pos_node = 0;
                    } else if (node == neg_node) {
                        neg_node = 0;
                    } else {
                        delete_node(node);
                    }
                }
                recalculateAmperageMap();
                PanelPaintCircuit.repaint();
            }
        }
    };

    private void resetSelectedResistor() {
        selected_resistor_id = 0;
        highlighted_resistor_id = 0;
        ButtonFlipResistor.setEnabled(false);
        //SpinnerResistance.setValue(0);
        SpinnerResistance.setEnabled(false);
        LabelResistorAmperage.setText("-");
        LabelResistorVoltage.setText("-");
    }

    private void selectResistor(int id) {
        if(id == 0){
            resetSelectedResistor();
            return;
        }
        selected_resistor_id = id;
        highlighted_resistor_id = id;
        ButtonFlipResistor.setEnabled(true);
        SpinnerResistance.setEnabled(true);
        int old_resistance = Integer.parseInt(SpinnerResistance.getValue().toString());
        if(old_resistance != circuit.getEdgeById(id).getWeight());
        SpinnerResistance.setValue(circuit.getEdgeById(id).getWeight());
        double amperage = amperage_map == null || isShortCircuit() ? 0 : amperage_map.get(id);
        LabelResistorVoltage.setText(String.format("%.2f", amperage * circuit.getEdgeById(id).getWeight()) + "V");
        LabelResistorAmperage.setText(String.format("%.2f", amperage) + "A");
    }

    private void recalculateAmperageMap() {
        HashMap<Integer, Double> shortCircuitMap = new HashMap<>();
        shortCircuitMap.put(0, Double.POSITIVE_INFINITY);
        if (pos_node == 0 || neg_node == 0 || circuit.findAmperage(pos_node, neg_node, voltage) == null) {
            amperage_map = null;
            amperage = 0d;
        } else if (circuit.findAmperage(pos_node, neg_node, voltage).equals(shortCircuitMap)) {
            amperage_map = shortCircuitMap;
            amperage = Double.POSITIVE_INFINITY;
        } else {
            amperage_map = circuit.findAmperage(pos_node, neg_node, voltage);
            amperage = circuit.amperageSum(pos_node);
        }
    }

}
