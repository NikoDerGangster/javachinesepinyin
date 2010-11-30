/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apc.pinyin;

/**
 *
 * @author gxm
 */
enum NodeKind {

    LN, BN
};
/*
 * Trie 借点
 */

class TrieNode {

    char key;
    TrieNode[] points = null;
    NodeKind kind = null;
}

class LeafNode extends TrieNode {

    LeafNode(char k) {
        super.key = k;
        super.kind = NodeKind.LN;
    }
}

/*
 * Trie 内部结点
 */
class BranchNode extends TrieNode {

    BranchNode(char k) {
        super.key = k;
        super.kind = NodeKind.BN;
        super.points = new TrieNode[27];
    }
}

public class StandardTree {

    private TrieNode root = new BranchNode(' ');

    public void insert(String word) {
        TrieNode curNode = root;
        word = word + "$";
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
//            System.out.println("   插入 " + chars[i]);
            if (chars[i] == '$') {
                curNode.points[26] = new LeafNode('$');
//                System.out.println("   插入完毕，使当前结点 " + curNode.key);
            } else {
                int pSize = chars[i] - 'a';
                try {
                    if (curNode.points[pSize] == null) {

                        curNode.points[pSize] = new BranchNode(chars[i]);
//                    System.out.println(" 使当前结点 " + curNode.key + " 的第" + pSize + " 孩子指针指向字符：" + chars[i]);
                        curNode = curNode.points[pSize];
                    } else {
//                    System.out.println("  不插入，找到当前结点" + curNode.key + " 的第" + pSize + "孩子指针已经指向的字符：" + chars[i]);
                        curNode = curNode.points[pSize];
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("the error is :" + String.valueOf(chars[i]) + "  " + pSize + "  " + word);

                }
            }
        }
    }

   
    public boolean fullMatch(String word) {

        TrieNode curNode = root;
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {           
            int pSize = chars[i] - 'a';
            if (curNode.points[pSize] == null) {             
                return false;
            } else {
                curNode = curNode.points[pSize];
                if ((i == chars.length - 1)) {
                    curNode = curNode.points[26];
                    if (curNode != null && curNode.key == '$') {                       
                        return true;
                    }
                }
            }
        }
        return false;
    }

//    private void preRootTraverse(TrieNode curNode) {
//        if (curNode != null) {
//            System.out.println(curNode.key + " ");
//            if (curNode.kind == NodeKind.BN) {
//                for (TrieNode childNode : curNode.points) {
//                    preRootTraverse(childNode);
//                }
//            }
//        }
//    }
    /*
     * 得到Trie根结点
     */
//    public TrieNode getRoot() {
//        return root;
//    }

}


