package ru.vsu.cs.yachnyy_m_a.mytree.map;

import ru.vsu.cs.yachnyy_m_a.mytree.BinarySearchTree;

import java.util.Map;

public interface BinarySearchTreeMap<K extends Comparable<K>, V> extends Map<K, V> {

    V get(Object key);

    V put(K key, V value);

    int size();

    boolean isEmpty();

    boolean containsKey(Object key);

    boolean containsValue(Object value);

    void clear();

    V remove(Object key);

    V getOrDefault(Object key, V defaultValue);

    BinarySearchTree<? extends Entry<K, V>> getTree();
}
