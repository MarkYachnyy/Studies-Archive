package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint;

import lombok.Data;

@Data
public class Unique {
    private String tableName;
    private String columnName;
}
