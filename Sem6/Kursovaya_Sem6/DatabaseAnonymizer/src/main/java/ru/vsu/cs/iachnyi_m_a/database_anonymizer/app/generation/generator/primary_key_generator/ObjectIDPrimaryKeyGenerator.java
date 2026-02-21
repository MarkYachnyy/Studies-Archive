package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteUniformDistribution;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.string.StringGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.string.WordGenerator;

import java.util.List;

public class ObjectIDPrimaryKeyGenerator implements PrimaryKeyGenerator {

    private final String columnName;
    private final StringGenerator stringGenerator;

    public ObjectIDPrimaryKeyGenerator(String columnName) {
        this.columnName = columnName;
        this.stringGenerator = new StringGenerator(null, 0, "", List.of(new WordGenerator(List.of("0123456789abcdef"), new DiscreteUniformDistribution(24, 25))), true);
    }

    @Override
    public String nextValue() {
        return stringGenerator.getNextValues()[0];
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
