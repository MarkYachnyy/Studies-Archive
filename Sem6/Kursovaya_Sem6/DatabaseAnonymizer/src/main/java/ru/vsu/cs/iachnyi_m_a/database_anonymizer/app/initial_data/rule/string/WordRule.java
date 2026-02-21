package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.string;

import lombok.Data;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete.DiscreteDistributionFactory;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator.string.WordGenerator;
import ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.rule.DiscreteDistributionType;

import java.util.List;

@Data
public class WordRule {
    private List<String> allowedCharacters;
    private DiscreteDistributionType distributionType;
    private float[] distributionParams;

    public WordGenerator toWordGenerator() {
        return new WordGenerator(allowedCharacters, distributionType == null ? null : DiscreteDistributionFactory.createDiscreteDistribution(distributionType, distributionParams));
    }
}
