package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;

public interface Rule {
    ColumnGenerator toGenerator(boolean unique);
    String getTableName();
    String getColumnName();
    float getNullChance();
}
