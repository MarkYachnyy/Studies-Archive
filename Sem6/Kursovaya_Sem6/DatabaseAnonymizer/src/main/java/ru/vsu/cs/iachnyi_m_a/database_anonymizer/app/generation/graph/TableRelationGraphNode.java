package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.DiscreteDistributionType;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableRelationGraphNode {
    private String tableName;
    private List<RelationMapElement> children;
    private List<RelationMapElement> parents;

    public TableRelationGraphNode(String tableName) {
        this.tableName = tableName;
        this.children = new ArrayList<>();
        this.parents = new ArrayList<>();
    }
}
