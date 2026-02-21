package ru.vsu.cs.yachnyy_m_a.mytree.map;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class FrozenSet<T> implements Set<T> {

    private List<T> list;

    FrozenSet(List<T> items){
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
