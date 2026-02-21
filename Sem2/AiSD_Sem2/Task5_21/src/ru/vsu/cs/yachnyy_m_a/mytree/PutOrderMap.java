package ru.vsu.cs.yachnyy_m_a.mytree;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PutOrderMap<K extends Comparable<K>, V> implements Map<K, V> {

    private AVLTreeMap<Integer, Entry<K, V>> put_orders;
    private AVLTreeMap<K, Entry<Integer, V>> values;

    public PutOrderMap(){
        put_orders = new AVLTreeMap<>();
        values = new AVLTreeMap<>();
    }
    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        put_orders = new AVLTreeMap<>();
        values = new AVLTreeMap<>();
    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
