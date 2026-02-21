package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator;

public interface PrimaryKeyGenerator {
    String nextValue();
    String getColumnName();
}
