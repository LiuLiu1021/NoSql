package com.company;

import java.util.ArrayList;
import java.util.List;

public class AllPath {

    public static List<String> findAllPath(TreeNode root) {
        List<String> paths = new ArrayList<>();

        if(root == null) {
            return paths;
        }

        List<String> leftPaths = findAllPath(root.left);
        List<String> rightPaths = findAllPath(root.right);

        for(String path : leftPaths) {
            paths.add(root.val + "->" + path);
        }

        for(String path : rightPaths) {
            paths.add(root.val + "->" + path);
        }

        if(paths.isEmpty()) {
            paths.add("" + root.val);
        }

        return paths;
    }

    public static void main(String[] args) {
        int[] str={1,2,3,4,5,6};
        List<TreeNode> list=TreeNode.createBinTree(str);
        List<String> paths = findAllPath(list.get(0));
        for(String path : paths) {
            System.out.println(path);
        }
    }
}
