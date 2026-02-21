package ru.vsu.cs.yachnyy_m_a.logic.factories;

import java.util.Comparator;
import java.util.PriorityQueue;

@FunctionalInterface
public interface PriorityQueueFactory<T> {
    public PriorityQueue<T> create(Comparator<T> comparator);

}
