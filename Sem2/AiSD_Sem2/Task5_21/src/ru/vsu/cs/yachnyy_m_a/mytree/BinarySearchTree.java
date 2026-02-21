package ru.vsu.cs.yachnyy_m_a.mytree;

import ru.vsu.cs.yachnyy_m_a.trees.BinaryTree;
import ru.vsu.cs.yachnyy_m_a.trees.BinaryTreeAlgorithms;

import java.util.Iterator;

public interface BinarySearchTree<T extends Comparable<T>> extends BinaryTree<T> {

    boolean contains(T value);

    boolean put(T value);

    boolean remove(T value);

    void clear();

    int size();

    T get(T value);

    TreeNode<T> getMinNode();

    default T getMin() {
        TreeNode<T> minNode = getMinNode();
        return (minNode == null) ? null : minNode.getValue();
    }

    TreeNode<T> getMaxNode();

    default T getMax() {
        TreeNode<T> minNode = getMinNode();
        return (minNode == null) ? null : minNode.getValue();
    }

    @Override
    default Iterator<T> iterator() {
        return BinaryTreeAlgorithms.inOrderValues(getRoot()).iterator();
    }
}
