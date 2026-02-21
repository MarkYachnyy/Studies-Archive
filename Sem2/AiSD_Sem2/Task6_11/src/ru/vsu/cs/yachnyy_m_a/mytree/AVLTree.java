package ru.vsu.cs.yachnyy_m_a.mytree;

public class AVLTree<T extends Comparable<T>> implements BinarySearchTree<T> {

    static class AVLTreeNode<T> implements BinaryTree.TreeNode<T> {

        public T value;
        public AVLTreeNode<T> left;
        public AVLTreeNode<T> right;
        public int height;

        public AVLTreeNode(T value) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.height = 0;
        }

        @Override
        public T getValue() {
            return value;
        }

        @Override
        public AVLTreeNode<T> getLeft() {
            return left;
        }

        @Override
        public AVLTreeNode<T> getRight() {
            return right;
        }

        public int getHeightDiff() {
            return (left == null ? -1 : left.height) - (right == null ? -1 : right.height);
        }

        public void recalcHeight() {
            height = Math.max((left == null ? -1 : left.height), (right == null ? -1 : right.height)) + 1;
        }
    }

    public AVLTree() {
        this.root = null;
        this.size = 0;
    }

    private AVLTreeNode<T> root;
    private int size;

    @Override
    public boolean contains(T value) {
        return contains(getRoot(), value);
    }

    private boolean contains(AVLTreeNode<T> node, T elem) {
        if (elem.compareTo(node.getValue()) < 0) {
            return node.getLeft() != null && contains(node.getLeft(), elem);
        } else if (elem.compareTo(node.getValue()) > 0) {
            return node.getRight() != null && contains(node.getRight(), elem);
        } else return true;
    }

    @Override
    public T put(T value) {
        TreeOperationData res = put(getRoot(), value);
        this.root = res.node;
        return res.value;
    }

    private TreeOperationData put(AVLTreeNode<T> node, T elem) {
        if (node == null) {
            size++;
            return new TreeOperationData(new AVLTreeNode<>(elem), elem);
        }
        int cmp = elem.compareTo(node.value);
        T resValue;
        if (cmp != 0) {
            if (cmp < 0) {
                TreeOperationData leftRes = put(node.left, elem);
                node.left = leftRes.node;
                resValue = leftRes.value;
            } else {
                TreeOperationData rightRes = put(node.right, elem);
                node.right = rightRes.node;
                resValue = rightRes.value;
            }
            node.recalcHeight();
            node = balance(node);
        } else {
            resValue = node.value;
            node.value = elem;
        }
        return new TreeOperationData(node, resValue);
    }

    private TreeOperationData remove(AVLTreeNode<T> node, T elem) {
        if (node == null) {
            return new TreeOperationData(null, null);
        }
        int cmp = elem.compareTo(node.value);
        T resValue;
        if (cmp == 0) {
            resValue = node.value;
            if (node.left != null && node.right != null) {
                node.value = getMinNode(node.right).getValue();
                node.right = remove(node.right, node.value).node;
            } else {
                node = (node.left != null) ? node.left : node.right;
                size--;
            }
        } else if (cmp < 0){
            TreeOperationData leftRes = remove(node.left, elem);
            node.left = leftRes.node;
            resValue = leftRes.value;
        } else {
            TreeOperationData rightRes = remove(node.right, elem);
            node.right = rightRes.node;
            resValue = rightRes.value;
        }
        return new TreeOperationData(balance(node), resValue);
    }

    @Override
    public T get(T value) {
        return root == null ? null : get(getRoot(), value);
    }

    private T get(AVLTreeNode<T> node, T elem) {
        int c = elem.compareTo(node.getValue());
        if (c == 0) {
            return node.value;
        } else if (c < 0 && node.getLeft() != null) {
            return get(node.getLeft(), elem);
        } else if (c > 0 && node.getRight() != null) {
            return get(node.getRight(), elem);
        }
        return null;
    }

    @Override
    public T remove(T value) {
        TreeOperationData res = remove(getRoot(), value);
        this.root = res.node;
        return res.value;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }


    @Override
    public AVLTreeNode<T> getMinNode() {
        return getMinNode(getRoot());
    }

    @Override
    public AVLTreeNode<T> getMaxNode() {
        return getMaxNode(getRoot());
    }

    public AVLTreeNode<T> getMinNode(AVLTreeNode<T> node) {
        return node.getLeft() == null ? node : getMinNode(node.getLeft());
    }

    public AVLTreeNode<T> getMaxNode(AVLTreeNode<T> node) {
        return node.getRight() == null ? node : getMaxNode(node.getRight());
    }

    @Override
    public AVLTreeNode<T> getRoot() {
        return root;
    }

    private AVLTreeNode<T> balance(AVLTreeNode<T> node) {
        if (node == null) {
            return null;
        }
        if (node.getHeightDiff() < -1) {
            if (node.right != null && node.right.getHeightDiff() > 0) {
                node.right = rightRotate(node.right);
            }
            node = leftRotate(node);
        } else if (node.getHeightDiff() > 1) {

            if (node.left != null && node.left.getHeightDiff() < 0) {
                node.left = leftRotate(node.left);
            }
            node = rightRotate(node);
        }
        return node;
    }

    private AVLTreeNode<T> leftRotate(AVLTreeNode<T> node) {
        AVLTreeNode<T> right = node.right;
        node.right = right.left;
        right.left = node;
        node.recalcHeight();
        right.recalcHeight();
        return right;
    }

    private AVLTreeNode<T> rightRotate(AVLTreeNode<T> node) {
        AVLTreeNode<T> left = node.left;
        node.left = left.right;
        left.right = node;
        node.recalcHeight();
        left.recalcHeight();
        return left;
    }

    private class TreeOperationData{
        public AVLTreeNode<T> node;
        public T value;

        public TreeOperationData(AVLTreeNode<T> node, T value) {
            this.node = node;
            this.value = value;
        }
    }


}
