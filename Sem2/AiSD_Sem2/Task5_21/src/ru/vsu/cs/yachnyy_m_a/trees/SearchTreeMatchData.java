package ru.vsu.cs.yachnyy_m_a.trees;

import java.util.*;

public class SearchTreeMatchData {

    private List<String> odd_paths;
    private List<String> non_unique_paths;
    private boolean can_transform;

    private SearchTreeMatchData(List<String> odd_paths, List<String> non_unique_paths, boolean can_transform) {
        this.odd_paths = odd_paths;
        this.non_unique_paths = non_unique_paths;
        this.can_transform = can_transform;
    }

    public List<String> getOddPaths() {
        return odd_paths;
    }

    public List<String> getNonUniquePaths(){return non_unique_paths;}

    public boolean containsOddPath(String path){
        if(path.equals("")) return false;
        for (String p: odd_paths) {
            if (path.startsWith(p)) return true;
        }
        return false;
    }

    public boolean canTransform() {
        return can_transform;
    }

    public static <T> SearchTreeMatchData check(BinaryTree<T> tree, Comparator<T> comp) {
        List<String> odd_paths = new ArrayList<>();
        boolean can_transform = checkNode(tree.getRoot(), odd_paths, "", comp) && odd_paths.size() == 1;
        List<String> non_unique_paths = uniquenessData(tree);
        if(can_transform) {
            if(non_unique_paths.size() > 2 || (non_unique_paths.size() == 2 && !non_unique_paths.contains(odd_paths.get(0)))){
                can_transform = false;
            }
        } else if(odd_paths.size() == 0 && non_unique_paths.size() == 2 && (isLeaf(tree.getNode(non_unique_paths.get(0))) || isLeaf(tree.getNode(non_unique_paths.get(1))))){
            can_transform = true;
        }
        return new SearchTreeMatchData(odd_paths, non_unique_paths, can_transform);
    }

    private static <T> boolean checkNode(BinaryTree.TreeNode<T> node, List<String> list, String path, Comparator<T> comp) {

        BinaryTree.TreeNode<T> left = node.getLeft();
        boolean flag_left;
        if (left != null) {
            if (comp.compare(left.getValue(), node.getValue()) >= 0) {
                list.add(path + 'L');
                flag_left = isLeaf(left);
            } else {
                flag_left = checkNode(left, list, path + 'L', comp);
            }
        } else flag_left = true;

        BinaryTree.TreeNode<T> right = node.getRight();
        boolean flag_right;
        if (right != null) {
            if (comp.compare(right.getValue(), node.getValue()) <= 0) {
                list.add(path + 'R');
                flag_right = isLeaf(right);
            } else {
                flag_right = checkNode(right, list, path + 'R', comp);
            }
        } else flag_right = true;

        return flag_right && flag_left;
    }

    private static <T> boolean isLeaf(BinaryTree.TreeNode<T> node) {
        return node.getLeft() == null && node.getRight() == null;
    }

    private static <T> List<String> uniquenessData(BinaryTree<T> tree){
        HashMap<T, List<String>> encounters = new HashMap<>();
        uniquenessCheckNode(tree.getRoot(), encounters, "");
        ArrayList<String> res = new ArrayList<>();
        for (List<String> paths: encounters.values()){
            if(paths.size() > 1) res.addAll(paths);
        }
        return res;
    }

    private static <T> void uniquenessCheckNode(BinaryTree.TreeNode<T> node, Map<T, List<String>> encounters, String path){
        if(!encounters.containsKey(node.getValue()))encounters.put(node.getValue(), new ArrayList<>());
        encounters.get(node.getValue()).add(path);
        if(node.getLeft() != null)uniquenessCheckNode(node.getLeft(), encounters, path+"L");
        if(node.getRight() != null)uniquenessCheckNode(node.getRight(), encounters, path+"R");
    }

}
