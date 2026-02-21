package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistributionFactory;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.IntegerGenerator;

@Data
public class IntegerRule implements Rule {
    private String tableName;
    private String columnName;
    private DiscreteDistributionType distributionType;
    private float[] params;
    private float nullChance;


    @Override
    public ColumnGenerator toGenerator(boolean unique) {
        return new IntegerGenerator(columnName, nullChance, DiscreteDistributionFactory.createDiscreteDistribution(distributionType, params), unique);
    }
}
