/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngram;

import java.util.*;

/**
 *
 * @author ray
 */
public class TreeNode<T> implements java.io.Serializable {

    T key = null;
    int count = 0;
    double prob = 0.0;
//    List<TreeNode<T>> temp = null;
    TreeNode<T> descendant[] = null;

    public T getKey() {
        return key;
    }

    public void buildIndex(int c, TreeNodeSortor<T> sortor) {
        prob = (double) count / (double) (c + 1.0);
        if (null != descendant) {
            for (TreeNode<T> node : descendant) {
                node.buildIndex(count, sortor);
            }
            sortor.sort(descendant);
        }
    }

    public TreeNode<T> insert(T[] ngram, TreeNodeSortor<T> sortor, Comparator<T> comparator) {
        count++;
        if (ngram.length > 0) {
            T k = ngram[0];
            TreeNode<T> n = null != descendant ? binarySearch(descendant, descendant.length, k, comparator) : null;
            if (null == n) {
                n = new TreeNode<T>();
                n.key = k;
                add(n);
                descendant = sortor.sort(descendant);
            }

            T rec[] = (T[]) new Object[ngram.length - 1];
            for (int i = 1; i < ngram.length; i++) {
                rec[i - 1] = ngram[i];
            }
            return n.insert(rec, sortor, comparator);
        } else {
            return this;
        }
    }

    private void add(TreeNode<T> e) {
        int i = 0;
        if (null == descendant) {
            descendant = new TreeNode[1];
        } else {
            TreeNode[] tmp = new TreeNode[descendant.length + 1];
            for (int j = 0; j < descendant.length; j++) {
                tmp[j] = descendant[j];
            }
            i = descendant.length;
            descendant = tmp;
        }
        descendant[i] = e;
    }

    public TreeNode<T> searchNode(T[] ngram, Comparator<T> comparator) {
        T k = ngram[0];
        TreeNode<T> n = searchNode(k, comparator);
        if (null != n && ngram.length > 1) {
            T rec[] = (T[]) new Object[ngram.length - 1];
            for (int i = 1; i < ngram.length; i++) {
                rec[i - 1] = ngram[i];
            }
            return n.searchNode(rec, comparator);
        }
        return n;
    }

    public TreeNode<T> searchNode(T k, Comparator<T> comparator) {
        return null != descendant ? binarySearch(descendant, descendant.length, k, comparator) : null;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getProb() {
        return prob;
    }

    public void setProb(double prob) {
        this.prob = prob;
    }

    public TreeNode<T> binarySearch(TreeNode<T>[] list, int listLength, T searchItem, Comparator<T> comparator) {
        if (null == list) {
            return null;
        }
        int first = 0;
        int last = listLength - 1;
        int mid = -1;

        boolean found = false;
        while (first <= last && !found) {
            mid = (first + last) / 2;

            int i = comparator.compare(list[mid].key, searchItem);

            if (i == 0) {
                found = true;
            } else {
                if (i > 0) {
                    last = mid - 1;
                } else {
                    first = mid + 1;
                }
            }
        }

        if (found) {
            return list[mid];
        } else {
            return null;
        }
    }

    public void printTreeNode(String indent) {
        System.out.println(indent + key + " - " + count + " - " + prob);
        if (null != descendant) {
            for (TreeNode node : descendant) {
                node.printTreeNode(indent + "  ");
            }
        }
    }

    public static Character[] stringToCharArray(String s) {
        Character array[] = new Character[s.length()];
        for (int i = 0; i < s.length(); i++) {
            array[i] = s.charAt(i);
        }
        return array;
    }

    public long getNumberOfNodeWhichCountLt(int lt) {
        long c = count < lt ? 1 : 0;

        if (null != descendant) {
            for (TreeNode<T> node : descendant) {
                c += node.getNumberOfNodeWhichCountLt(lt);
            }
        }

        return c;
    }

    public void cutCountLowerThan(int lt) {
        if (lt == 1) {
            return;
        }
        if (null != descendant) {
            List<TreeNode<T>> l = new LinkedList<TreeNode<T>>();
            for (int i = 0; i < descendant.length; i++) {
                TreeNode<T> node = descendant[i];
                if (node.getCount() >= lt) {
                    l.add(node);
                    node.cutCountLowerThan(lt);
                }
            }

            descendant = l.toArray(new TreeNode[0]);
        }
    }
}
