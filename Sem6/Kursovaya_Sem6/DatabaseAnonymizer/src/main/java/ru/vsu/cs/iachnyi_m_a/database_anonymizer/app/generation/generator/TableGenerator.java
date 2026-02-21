package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator.PrimaryKeyGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;

import java.util.*;

@Data
public class TableGenerator {
    private String tableName;
    private PrimaryKeyGenerator primaryKeyGenerator;
    private List<ColumnGenerator> columnGenerators;

    public TableGenerator(String tableName) {
        this.tableName = tableName;
        this.columnGenerators = new ArrayList<>();
    }
}
