package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateIntervalGenerator extends ColumnGenerator {

    private String intervalName;
    private float nullChance;
    private long startMin;
    private long startMax;
    private long endMin;
    private long endMax;
    private boolean unique;
    private Random random;

    private Set<Integer> alreadyGeneratedValues;

    public DateIntervalGenerator(String intervalName, float nullChance, long startMin, long startMax, long endMin, long endMax, boolean unique) {
        this.intervalName = intervalName;
        this.nullChance = nullChance;
        this.startMin = startMin;
        this.startMax = startMax;
        if (startMin == startMax) this.startMax++;
        this.endMin = endMin;
        this.endMax = endMax;
        if (endMin == endMax) this.endMax++;
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
        return new String[]{intervalName + "_start", intervalName + "_end"};
    }

    @Override
    protected String[] generateValues() {
        long min = startMin == startMax ? startMin : random.nextLong(startMin, startMax);
        long max = endMin == endMax ? endMin : random.nextLong(endMin, endMax);
        return min < max ? new String[]{longToDateString(min), longToDateString(max)} : new String[]{longToDateString(max), longToDateString(min)};
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public Set<Integer> getAlreadyGeneratedValuesHashCodes() {
        return alreadyGeneratedValues;
    }

    private String longToDateString(long dateLong) {
        Date date = new Date(dateLong);
        return "'" + new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.ROOT
        ).format(date) + "'";
    }
}
