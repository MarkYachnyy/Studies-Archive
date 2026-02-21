package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.continuous.ContinuousDistribution;

import java.util.HashSet;
import java.util.Set;

public class FloatGenerator extends ColumnGenerator {

    private final String columnName;
    private final float nullChance;
    private final ContinuousDistribution distribution;
    private final boolean unique;
    private final Set<Integer> alreadyGeneratedValues;


    public FloatGenerator(String columnName, float nullChance, ContinuousDistribution distribution, boolean unique) {
        this.columnName = columnName;
        this.distribution = distribution;
        this.nullChance = nullChance;
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
    public boolean isUnique(){
        return unique;
    }

    @Override
    public Set<Integer> getAlreadyGeneratedValuesHashCodes() {
        return alreadyGeneratedValues;
    }
}
