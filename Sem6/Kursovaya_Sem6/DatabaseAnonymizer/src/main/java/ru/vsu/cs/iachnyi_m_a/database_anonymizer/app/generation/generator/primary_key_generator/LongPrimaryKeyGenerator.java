package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator;

public class LongPrimaryKeyGenerator implements PrimaryKeyGenerator {

    private final String columnName;
    private long next = 0L;

    public LongPrimaryKeyGenerator(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String nextValue() {
        next++;
        return String.valueOf(next);
    }

    @Override
    public String getColumnName() {
        return columnName;
    }
}
