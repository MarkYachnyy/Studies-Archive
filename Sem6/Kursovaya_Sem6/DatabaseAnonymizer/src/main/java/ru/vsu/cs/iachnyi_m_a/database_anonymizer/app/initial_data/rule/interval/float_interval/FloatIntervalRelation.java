package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.float_interval;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.AllenAlgebraRelation;

@Data
@AllArgsConstructor
public class FloatIntervalRelation {
    private float start;
    private float end;
    private AllenAlgebraRelation relation;

    public Float[] getBorders(){
        return switch (relation) {
            case M -> new Float[]{null, start, start, start};
            case MI -> new Float[]{end, end, end, null};
            case O -> new Float[]{null, start, start, end};
            case OI -> new Float[]{start, end, end, null};
            case D -> new Float[]{start, end, start, end};
            case DI -> new Float[]{null, start, end, null};
        };
    }
}
