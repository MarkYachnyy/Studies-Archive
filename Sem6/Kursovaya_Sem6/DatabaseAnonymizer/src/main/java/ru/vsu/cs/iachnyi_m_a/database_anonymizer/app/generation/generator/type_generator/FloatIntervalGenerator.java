package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class FloatIntervalGenerator extends ColumnGenerator {

    private String intervalName;
    private float nullChance;
    private float startMin;
    private float startMax;
    private float endMin;
    private float endMax;
    private boolean unique;
    private Random random;

    private Set<Integer> alreadyGeneratedValues;

    public FloatIntervalGenerator(String intervalName, float nullChance, float startMin, float startMax, float endMin, float endMax, boolean unique) {
        this.intervalName = intervalName;
        this.nullChance = nullChance;
        this.startMin = startMin;
        this.startMax = startMax;
        this.endMin = endMin;
        this.endMax = endMax;
        this.unique = unique;
        this.alreadyGeneratedValues = new HashSet<>();
        random = new Random();
    }

    @Override
    public float getNullChance() {
        return nullChance;
    }

    @Override
    public String[] getColumnNames() {
        return new String[]{intervalName+"_start", intervalName+"_end"};
    }

    @Override
    protected String[] generateValues() {
        float min = startMin == startMax ? startMin : random.nextFloat(startMin, startMax);
        float max = endMin == endMax ? endMin : random.nextFloat(endMin, endMax);
        return min < max ? new String[]{String.valueOf(min), String.valueOf(max)} : new String[]{String.valueOf(max), String.valueOf(min)};
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
