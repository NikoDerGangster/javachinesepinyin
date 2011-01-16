/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

import hmm.ngram.TreeNode;
import hmm.ngram.TreeNodeSortor;
import java.util.Comparator;

/**
 *
 * @author ray
 */
public class Transition<T> {

    TreeNode<T> root = null;

    public TreeNode<T> getRoot() {
        return root;
    }
    NodeBank<T, Node<T>> stateBank = null;
    Comparator<T> comparator = null;
    TreeNodeSortor<T> sortor = null;

    public void setStateBank(NodeBank<T, Node<T>> stateBank) {
        this.stateBank = stateBank;
    }

    public void setSortor(TreeNodeSortor<T> sortor) {
        this.sortor = sortor;
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public Transition() {
        this.root = new TreeNode<T>();
    }

    public Transition(TreeNode<T> root, NodeBank<T, Node<T>> bank) {
        this.root = root;
        this.stateBank = bank;
    }

    public void setProb(int s1, int s2, double prob) {
        T[] ngram = (T[]) new Object[2];
        ngram[0] = stateBank.get(s1).getName();
        ngram[1] = stateBank.get(s2).getName();
        TreeNode<T> node = root.insert(ngram, sortor, comparator);
        node.setProb(prob);
    }

    public double getProb(int[] s) {
        T[] ngram = (T[]) new Object[s.length];
        for (int i = 0; i < s.length; i++) {
            ngram[i] = stateBank.get(s[i]).getName();
        }
        return getProb(ngram, ngram.length);
    }

    public double getProb(int[] c, int s) {
        T[] ngram = (T[]) new Object[c.length + 1];
        for (int i = 0; i < c.length; i++) {
            ngram[i] = stateBank.get(c[i]).getName();
        }
        ngram[c.length] = stateBank.get(s).getName();

        return getProb(ngram, ngram.length);
    }

    public double getProb(int s1, int s2) {
        T[] ngram = (T[]) new Object[2];
        ngram[0] = stateBank.get(s1).getName();
        ngram[1] = stateBank.get(s2).getName();
        return getProb(ngram, 2);
    }

    public double getProb(T[] ngram, int n) {
        double ret = 0.00000001D;

        //bigram
        if (2 == n) {
            return getProb(ngram);
        }

        for (int i = n; i > 0; i--) {
            T[] igram = (T[]) new Object[i];
            for (int j = 1; j <= i; j++) {
                igram[i - j] = ngram[n - j];
            }
            ret += Flag.getInstance().labda(i - 1) * getProb(igram);
        }

        return ret;
    }

    private double getProb(T[] ngram) {
        double ret = 0.00000001D;

        TreeNode node = root.searchNode(ngram, comparator);
        if (null != node) {
            ret = node.getProb();
        } else {
            ret = 1.0 / root.getCount();
        }

        return ret;
    }
}
