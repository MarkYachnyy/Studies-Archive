package ru.vsu.cs.yachnyy_m_a;

import java.util.*;
import java.util.stream.Collectors;

public class ElectricCircuit implements WeightedGraph {

    public HashMap<Integer, List<Integer>> adjacency_map;
    public HashMap<Integer, Resistor> resistors;
    private int last_resistor_id;
    private int last_node_id;

    private HashMap<Integer, Double> amperage_map = null;

    public class Resistor implements WeightedGraph.WeightedEdge {

        private int id;
        private int node1;
        private int node2;
        private int resistance;

        private Resistor(int id, int node1, int node2, int resistance) {
            this.id = id;
            this.node1 = node1;
            this.node2 = node2;
            this.resistance = resistance;
        }

        public int getV1() {
            return node1;
        }

        public int getV2() {
            return node2;
        }

        public int getWeight() {
            return resistance;
        }

        public int getId() {
            return id;
        }

        @Override
        public void flip() {
            int tmp = this.node1;
            this.node1 = this.node2;
            this.node2 = tmp;
        }

        public boolean isFree(int pos_node, int neg_node) {
            if(node1 == node2) return true;
            Set<Integer> forbidden_edges = new HashSet<>();
            forbidden_edges.add(this.id);
            List<List<Integer>> p1p = allPaths(node1, pos_node, forbidden_edges, true);
            List<List<Integer>> p2n = allPaths(node2, neg_node, forbidden_edges, true);
            List<List<Integer>> p2p = allPaths(node2, pos_node, forbidden_edges, true);
            List<List<Integer>> p1n = allPaths(node1, neg_node, forbidden_edges, true);
            if(p1p.size() > 0 && p2n.size() == 0 ||
                    p1n.size() > 0 && p2p.size() == 0 ||
                    p1p.size() == 0 && p2n.size() > 0 ||
                    p1n.size() == 0 && p2p.size() > 0) return true;
            return !(containIndependentPaths(p2p, p1n) || containIndependentPaths(p2n, p1p));
        }

        @Override
        public int hashCode() {
            return id;
        }

        @Override
        public boolean equals(Object obj) {
            return this.id == ((Resistor) obj).id;
        }

        public void setV1(int node1) {
            if (adjacency_map.get(node1) == null) adjacency_map.put(node1, new ArrayList<>());
            adjacency_map.get(this.node1).remove(new Integer(id));
            if(adjacency_map.get(this.node1).size() == 0) adjacency_map.remove(this.node1);
            this.node1 = node1;
            adjacency_map.get(node1).add(id);
        }

        public void setV2(int node2) {
            if (adjacency_map.get(node2) == null) adjacency_map.put(node2, new ArrayList<>());
            adjacency_map.get(this.node2).remove(new Integer(id));
            if(adjacency_map.get(this.node2).size() == 0) adjacency_map.remove(this.node2);
            this.node2 = node2;
            adjacency_map.get(node2).add(id);
        }

        public void setWeight(int resistance) {
            this.resistance = resistance;
        }

        @Override
        public String toString() {
            return "Between " + node1 + " and " + node2;
        }
    }

    public ElectricCircuit() {
        this.adjacency_map = new HashMap<>();
        this.resistors = new HashMap<>();
        this.last_resistor_id = 0;
        this.last_node_id = 0;
    }

    @Override
    public int edgeCount() {
        return resistors.size();
    }

    public int vertexCount(){
        return adjacency_map.size();
    }

    public boolean isDeadEnd(int node){
        if(!adjacency_map.containsKey(node)) return false;
        return adjacency_map.get(node).size() <= 1;
    }

    @Override
    public void deleteEdgeById(int id) {
        Resistor to_del = resistors.remove(id);
        if (to_del == null) return;
        for (Integer node : new HashSet<>(adjacency_map.keySet())) {
            adjacency_map.get(node).remove(new Integer(to_del.id));
            if(adjacency_map.get(node).size() == 0) adjacency_map.remove(node);
        }
    }

    @Override
    public Resistor getEdgeById(int id) {
        return resistors.get(id);
    }

    public Resistor firstAdjacentEdge(int v){
        return adjacency_map.containsKey(v) ? resistors.get(adjacency_map.get(v).get(0)) : null;
    }

    @Override
    public Iterable<Resistor> adjacentEdges(int v) {
        if (adjacency_map.get(v) == null) return null;
        return adjacency_map.get(v).stream().map(i -> resistors.get(i)).collect(Collectors.toList());
    }

    public void deleteNode(int node) {
        for (Resistor resistor : this.resistors.values()) {
            if (resistor.node1 == node) resistor.setV1(++last_node_id);

            if (resistor.node2 == node) resistor.setV2(++last_node_id);
        }
        adjacency_map.remove(node);
    }

    public int addResistor(int resistance) {
        return addEdge(++last_node_id, ++last_node_id, resistance);
    }

    public HashMap<Integer, Double> findAmperage(int pos_node, int neg_node, int voltage) {
        Pair<ElectricCircuit, HashMap<Integer, Integer>> simplify_data = simplified(pos_node, neg_node);
        ElectricCircuit simplified_circuit = simplify_data.first;
        HashMap<Integer, Integer> match = simplify_data.second;

        if(simplified_circuit.edgeCount() == 0) {
            this.amperage_map = null;
            return null;
        }
        double[][] equations = new double[simplified_circuit.resistors.size()][simplified_circuit.resistors.size() + 1];
        List<List<Integer>> flow_paths = simplified_circuit.allOrientedFlowPaths(pos_node, neg_node);
        for (int i = 0; i < flow_paths.size(); i++) {
            for(int or_res_id: flow_paths.get(i)){
                int sign = or_res_id / Math.abs(or_res_id);
                int res_id = Math.abs(or_res_id);
                equations[i][res_id - 1] = simplified_circuit.getEdgeById(Math.abs(res_id)).resistance * sign;
            }
            equations[i][equations.length] = voltage;
        }
        boolean defined = false;
        HashMap<Integer, Double> res = new HashMap<>();
        outer:
        for (int i = flow_paths.size(); !defined; i--) {
            int j = i;
            if(j < equations.length )Arrays.fill(equations[j], 0);
            for(int node: simplified_circuit.adjacency_map.keySet()){
                if(j == equations.length) break;
                if(node != pos_node && node != neg_node){
                    for(int resId: simplified_circuit.adjacency_map.get(node)){
                        Resistor resistor = simplified_circuit.getEdgeById(resId);
                        equations[j][resId - 1] = resistor.node2 == node ? 1 : -1;
                    }
                    j++;
                }
            }
            String[] string_answers = GaussMethod.solve(equations);
            if(string_answers == null) {
                HashMap<Integer, Double> res1 = new HashMap<>();
                res1.put(0, Double.POSITIVE_INFINITY);
                this.amperage_map = res1;
                return res1;
            };
            for(Integer resId: this.resistors.keySet()){
                try {
                    res.put(resId, !match.containsKey(resId) ? 0 : Double.parseDouble(string_answers[match.get(resId) - 1]));
                } catch (NumberFormatException e){
                    continue outer;
                }
            }
            defined = true;
        }

        this.amperage_map = res;
        return res;
    }

    @Override
    public Collection<? extends WeightedEdge> edges() {
        return this.resistors.values();
    }

    public HashMap<Integer, Integer> reduceResistorIds() {
        HashMap<Integer, Integer> res = new HashMap<>();
        int i = 1;
        for (Resistor resistor : this.resistors.values()) {
            res.put(resistor.id, i);
            resistor.id = i;
            i++;
        }
        return res;
    }

    @Override
    public int addEdge(int node1, int node2, int resistance) {
        int id = ++last_resistor_id;
        Resistor resistor = new Resistor(id, node1, node2, resistance);
        if (adjacency_map.get(node1) == null) adjacency_map.put(node1, new ArrayList<>());
        adjacency_map.get(node1).add(id);

        if (adjacency_map.get(node2) == null) adjacency_map.put(node2, new ArrayList<>());
        adjacency_map.get(node2).add(id);
        resistors.put(id, resistor);
        return id;
    }

    public Pair<ElectricCircuit, HashMap<Integer, Integer>> simplified(int pos_node, int neg_node) {
        ElectricCircuit simpleCircuit = new ElectricCircuit();
        HashMap<Integer, Integer> match = new HashMap<>();
        for (Resistor resistor : this.resistors.values()) {
            if (!resistor.isFree(pos_node, neg_node)) {
                int new_id = simpleCircuit.addEdge(resistor.getV1(), resistor.getV2(), resistor.getWeight());
                match.put(resistor.id, new_id);
            }
        }
        HashMap<Integer, Integer> simplification_match = simpleCircuit.reduceResistorIds();
        match.replaceAll((k, v) -> simplification_match.get(match.get(k)));
        //System.out.println(simpleCircuit.resistors.size());
        return new Pair<>(simpleCircuit, match);
    }

    public boolean containIndependentPaths(List<List<Integer>> lists1, List<List<Integer>> lists2){
        if(lists1 == null || lists2 == null) return false;
        for (int i = 0; i < lists1.size(); i++) {
            for (int j = i; j < lists2.size(); j++) {
                ArrayList<Integer> tmp = new ArrayList<>(lists1.get(i));
                tmp.retainAll(lists2.get(j));
                if(tmp.size() == 0) return true;
            }
        }
        return false;
    }

    public List<List<Integer>> allPaths(int from, int to, Set<Integer> forbidden_edges, boolean buildByNodes) {
        List<List<Integer>> res = new ArrayList<>();
        allPaths(from, to, new LinkedList<>(), res, new HashSet<>(), false, forbidden_edges, buildByNodes);
        return res;
    }

    private List<List<Integer>> allOrientedFlowPaths(int from, int to){
        List<List<Integer>> res = new ArrayList<>();
        allPaths(from, to, new LinkedList<>(), res, new HashSet<>(), true, new HashSet<>(), false);
        return res;
    }

    private void allPaths(int from, int to, LinkedList<Integer> currPath, List<List<Integer>> output, Set<Integer> nodeVisits, boolean oriented, Set<Integer> forbidden_edges, boolean buildByNodes) {
        if (buildByNodes) currPath.add(from);
        if (from == to) {
            output.add(new ArrayList<>(currPath));
            if (buildByNodes) currPath.removeLast();
            return;
        }
        nodeVisits.add(from);
        for (int resId : adjacency_map.get(from)) {
            if (!forbidden_edges.contains(resId)) {
                Resistor resistor = resistors.get(resId);
                int adjNode = resistor.node1 + resistor.node2 - from;
                if (!nodeVisits.contains(adjNode)) {
                    int orEdgeId = !oriented ? resId : (adjNode == resistor.node2 ? resId : -resId);
                    if (!buildByNodes) currPath.add(orEdgeId);
                    allPaths(adjNode, to, currPath, output, nodeVisits, oriented, forbidden_edges, buildByNodes);
                    if (!buildByNodes) currPath.removeLast();
                }
            }
        }
        nodeVisits.remove(from);
        if (buildByNodes) currPath.removeLast();
    }

    public double amperageSum(int node){
        if(amperage_map == null) return 0;
        double res = 0;
        for(Resistor resistor: adjacentEdges(node)){
            res += resistor.getV1() == node ? amperage_map.get(resistor.id) : - amperage_map.get(resistor.id);
        }
        return Math.abs(res);
    }
}

