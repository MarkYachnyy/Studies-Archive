package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.primary_key_generator;

import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.schema.ValueType;

public class PrimaryKeyGeneratorFactory {
    public static PrimaryKeyGenerator createColumnGenerator(String columnName, ValueType type){
        PrimaryKeyGenerator res = null;
        switch (type) {
            case BIGINT : {
                res = new LongPrimaryKeyGenerator(columnName);
                break;
            }
            case UUID : {
                res = new UUIDPrimaryKeyGenerator(columnName);
                break;
            }
            case OBJECT_ID: {
                res = new ObjectIDPrimaryKeyGenerator(columnName);
                break;
            }
        }
        return res;
    }
}
