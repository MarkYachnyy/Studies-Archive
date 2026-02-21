package ru.vsu.cs.yachnyy_m_a.logic.factories;

import com.sun.xml.internal.messaging.saaj.packaging.mime.util.QEncoderStream;

import java.util.List;
import java.util.Queue;

@FunctionalInterface
public interface QueueFactory<T> {
    Queue<T> create();

    default Queue<T> create(List<T> items){
        Queue<T> res = this.create();
        res.addAll(items);
        return res;
    }
}
