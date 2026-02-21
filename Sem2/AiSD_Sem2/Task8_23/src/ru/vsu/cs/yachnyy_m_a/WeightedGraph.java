package ru.vsu.cs.yachnyy_m_a;

import java.util.Collection;

public interface WeightedGraph {

    interface WeightedEdge{
        int getV1();
        int getV2();
        void setV1(int v1);
        void setV2(int v2);
        int getWeight();
        void setWeight(int weight);
        int getId();
        void flip();
    }

    int addEdge(int v1, int v2, int weight);
    void deleteEdgeById(int id);
    WeightedEdge getEdgeById(int id);

    int vertexCount();
    int edgeCount();

    Iterable<? extends WeightedEdge> adjacentEdges(int v);

    Collection<? extends WeightedEdge> edges();
}
