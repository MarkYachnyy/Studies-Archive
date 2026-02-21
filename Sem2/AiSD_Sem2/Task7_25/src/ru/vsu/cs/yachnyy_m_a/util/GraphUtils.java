package ru.vsu.cs.yachnyy_m_a.util;

import com.kitfox.svg.A;
import guru.nidi.graphviz.model.Link;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import ru.vsu.cs.yachnyy_m_a.AdjMatrixDigraph;
import ru.vsu.cs.yachnyy_m_a.AdjMatrixGraph;
import ru.vsu.cs.yachnyy_m_a.Digraph;
import ru.vsu.cs.yachnyy_m_a.Graph;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Утилиты работы с графами
 */
public class GraphUtils {

    public static Graph fromStr(String str, Class clz) throws IOException, InstantiationException, IllegalAccessException {
        Graph graph = (Graph) clz.newInstance();
        Map<String, Integer> names = new HashMap<>();
        int vertexCount = 0;
        if (Pattern.compile("^\\s*(strict\\s+)?(graph|digraph)\\s*\\{").matcher(str).find()) {
            // dot-формат
            MutableGraph g = new Parser().read(str);
            vertexCount = g.nodes().size();
            graph.addAdge(vertexCount - 1, vertexCount - 1);
            graph.removeAdge(vertexCount - 1, vertexCount - 1);

            // проверка, являются ли все вершины целыми (-2 - не являются)
            Pattern intPattern = Pattern.compile("^\\d+$");
            int maxVertex = -1;
            for (Link l : g.links()) {
                String fromStr = l.from().toString();
                if (intPattern.matcher(fromStr).matches()) {
                    maxVertex = Math.max(maxVertex, Integer.parseInt(fromStr));
                } else {
                    maxVertex = -2;
                    break;
                }
                String toStr = l.from().toString();
                if (intPattern.matcher(toStr).matches()) {
                    maxVertex = Math.max(maxVertex, Integer.parseInt(toStr));
                } else {
                    maxVertex = -2;
                    break;
                }
            }
            vertexCount = 0;
            for (Link l : g.links()) {
                String fromStr = l.from().toString();
                Integer from = null;
                if (maxVertex == -2) {
                    from = names.get(fromStr);
                    if (from == null) {
                        from = vertexCount;
                        names.put(fromStr, from);
                        vertexCount++;
                    }
                } else {
                    from = Integer.parseInt(fromStr);
                }
                String toStr = l.to().toString();
                Integer to = null;
                if (maxVertex == -2) {
                    to = names.get(toStr);
                    if (to == null) {
                        to = vertexCount;
                        names.put(toStr, to);
                        vertexCount++;
                    }
                } else {
                    to = Integer.parseInt(toStr);
                }
                graph.addAdge(from, to);
            }
        } else if (Pattern.compile("^\\s*\\d+").matcher(str).find()) {
            Scanner scanner = new Scanner(str);
            vertexCount = scanner.nextInt();
            int edgeCount = scanner.nextInt();
            for (int i = 0; i < edgeCount; i++) {
                graph.addAdge(scanner.nextInt(), scanner.nextInt());
            }
        } else {
            Scanner scanner = new Scanner(str);
            vertexCount = scanner.nextInt();
            while (scanner.hasNext()) {
                String fromStr = scanner.next();
                Integer from = names.get(fromStr);
                if (from == null) {
                    from = vertexCount;
                    names.put(fromStr, from);
                    vertexCount++;
                }
                String toStr = scanner.next();
                Integer to = names.get(toStr);
                if (to == null) {
                    to = vertexCount;
                    names.put(toStr, to);
                    vertexCount++;
                }
                graph.addAdge(from, to);
            }
        }

        return graph;
    }


    /**
     * Получение dot-описяния графа (для GraphViz)
     *
     * @return
     */
    public static String toDot(Graph graph, List<Integer> path) {
        StringBuilder sb = new StringBuilder();
        String nl = System.getProperty("line.separator");
        List<String> pairList = toPairList(path);
        boolean isDigraph = graph instanceof Digraph;
        sb.append(isDigraph ? "digraph" : "strict graph").append(" {").append(nl);
        for (int v1 = 0; v1 < graph.vertexCount(); v1++) {
            if (path != null && path.contains(v1)) sb.append(v1).append("[color = \"red\"]");
        }
        for (int v1 = 0; v1 < graph.vertexCount(); v1++) {
            int count = 0;
            for (Integer v2 : graph.adjacencies(v1)) {
                List<Integer> list = allOccurrences(pairList, String.format("(%s,%s)", v1, v2));
                if (!isDigraph) list.addAll(allOccurrences(pairList, String.format("(%s,%s)", v2, v1)));
                String label = list.stream().map(String::valueOf).collect(Collectors.joining(", "));
                sb.append(String.format("  %d %s %d", v1, (isDigraph ? "->" : "--"), v2)).append(nl).append(String.format(" [label=\"%s\"]", label)).append(list.size() > 0 ? "[color=\"red\"]" : "").append(";");
                count++;
            }
            if (count == 0) {
                sb.append(v1).append(nl);
            }
        }
        sb.append("}").append(nl);

        return sb.toString();
    }

    private static List<String> toPairList(List<Integer> path) {
        ArrayList<String> res = new ArrayList<String>();
        if (path == null) return res;
        for (int i = 0; i < path.size() - 1; i++) {
            res.add(String.format("(%s,%s)", path.get(i), path.get(i + 1)));
        }
        return res;
    }

    private static <T> List<Integer> allOccurrences(@Nonnull List<T> list, T item) {
        ArrayList<Integer> res = new ArrayList<>();
        int i = 0;
        for (T t : list) {
            if (t.equals(item)) res.add(i);
            i++;
        }
        return res;
    }

    public static Graph randomGraph(int vCount, float edgeChance, boolean isDigraph) {
        Graph res = isDigraph ? new AdjMatrixDigraph(vCount) : new AdjMatrixGraph(vCount);
        for (int i = 0; i < vCount; i++) {
            for (int j = isDigraph ? 0 : i + 1; j < vCount; j++) {
                if (i != j && Math.random() < edgeChance) res.addAdge(i, j);
            }
        }
        return res;
    }
}
