package ru.cs.vsu.yachnyy_m_a;


import java.util.Comparator;
import java.util.Iterator;

public class MyLinkedList<T> implements Iterable<T> {



    private static class ListNode<T> {
        T value;
        ListNode<T> next;

        public ListNode(T value, ListNode<T> next) {
            this.value = value;
            this.next = next;
        }

        public ListNode(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private ListNode<T> head;
    private ListNode<T> tail;
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public MyLinkedList(T[] items) {
        head = null;
        tail = null;
        size = 0;
        for (T item : items) {
            this.add(item);
        }
    }

    public boolean isEmpty(){
        return size > 0;
    }



    public int size() {
        return size;
    }

    public T get(int index) {
        ListNode<T> node = head;
        if (node == null || index < 0 || index >= size) throw new IndexOutOfBoundsException();
        for (int i = 0; i < index; i++) {
            node = node.next;
        }
        return node.value;
    }

    public T getFirst(){
        if(head != null) return head.value;
        throw new IndexOutOfBoundsException();
    }

    public T getLast(){
        if(tail!= null)return tail.value;
        throw new IndexOutOfBoundsException();
    }

    public void add(T item) {
        add(size, item);
    }

    public void add(int index, T item) {
        if(index < 0 || index > size) throw new IndexOutOfBoundsException();
        ListNode<T> new_node = new ListNode<>(item);
        if(size == 0){
            head = new_node;
            tail = new_node;
        } else if(index == size){
            tail.next = new_node;
            tail = new_node;
        } else if(index == 0){
            new_node.next = head;
            head = new_node;
        } else {
            ListNode<T> prev_node = head;
            for (int i = 0; i < index - 1; i++) {
                prev_node = prev_node.next;
            }
            new_node.next = prev_node.next;
            prev_node.next = new_node;
        }
        size++;
    }

    public void remove(int index) {
        if (index == 0) {
            head = head.next;
            if (head == null) tail = null;
        } else if (index == size - 1) {
            tail = getNode(index - 1);
        } else {
            ListNode<T> prev = getNode(index - 1);
            ListNode<T> to_remove = prev.next;
            prev.next = to_remove.next;
        }
        size--;
    }

    private ListNode<T> getNode(int index) {
        ListNode<T> node = head;
        if (node == null || index < 0) throw new IndexOutOfBoundsException();
        for (int i = 0; i < index; i++) {
            node = node.next;
            if (node == null) throw new IndexOutOfBoundsException();
        }
        return node;
    }

    public void bubbleSort(Comparator<? super T> comparator) {
        if (size < 2) return;
        for (int i = size - 1; i >= 1; i--) {
            ListNode<T> prevNode = null;
            ListNode<T> currNode = head;
            ListNode<T> nextNode = currNode.next;
            for (int j = 0; j < i; j++) {
                if (comparator.compare(currNode.value, nextNode.value) > 0) {
                    currNode.next = nextNode.next;
                    nextNode.next = currNode;
                    if (prevNode == null) {
                        head = nextNode;
                    } else {
                        prevNode.next = nextNode;
                    }
                    prevNode = prevNode == null ? head : prevNode.next;
                    nextNode = currNode.next;
                } else {
                    prevNode = prevNode == null ? head : prevNode.next;
                    currNode = currNode.next;
                    nextNode = nextNode.next;
                }
            }
        }

        tail = getNode(size - 1);
    }

    @Override
    public Iterator<T> iterator() {
        class MyIterator implements Iterator<T>{

            ListNode<T> curr = head;
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
        String res = "[";
        ListNode<T> node = head;
        while (node != null) {
            res += node.value.toString();
            if (size > 1 && node != tail) res += ", ";
            node = node.next;
        }
        return res + "]";
    }
}
