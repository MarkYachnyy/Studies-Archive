package ru.vsu.cs.yachnyy_m_a.mytree.map;

import ru.vsu.cs.yachnyy_m_a.mytree.AVLTree;

import java.util.*;

public class AVLTreeMap<K extends Comparable<K>, V> implements BinarySearchTreeMap<K, V>, Iterable<Map.Entry<K, V>> {

    static class AVLTreeMapEntry<K extends Comparable<K>, V> implements Map.Entry<K, V>, Comparable<AVLTreeMapEntry<K, V>> {

        private final K key;
        private V value;

        public AVLTreeMapEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldVal = this.value;
            this.value = value;
            return oldVal;
        }

        @Override
        public int compareTo(AVLTreeMapEntry<K, V> o) {
            return AVLTreeMapEntry.this.getKey().compareTo(o.getKey());
        }
    }

    public AVLTreeMap() {
        tree = new AVLTree<>();
        size = 0;
    }

    private AVLTree<AVLTreeMapEntry<K, V>> tree;
    private int size;

    @Override
    public V get(Object key) {
        AVLTreeMapEntry<K, V> entry = getTree().get(new AVLTreeMapEntry<>((K) key, null));
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V put(K key, V value) {
        V old_val = get(key);
        getTree().put(new AVLTreeMapEntry<>(key, value));
        return old_val;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getTree().contains(new AVLTreeMapEntry<>((K) key, null));
    }

    @Override
    public boolean containsValue(Object value) {
        for(AVLTreeMapEntry<K, V> entry: getTree()){
            if(entry.getValue().equals(value))return true;
        }
        return false;
    }

    @Override
    public void clear() {
        tree = new AVLTree<>();
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        LinkedList<K> keys = new LinkedList<>();
        for (Entry<K, V> entry: this.getTree()){
            keys.add(entry.getKey());
        }
        return new FrozenSet<>(keys);
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        LinkedList<Entry<K, V>> entries = new LinkedList<>();
        for (Entry<K, V> entry: this.getTree()){
            entries.add(entry);
        }
        return new FrozenSet<>(entries);
    }

    @Override
    public V remove(Object key) {
        return getTree().remove(new AVLTreeMapEntry<>((K)key, null)).getValue();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        V val = get(key);
        return val == null ? defaultValue : val;
    }

    @Override
    public AVLTree<AVLTreeMapEntry<K, V>> getTree() {
        return tree;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return null;
    }
}
