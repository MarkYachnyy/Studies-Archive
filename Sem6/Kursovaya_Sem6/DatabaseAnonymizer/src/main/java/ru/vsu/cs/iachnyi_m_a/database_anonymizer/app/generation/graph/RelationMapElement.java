package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistribution;

@Data
@AllArgsConstructor
public class RelationMapElement {
    private String foreignKeyName;
    private TableRelationGraphNode node;
    private DiscreteDistribution distribution;
    private float sourceZeroChance;
    private float targetZeroChance;
}
