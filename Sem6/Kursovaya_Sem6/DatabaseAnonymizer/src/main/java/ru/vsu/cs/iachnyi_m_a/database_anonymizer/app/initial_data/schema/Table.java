package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema;

import lombok.Data;

import java.util.List;

@Data
public class Table {
    private String name;
    private List<Column> columns;
}
