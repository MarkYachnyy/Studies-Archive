package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.generator.type_generator;

import java.util.Arrays;
import java.util.Set;

public abstract class ColumnGenerator {
    public abstract float getNullChance();
    public abstract String[] getColumnNames();
    public String[] getNextValues(){
        if(Math.random() < getNullChance()){
            return new String[getColumnNames().length];
        }
        if(!isUnique()){
            return generateValues();
        } else {
            boolean uniques;
            String[] res;
            do {
                res = generateValues();
                uniques = !getAlreadyGeneratedValuesHashCodes().contains(Arrays.hashCode(res));
            } while (!uniques);
            getAlreadyGeneratedValuesHashCodes().add(Arrays.hashCode(res));
            return res;
        }
    };
    protected abstract String[] generateValues();
    public abstract boolean isUnique();
    public abstract Set<Integer> getAlreadyGeneratedValuesHashCodes();
}
