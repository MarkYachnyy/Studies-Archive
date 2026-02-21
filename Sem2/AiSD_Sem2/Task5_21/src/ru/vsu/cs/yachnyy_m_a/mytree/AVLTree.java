package ru.vsu.cs.yachnyy_m_a.mytree;

public class AVLTree<T extends Comparable<T>> implements BinarySearchTree<T> {

    class AVLTreeNode implements TreeNode<T> {

        public T value;
        public AVLTreeNode left;
        public AVLTreeNode right;
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
        public AVLTreeNode getLeft() {
            return left;
        }

        @Override
        public AVLTreeNode getRight() {
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

    private AVLTreeNode root;
    private int size;

    @Override
    public boolean contains(T value) {
        return contains(getRoot(), value);
    }

    private boolean contains(AVLTreeNode node, T elem) {
        if (elem.compareTo(node.getValue()) < 0) {
            return node.getLeft() != null && contains(node.getLeft(), elem);
        } else if (elem.compareTo(node.getValue()) > 0) {
            return node.getRight() != null && contains(node.getRight(), elem);
        } else return true;
    }

    @Override
    public boolean put(T value) {
        int size_before = size();
        this.root = put(getRoot(), value);
        return size() > size_before;
    }

    private AVLTreeNode put(AVLTreeNode node, T elem) {
        if (node == null) {
            size++;
            return new AVLTreeNode(elem);
        }
        int cmp = elem.compareTo(node.value);
        if (cmp != 0) {
            if (cmp < 0) {
                node.left = put(node.left, elem);
            } else {
                node.right = put(node.right, elem);
            }
            node.recalcHeight();
            node = balance(node);
        }
        return node;
    }

    private AVLTreeNode remove(AVLTreeNode node, T elem) {
        if (node == null) {
            return null;
        }
        int cmp = elem.compareTo(node.value);
        if (cmp == 0) {
            if (node.left != null && node.right != null) {
                node.value = getMinNode(node.right).getValue();
                node.right = remove(node.right, node.value);
            } else {
                node = (node.left != null) ? node.left : node.right;
                size--;
            }
        } else if (cmp < 0)
            node.left = remove(node.left, elem);
        else {
            node.right = remove(node.right, elem);
        }
        return balance(node);
    }

    @Override
    public T get(T value) {
        return get(getRoot(), value);
    }

    private T get(AVLTreeNode node, T elem) {
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
    public boolean remove(T value) {
        int size_before = size();
        remove(getRoot(), value);
        return size() < size_before;
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
    public AVLTreeNode getMinNode() {
        return getMinNode(getRoot());
    }

    @Override
    public AVLTreeNode getMaxNode() {
        return getMaxNode(getRoot());
    }

    public AVLTreeNode getMinNode(AVLTreeNode node) {
        return node.getLeft() == null ? node : getMinNode(node.getLeft());
    }

    public AVLTreeNode getMaxNode(AVLTreeNode node) {
        return node.getRight() == null ? node : getMaxNode(node.getRight());
    }

    @Override
    public AVLTreeNode getRoot() {
        return root;
    }

    private AVLTreeNode balance(AVLTreeNode node) {
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

    private AVLTreeNode leftRotate(AVLTreeNode node) {
        AVLTreeNode right = node.right;
        node.right = right.left;
        right.left = node;
        node.recalcHeight();
        right.recalcHeight();
        return right;
    }

    private AVLTreeNode rightRotate(AVLTreeNode node) {
        AVLTreeNode left = node.left;
        node.left = left.right;
        left.right = node;
        node.recalcHeight();
        left.recalcHeight();
        return left;
    }



}
