package ru.vsu.cs.yachnyy_m_a;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.*;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import ru.vsu.cs.yachnyy_m_a.util.GraphUtils;
import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FormMain extends JFrame {
    private JPanel PanelMain;
    private JTextField TextFieldInput;
    private JButton ButtonLoadListFromFile;
    private JButton ButtonFindMaxChain;
    private JLabel LabelResult;
    private JPanel PanelPaintContainer;
    private JComboBox ComboBoxAllowRep;
    private JComboBox ComboBoxOrientedEdges;
    private JButton ButtonPaintGraph;
    private JSpinner SpinnerVertexCount;
    private JButton ButtonRandomGraph;
    private JSpinner SpinnerEdgeChance;

    private SvgPanel PanelPaint;
    private Graph graph;

    private JFileChooser InputFileChooser;

    private static class SvgPanel extends JPanel {
        private String svg = null;
        private GraphicsNode svgGraphicsNode = null;

        public void paint(String svg) throws IOException {
            String xmlParser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory df = new SAXSVGDocumentFactory(xmlParser);
            SVGDocument doc = df.createSVGDocument(null, new StringReader(svg));
            UserAgent userAgent = new UserAgentAdapter();
            DocumentLoader loader = new DocumentLoader(userAgent);
            BridgeContext ctx = new BridgeContext(userAgent, loader);
            ctx.setDynamicState(BridgeContext.DYNAMIC);
            GVTBuilder builder = new GVTBuilder();
            svgGraphicsNode = builder.build(ctx, doc);

            this.svg = svg;
            repaint();
        }

        @Override
        public void paintComponent(Graphics gr) {
            super.paintComponent(gr);

            if (svgGraphicsNode == null) {
                return;
            }

            double scaleX = this.getWidth() / svgGraphicsNode.getPrimitiveBounds().getWidth();
            double scaleY = this.getHeight() / svgGraphicsNode.getPrimitiveBounds().getHeight();
            double scale = Math.min(scaleX, scaleY);
            AffineTransform transform = new AffineTransform(scale, 0, 0, scale, 0, 0);
            svgGraphicsNode.setTransform(transform);
            Graphics2D g2d = (Graphics2D) gr;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            svgGraphicsNode.paint(g2d);
        }


    }

    public FormMain() {
        setTitle("Максимальная цепь");
        setContentPane(PanelMain);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.pack();

        PanelPaintContainer.setLayout(new BorderLayout());
        PanelPaint = new SvgPanel();
        PanelPaintContainer.add(new JScrollPane(PanelPaint));

        ButtonFindMaxChain.addActionListener((event) -> {
            boolean allowVertexRep = ComboBoxAllowRep.getSelectedIndex() > 0;
            boolean allowEdgeRep = ComboBoxAllowRep.getSelectedIndex() == 2;
            List<Integer> chain_path = ChainInspector.longestChain(graph, allowVertexRep, allowEdgeRep);
            LabelResult.setText("(" + chain_path.stream().map(String::valueOf).collect(Collectors.joining(", ")) + ")");
            try {
                PanelPaint.paint(dotToSvg(GraphUtils.toDot(graph, chain_path)));
            } catch (Exception exc) {
                SwingUtils.showErrorMessageBox(exc);
            }
        });

        ButtonPaintGraph.addActionListener((event) -> {
            try {
                Graph graph = fromPairListString(TextFieldInput.getText(), ComboBoxOrientedEdges.getSelectedIndex() == 0);
                FormMain.this.graph = graph;
                PanelPaint.paint(dotToSvg(GraphUtils.toDot(graph, null)));
            } catch (Exception exc) {
                SwingUtils.showErrorMessageBox(exc);
            }
        });

        InputFileChooser = new JFileChooser();
        InputFileChooser.setCurrentDirectory(new File("."));
        InputFileChooser.addChoosableFileFilter(new FileNameExtensionFilter("text files", "txt"));

        ButtonLoadListFromFile.addActionListener(event -> {
            try {
                if (InputFileChooser.showOpenDialog(PanelMain) == JFileChooser.APPROVE_OPTION) {
                    File file = new File(InputFileChooser.getSelectedFile().getPath());
                    Scanner scanner = new Scanner(file);
                    String input = "";
                    while (scanner.hasNext()) {
                        input += scanner.nextLine();
                    }
                    TextFieldInput.setText(input);
                }
            } catch (Exception e) {
                SwingUtils.showErrorMessageBox(e);
            }
        });

        ButtonRandomGraph.addActionListener(event -> {
            graph = GraphUtils.randomGraph((Integer) SpinnerVertexCount.getValue(), ((Integer)SpinnerEdgeChance.getValue())/100f, ComboBoxOrientedEdges.getSelectedIndex() == 0);
            try {
                PanelPaint.paint(dotToSvg(GraphUtils.toDot(graph, null)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private Graph fromPairListString(String pairList, boolean oriented) {
        try {
            int vCount = 0;
            int eCount = 0;
            StringBuilder res = new StringBuilder();
            Pattern pattern = Pattern.compile("\\(\\s*(\\d+)\\s*,\\s*(\\d+)\\s*\\)");
            Matcher matcher = pattern.matcher(pairList);
            while (matcher.find()) {
                int v1 = Integer.parseInt(matcher.group(1));
                int v2 = Integer.parseInt(matcher.group(2));
                res.append(v1).append(" ").append(v2).append('\n');
                eCount++;
                if (Math.max(v1, v2) >= vCount) vCount = Math.max(v1, v2) + 1;
            }
            String graphStr = vCount + "\n" + eCount + "\n" + res;
            return GraphUtils.fromStr(graphStr, Class.forName("ru.vsu.cs.yachnyy_m_a." + (oriented ? "AdjMatrixDigraph" : "AdjMatrixGraph")));
        } catch (Exception e) {
            SwingUtils.showErrorMessageBox(e);
        }
        return null;
    }

    private static String dotToSvg(String dotSrc) throws IOException {
        MutableGraph g = new Parser().read(dotSrc);
        return Graphviz.fromGraph(g).render(Format.SVG).toString();
    }


}
