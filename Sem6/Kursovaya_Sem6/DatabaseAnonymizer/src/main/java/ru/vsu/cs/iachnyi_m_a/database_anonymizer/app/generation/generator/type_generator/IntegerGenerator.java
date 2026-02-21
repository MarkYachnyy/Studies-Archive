package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistribution;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntegerGenerator extends ColumnGenerator {

    private final String columnName;
    private final float nullChance;
    private final DiscreteDistribution distribution;
    private final boolean unique;
    private final Set<Integer> alreadyGeneratedValues;


    public IntegerGenerator(String columnName, float nullChance, DiscreteDistribution distribution, boolean unique) {
        this.columnName = columnName;
        this.nullChance = nullChance;
        this.distribution = distribution;
        this.unique = unique;
        this.alreadyGeneratedValues = new HashSet<>();
    }

    @Override
    public float getNullChance() {
        return nullChance;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{columnName};
    }

    @Override
    protected String[] generateValues() {
        return new String[]{String.valueOf(distribution.next())};
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public Set<Integer> getAlreadyGeneratedValuesHashCodes() {
        return alreadyGeneratedValues;
    }
}
