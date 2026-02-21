package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint;

import lombok.Data;

@Data
public class ForeignKey {
    private String sourceTableName;
    private String sourceColumnName;

    private String targetTableName;
    private String targetColumnName;

    private String sourceDistributionType;
    private float[] sourceDistributionParams;
    private float sourceZeroChance;
    private float targetZeroChance;
}
