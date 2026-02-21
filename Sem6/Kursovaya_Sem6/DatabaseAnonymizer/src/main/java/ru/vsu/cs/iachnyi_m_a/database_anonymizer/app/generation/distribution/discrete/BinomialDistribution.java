package ru.vsu.cs.iachnyi_m_a.database_anonymizer.app.generation.distribution.discrete;

import java.util.stream.IntStream;

public class BinomialDistribution implements DiscreteDistribution{

    private final int n;
    private final float p;

    public BinomialDistribution(int n, float p) {
        this.n = n;
        this.p = p;
    }

    @Override
    public int next() {
        return IntStream.range(0, n).map(i -> Math.random() < p ? 1 : 0).sum();
    }
}
