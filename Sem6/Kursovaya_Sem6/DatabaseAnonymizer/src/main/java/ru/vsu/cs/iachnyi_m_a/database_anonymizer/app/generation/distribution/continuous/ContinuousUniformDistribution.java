package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.continuous;

import java.util.Random;

public class ContinuousUniformDistribution implements ContinuousDistribution {

    private final float min;
    private final float max;
    private final Random rand;

    public ContinuousUniformDistribution(float min, float max) {
        this.min = min;
        this.max = max;
        this.rand = new Random();
    }

    @Override
    public float next() {
        return rand.nextFloat() * (max - min) + min;
    }
}
