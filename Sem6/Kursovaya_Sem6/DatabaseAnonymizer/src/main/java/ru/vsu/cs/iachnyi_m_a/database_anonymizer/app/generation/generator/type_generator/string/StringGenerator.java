package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.string;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistribution;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class StringGenerator extends ColumnGenerator {


    private final Set<Integer> alreadyGeneratedValues;
    private final String columnName;
    private final float nullChance;
    private final boolean unique;
    private final List<WordGenerator> wordGenerators;
    private final String separator;

    public StringGenerator(String columnName, float nullChance, String separator, List<WordGenerator> wordGenerators, boolean unique) {
        this.columnName = columnName;
        this.nullChance = nullChance;
        this.unique = unique;
        this.alreadyGeneratedValues = new HashSet<>();
        this.wordGenerators = wordGenerators;
        this.separator = separator;
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
        return new String[]{"'" + wordGenerators.stream().map(WordGenerator::generateWord).collect(Collectors.joining(separator)) + "'"};
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
