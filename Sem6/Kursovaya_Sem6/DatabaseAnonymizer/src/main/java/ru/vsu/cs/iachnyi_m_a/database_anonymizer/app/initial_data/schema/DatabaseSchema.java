package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema;

import lombok.Data;

import java.util.List;

@Data
public class DatabaseSchema {
    private List<Table> tables;
}
