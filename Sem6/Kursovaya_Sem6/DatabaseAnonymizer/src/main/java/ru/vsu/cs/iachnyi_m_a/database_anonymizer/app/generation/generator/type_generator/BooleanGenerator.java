package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BooleanGenerator extends ColumnGenerator{

    private final String columnName;
    private final float nullChance;
    private final float trueChance;
    private final boolean unique;
    private final Set<Integer> alreadyGeneratedValues;


    public BooleanGenerator(String columnName, float nullChance, float trueChance, boolean unique) {
        this.columnName = columnName;
        this.nullChance = nullChance;
        this.trueChance = trueChance;
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
        return new String[]{String.valueOf(Math.random() < trueChance)};
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
