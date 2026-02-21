package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.float_interval;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.ColumnGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.FloatIntervalGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.Rule;

import java.util.List;

@Data
public class FloatIntervalRule implements Rule {

    private String tableName;
    private String columnName;

    private float nullChance;
    private float unique;
    private List<FloatIntervalRelation> relations;

    @Override
    public ColumnGenerator toGenerator(boolean unique) {
        Float[] bounds = new Float[4];
        for(FloatIntervalRelation relation : relations) {
            Float[] new_bounds = relation.getBorders();
            if(bounds[0] == null) {
                bounds[0] = new_bounds[0];
            } else if(new_bounds[0] != null){
                bounds[0] = Math.max(bounds[0], new_bounds[0]);
            }
            if(bounds[1] == null) {
                bounds[1] = new_bounds[1];
            } else if(new_bounds[1] != null){
                bounds[1] = Math.min(bounds[1], new_bounds[1]);
            }
            if(bounds[2] == null) {
                bounds[2] = new_bounds[2];
            } else if(new_bounds[2] != null){
                bounds[2] = Math.max(bounds[2], new_bounds[2]);
            }
            if(bounds[3] == null) {
                bounds[3] = new_bounds[3];
            } else if(new_bounds[3] != null){
                bounds[3] = Math.min(bounds[3], new_bounds[3]);
            }
        }
        return new FloatIntervalGenerator(columnName, nullChance, bounds[0], bounds[1], bounds[2], bounds[3], unique);
    }
}
