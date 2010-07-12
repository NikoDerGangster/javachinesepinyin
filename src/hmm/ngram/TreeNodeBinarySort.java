/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm.ngram;

import java.util.*;

/**
 *
 * @author ray
 */
public class TreeNodeBinarySort<T> implements TreeNodeSortor<T> {

    Comparator<TreeNode<T>> comparator = null;

    public void setComparator(Comparator<TreeNode<T>> comparator) {
        this.comparator = comparator;
    }

    public TreeNode<T>[] sort(TreeNode<T>[] values) {
        List<TreeNode<T>> tmp = new ArrayList<TreeNode<T>>();
        for(TreeNode<T> node : values){
            tmp.add(node);
        }
        Collections.sort(tmp, comparator);
        return tmp.toArray(values);
    }
}
