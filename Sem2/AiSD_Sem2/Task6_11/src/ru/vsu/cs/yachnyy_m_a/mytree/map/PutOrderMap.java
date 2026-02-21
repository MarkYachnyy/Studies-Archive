package ru.vsu.cs.yachnyy_m_a.mytree.map;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class PutOrderMap<K extends Comparable<K>, V> implements Map<K, V> {

    private AVLTreeMap<Integer, Entry<K, V>> put_orders;
    private AVLTreeMap<K, Entry<Integer, V>> values;
    private int items_put;

    private static class SimpleEntry<K1, V1> implements Entry<K1, V1> {

        private K1 key;
        private V1 value;

        @Override
        public K1 getKey() {
            return key;
        }

        @Override
        public V1 getValue() {
            return value;
        }

        @Override
        public V1 setValue(V1 value) {
            V1 old_val = this.value;
            this.value = value;
            return old_val;
        }

        public SimpleEntry(K1 key, V1 value) {
            this.key = key;
            this.value = value;
        }
    }

    public PutOrderMap(){
        put_orders = new AVLTreeMap<>();
        values = new AVLTreeMap<>();
        items_put = 0;
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
        for (Map.Entry<K, Entry<Integer, V>> entry: values){
            if (entry.getValue().getValue().equals(value)) return true;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        return values.get(key).getValue();
    }

    @Override
    public V put(K key, V value) {
        Entry<Integer, V> old_entry = values.get(key);
        int put_order = old_entry == null ? ++items_put : old_entry.getKey();
        values.put(key, new SimpleEntry<>(put_order, value));
        put_orders.put(put_order, new SimpleEntry<>(key, value));

        //Entry<Integer, V> old_entry = values.put(new SimpleEntry<>(++items_put, values));
        //put_orders.remove(old_entry.getKey());
        //put_orders.put(items_put, new SimpleEntry<>(key, value));

        return old_entry != null ? old_entry.getValue() : null;
    }

    @Override
    public V remove(Object key) {
        Entry<Integer, V> old_entry = values.remove(key);
        if(old_entry != null) put_orders.remove(old_entry.getKey());
        return old_entry == null ? null : old_entry.getValue();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {
        put_orders = new AVLTreeMap<>();
        values = new AVLTreeMap<>();
        items_put = 0;
    }

    @Override
    public Set<K> keySet() {
        LinkedList<K> res = new LinkedList<>();
        for (Entry<Integer, Entry<K, V>> entry: put_orders.entrySet()){
            res.add(entry.getValue().getKey());
        }
        return new FrozenSet<>(res);
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        LinkedList<Entry<K, V>> res = new LinkedList<>();
        for (Entry<Integer, Entry<K, V>> entry: put_orders.entrySet()){
            res.add(entry.getValue());
        }
        return new FrozenSet<>(res);
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
