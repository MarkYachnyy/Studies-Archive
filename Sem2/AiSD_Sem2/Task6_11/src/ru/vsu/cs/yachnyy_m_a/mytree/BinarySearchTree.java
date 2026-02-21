package ru.vsu.cs.yachnyy_m_a.mytree;

import java.util.Iterator;

public interface BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    boolean contains(T value);

    T put(T value);

    T remove(T value);

    void clear();

    int size();

    T get(T value);

    BinaryTree.TreeNode<T> getMinNode();

    default T getMin() {
        BinaryTree.TreeNode<T> minNode = getMinNode();
        return (minNode == null) ? null : minNode.getValue();
    }

    BinaryTree.TreeNode<T> getMaxNode();

    default T getMax() {
        BinaryTree.TreeNode<T> maxNode = getMaxNode();
        return (maxNode == null) ? null : maxNode.getValue();
    }

    @Override
    default Iterator<T> iterator() {
        return BinaryTreeAlgorithms.inOrderValues(getRoot()).iterator();
    }
}
