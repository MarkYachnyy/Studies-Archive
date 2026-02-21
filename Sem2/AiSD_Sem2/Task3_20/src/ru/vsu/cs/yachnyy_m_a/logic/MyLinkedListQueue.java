package ru.vsu.cs.yachnyy_m_a.logic;

import ru.vsu.cs.yachnyy_m_a.logic.factories.QueueFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;

public class MyLinkedListQueue<T> implements Queue<T> {

    private MyLinkedList<T> list;

    public MyLinkedListQueue(){
        this.list = new MyLinkedList<>();
    }

    public MyLinkedListQueue(List<T> list){
        this.list = new MyLinkedList<>();
        for(T item: list){
            this.add(item);
        }
    }

    @Override
    public boolean add(T item) {
        list.add(item);
        return true;
    }
    @Override
    public T poll(){
        T element = peek();
        list.remove(0);
        return element;
    }
    @Override
    public T peek(){
        return list.getFirst();
    }

    public static <D> QueueFactory<D> factory(){
        return MyLinkedListQueue::new;
    }

    /*************************
     Методы далее не используются
     *************************/

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for(T element: c){
            this.add(element);
        }
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(T t) {
        return false;
    }

    @Override
    public T remove() {
        return null;
    }



    @Override
    public T element() {
        return null;
    }



    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return false;
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
    public String toString() {
        return list.toString();
    }
}
