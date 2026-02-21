package ru.vsu.cs.yachnyy_m_a.mytree;

import java.util.*;

public class AVLTreeMap<K extends Comparable<K>, V> implements BinarySearchTreeMap<K, V>, Iterable<Map.Entry<K, V>> {

    class AVLTreeMapEntry implements Entry<K, V>, Comparable<AVLTreeMapEntry> {

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
        public int compareTo(AVLTreeMapEntry o) {
            return AVLTreeMapEntry.this.getKey().compareTo(o.getKey());
        }
    }

    public AVLTreeMap() {
        tree = new AVLTree<>();
        size = 0;
    }

    private AVLTree<AVLTreeMapEntry> tree;
    private int size;

    @Override
    public V get(Object key) {
        AVLTreeMapEntry entry = getTree().get(new AVLTreeMapEntry((K) key, null));
        return entry == null ? null : entry.getValue();
    }

    @Override
    public V put(K key, V value) {
        V old_val = get(key);
        getTree().put(new AVLTreeMapEntry(key, value));
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
        return getTree().contains(new AVLTreeMapEntry((K) key, null));
    }

    @Override
    public boolean containsValue(Object value) {
        for(AVLTreeMapEntry entry: getTree()){
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
        for (Entry<K, V> entry: this){
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
        for (Entry<K, V> entry: this){
            entries.add(entry);
        }
        return new FrozenSet<>(entries);
    }

    @Override
    public V remove(Object key) {
        return null;
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
    public AVLTree<AVLTreeMapEntry> getTree() {
        return tree;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return null;
    }

    private static class FrozenSet<T> implements Set<T>{

        private List<T> list;

        private FrozenSet(List<T> items){
            this.list = items;
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }

        @Override
        public Iterator<T> iterator() {
            return list.iterator();
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <T1> T1[] toArray(T1[] a) {
            return null;
        }

        @Override
        public boolean add(T t) {
            throw new UnsupportedOperationException("This set is immutable");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("This set is immutable");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            boolean res = true;
            for(Object item: c){
                res &= this.contains(item);
            }
            return res;
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException("This set is immutable");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("This set is immutable");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("This set is immutable");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("This set is immutable");
        }
    }
}
