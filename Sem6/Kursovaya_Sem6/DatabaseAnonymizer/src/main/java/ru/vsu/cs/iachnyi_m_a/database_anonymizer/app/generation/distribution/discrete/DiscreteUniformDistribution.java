package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete;

import java.util.Random;

public class DiscreteUniformDistribution  implements DiscreteDistribution{

    private final int min;
    private final int max;
    private final Random random;

    public DiscreteUniformDistribution(int min, int max) {
        this.min = min;
        this.max = max;
        random = new Random();
    }

    @Override
    public int next() {
        return random.nextInt(max - min) + min;
    }
}
