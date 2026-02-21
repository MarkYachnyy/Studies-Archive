package ru.vsu.cs.yachnyy_m_a.logic;

import ru.vsu.cs.yachnyy_m_a.logic.factories.PriorityQueueFactory;

import java.util.*;

public class MyPriorityQueue<T> extends PriorityQueue<T> {

    private class Node<T>{
        T value;
        Node<T> next;

        public Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }

        public Node(T value){
            this(value, null);
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private Comparator<T> comparator;

    public MyPriorityQueue(Comparator<T> comparator){
        this(comparator, null);
    }

    public MyPriorityQueue(Comparator<T> comparator, List<T> items){
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.comparator = comparator;
        if(items != null)this.addAll(items);
    }

    @Override
    public T peek() {
        if(this.isEmpty()) throw new IndexOutOfBoundsException();
        return head.value;
    }

    @Override
    public T poll() {
        if(this.isEmpty()) throw new IndexOutOfBoundsException();
        T res = head.value;
        if(size == 1) tail = null;
        head = head.next;
        size--;
        return res;
    }

    @Override
    public boolean add(T t) {
        if(size == 0){
            head = new Node<>(t, null);
            tail = head;
        } else {
            Node<T> comparing_to = head;
            Node<T> prev = null;
            while(comparing_to != null && comparator.compare(t, comparing_to.value) >= 0){
                prev = comparing_to;
                comparing_to = comparing_to.next;
            }
            Node<T> new_node = new Node<>(t, comparing_to);
            if(prev != null){
                prev.next = new_node;
            } else {
                head = new_node;
            }
            if(new_node.next == null) tail = tail.next;
        }
        size++;
        return true;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Iterator<T> iterator() {
        class MyIterator implements Iterator<T>{

            Node<T> curr = head;

            @Override
            public boolean hasNext() {
                return curr != null;
            }

            @Override
            public T next() {
                T value = curr.value;
                curr = curr.next;
                return value;
            }
        }

        return new MyIterator();
    }

    @Override
    public String toString() {
        if(this.isEmpty()) return "[]";
        StringBuilder res = new StringBuilder().append('[');
        for (T item: this){
            res.append(item.toString()).append(", ");
        }
        return res.substring(0, res.length() - 2) + ']';
    }
}
