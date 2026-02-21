package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.date_interval;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.AllenAlgebraRelation;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class DateIntervalRelation {

    private String start;
    private String end;
    private AllenAlgebraRelation relation;

    public Long[] getBorders(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        long start = LocalDate.parse(this.start, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long end = LocalDate.parse(this.end, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        return switch (relation) {
            case M -> new Long[]{null, start, start, start};
            case MI -> new Long[]{end, end, end, null};
            case O -> new Long[]{null, start, start, end};
            case OI -> new Long[]{start, end, end, null};
            case D -> new Long[]{start, end, start, end};
            case DI -> new Long[]{null, start, end, null};
        };
    };
}
