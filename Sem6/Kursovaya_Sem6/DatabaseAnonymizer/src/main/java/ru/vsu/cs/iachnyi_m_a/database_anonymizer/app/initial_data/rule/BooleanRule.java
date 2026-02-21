package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.BooleanGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;

@Data
public class BooleanRule implements Rule{
    private String tableName;
    private String columnName;
    private float trueChance;
    private float nullChance;

    @Override
    public ColumnGenerator toGenerator(boolean unique) {
        return new BooleanGenerator(columnName, nullChance, trueChance, unique);
    }
}
