package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.date_interval.DateIntervalRule;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.interval.float_interval.FloatIntervalRule;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.string.StringRule;

import java.util.List;

@Data
public class RuleSet {
    private List<IntegerRule> integerRules;
    private List<StringRule> stringRules;
    private List<FloatRule> floatRules;
    private List<DateRule> dateRules;
    private List<BooleanRule> booleanRules;
    private List<FloatIntervalRule> floatIntervalRules;
    private List<DateIntervalRule> dateIntervalRules;
}
