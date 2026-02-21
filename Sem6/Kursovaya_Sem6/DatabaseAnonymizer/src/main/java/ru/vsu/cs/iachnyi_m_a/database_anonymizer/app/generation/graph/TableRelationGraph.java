package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableRelationGraph {

    public TableRelationGraph() {
        nodes = new ArrayList<>();
    }

    private List<TableRelationGraphNode> nodes;

    public TableRelationGraphNode findNodeByTableName(String tableName) {
        return nodes.stream().filter(n -> n.getTableName().equals(tableName)).findFirst().orElse(null);
    }
}
