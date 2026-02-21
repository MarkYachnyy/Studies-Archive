package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.continuous.ContinuousDistributionFactory;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.FloatGenerator;

@Data
public class FloatRule implements Rule {
    private String tableName;
    private String columnName;

    private ContinuousDistributionType distributionType;
    private float[] params;
    private float nullChance;

    @Override
    public ColumnGenerator toGenerator(boolean unique) {
        return new FloatGenerator(
                columnName,
                nullChance,
                ContinuousDistributionFactory.createContinuousDistribution(distributionType, params), unique);
    }
}
