/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm.ngram;

import java.util.Comparator;

/**
 *
 * @author ray
 */
public interface TreeNodeSortor<T> {

    public void setComparator(Comparator<TreeNode<T>> comparator);

    public TreeNode<T>[] sort(TreeNode<T>[] values);
}
