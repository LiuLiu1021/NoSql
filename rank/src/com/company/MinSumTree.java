package com.company;

import java.util.ArrayList;
import java.util.List;

public class MinSumTree {

    private static int sum = Integer.MAX_VALUE;

    public static int findMinSumTree(TreeNode root) {
        if(root == null) {
            return 0;
        }

        int left = findMinSumTree(root.left);
        int right = findMinSumTree(root.right);
        sum = Math.min(sum, left + right + root.val);
        return sum;
    }


    public static void main(String[] args) {
        int[] str={1,2,3,4,5,6};
        List<TreeNode> list=TreeNode.createBinTree(str);

        System.out.println(findMinSumTree(list.get(0)));
    }
}
