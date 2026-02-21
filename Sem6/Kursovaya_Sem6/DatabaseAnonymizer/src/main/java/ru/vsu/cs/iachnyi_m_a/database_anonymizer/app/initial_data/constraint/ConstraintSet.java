package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.initial_data.constraint;

import lombok.Data;

import java.util.List;

@Data
public class ConstraintSet {
    private List<ForeignKey> foreignKeys;
    private List<PrimaryKey> primaryKeys;
    private List<Unique> uniques;
}
