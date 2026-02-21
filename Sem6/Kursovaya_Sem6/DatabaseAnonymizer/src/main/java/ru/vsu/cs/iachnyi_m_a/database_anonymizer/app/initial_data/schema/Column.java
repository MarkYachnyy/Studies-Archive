package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema;

import lombok.Data;

@Data
public class Column {
    String name;
    ValueType type;
}
