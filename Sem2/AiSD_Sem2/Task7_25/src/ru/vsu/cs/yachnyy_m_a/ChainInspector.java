package ru.vsu.cs.yachnyy_m_a;

import ru.vsu.cs.yachnyy_m_a.util.SwingUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ChainInspector {
    public static List<Integer> longestChain(Graph graph, boolean allowVertexRep, boolean allowEdgeRep) {
        int vCount = graph.vertexCount();
        LinkedList<Integer> max_path = new LinkedList<>();
        boolean isDigraph = graph instanceof Digraph;
        for (int i = 0; i < vCount; i++) {
            if (!allowEdgeRep) {
                longestChainNoEdgeRep(graph, i, new LinkedList<>(), max_path, allowVertexRep ? null : new boolean[vCount], allowVertexRep ? new boolean[vCount][vCount] : null, isDigraph);
            } else {
                longestChainAllowEdgeRep(graph, i, new ArrayList<>(), max_path, new int[vCount][vCount], isDigraph);
            }
        }
        return max_path;
    }

    private static void longestChainNoEdgeRep(Graph graph, int v, LinkedList<Integer> path, LinkedList<Integer> max_path, boolean[] vert_visits, boolean[][] edge_visits, boolean isDigraph) {
        if (vert_visits != null) vert_visits[v] = true;
        path.add(v);
        boolean is_deadlock = true;
        for (int adj : graph.adjacencies(v)) {
            if (vert_visits == null && !edge_visits[v][adj] || edge_visits == null && !vert_visits[adj]) {
                is_deadlock = false;
                if (edge_visits != null) {
                    edge_visits[v][adj] = true;
                    if (!isDigraph) edge_visits[adj][v] = true;
                }
                longestChainNoEdgeRep(graph, adj, path, max_path, vert_visits, edge_visits, isDigraph);
                if (edge_visits != null) {
                    edge_visits[v][adj] = false;
                    if (!isDigraph) edge_visits[adj][v] = false;
                }
            }
        }
        if (is_deadlock && path.size() > max_path.size()) {
            max_path.clear();
            max_path.addAll(path);
        }
        if (vert_visits != null) vert_visits[v] = false;
        path.removeLast();
    }

    private static void longestChainAllowEdgeRep(Graph graph, int v, ArrayList<Integer> path, LinkedList<Integer> max_path, int[][] edge_visits, boolean isDigraph) {
        try {
            path.add(v);
            boolean is_deadlock = true;
            for (int adj : graph.adjacencies(v)) {
                if (edge_visits[v][adj] == 0 || !createsLoop(path, adj)) {
                    is_deadlock = false;
                    edge_visits[v][adj]++;
                    longestChainAllowEdgeRep(graph, adj, path, max_path, edge_visits, isDigraph);
                    edge_visits[v][adj]--;
                }
            }
            if (is_deadlock && path.size() > max_path.size()) {
                max_path.clear();
                max_path.addAll(path);
            }
            path.remove(path.size() - 1);
        } catch (StackOverflowError error) {
            SwingUtils.showInfoMessageBox("Recursion depth exceeded");
            return;
        }

    }

    public static boolean createsLoop(ArrayList<Integer> path, int v) {
        List<Integer> occurrences = occurrencesAfterMid(path, v);
        if (occurrences.size() == 0) return false;
        outer:
        for (Integer occ : occurrences) {
            for (int i = 0; i < path.size() - occ; i++) {
                if (!Objects.equals(path.get(path.size() - 1 - i), path.get(occ - 1 - i))) continue outer;
            }
            return true;
        }
        return false;
    }

    public static List<Integer> occurrencesAfterMid(List<Integer> path, int v) {
        List<Integer> res = new LinkedList<>();
        for (int i = path.size() / 2 + path.size() % 2; i < path.size(); i++) {
            if (v == path.get(i)) res.add(i);
        }
        return res;
    }
}
