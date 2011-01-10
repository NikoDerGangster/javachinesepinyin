/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ngram;

import hmm.*;
import hmm.ngram.*;
import java.util.Comparator;
import junit.framework.TestCase;

/**
 *
 * @author ray
 */
public class NgramTest extends TestCase {

    public void testNgram() {
        String a = "a";
        String a1 = "abc";
        String a2 = "acb";
        String b = "b";
        String b1 = "bc";
        TreeNode<Character> root = new TreeNode<Character>();
        NodeBank<Character, Node<Character>> stateBank = new NodeBank<Character, Node<Character>>();
        stateBank.add(new State<Character>('a'));
        stateBank.add(new State<Character>('b'));

        CharComparater comparator = new CharComparater();
        TreeNodeSortor<Character> sortor = new TreeNodeQuickSort<Character>();
        sortor.setComparator(new Comparator<TreeNode<Character>>() {

            @Override
            public int compare(TreeNode<Character> t, TreeNode<Character> t1) {
                return t.getKey() - t1.getKey();
            }
        });

        TreeNode<Character> node = root.insert(TreeNode.stringToCharArray(a), sortor, comparator);
        node = root.insert(TreeNode.stringToCharArray(a2), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(b), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(a1), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(a1), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(b1), sortor, comparator);

        root.printTreeNode("");
        root.buildIndex(root.getCount(), sortor);

        root.printTreeNode("");

        Transition<Character> tran = new Transition<Character>(root, stateBank);
        tran.setComparator(comparator);
        tran.setSortor(sortor);
        double p = tran.getProb(TreeNode.stringToCharArray(a2), 3);
        assert ((p - 0.3531) < 0.0001 && (p - 0.3531) > -0.0001);
        System.out.println(p);
    }

    public void testTreeCut() {
        String a = "a";
        String a1 = "abc";
        String a2 = "acb";
        String b = "b";
        String b1 = "bc";
        TreeNode<Character> root = new TreeNode<Character>();
        NodeBank<Character, Node<Character>> stateBank = new NodeBank<Character, Node<Character>>();
        stateBank.add(new State<Character>('a'));
        stateBank.add(new State<Character>('b'));

        CharComparater comparator = new CharComparater();
        TreeNodeSortor<Character> sortor = new TreeNodeQuickSort<Character>();
        sortor.setComparator(new Comparator<TreeNode<Character>>() {

            @Override
            public int compare(TreeNode<Character> t, TreeNode<Character> t1) {
                return t.getKey() - t1.getKey();
            }
        });

        TreeNode<Character> node = root.insert(TreeNode.stringToCharArray(a), sortor, comparator);
        node = root.insert(TreeNode.stringToCharArray(a2), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(b), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(a1), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(a1), sortor, comparator);
        root.insert(TreeNode.stringToCharArray(b1), sortor, comparator);
        root.printTreeNode("");

        System.out.println("Total count: " + root.getCount());
        System.out.println("Number of Node which count lower than 2: " + root.getNumberOfNodeWhichCountLt(2));

        root.cutCountLowerThan(2);
        root.printTreeNode("");
    }
}

class CharComparater implements Comparator<Character> {

    @Override
    public int compare(Character t, Character t1) {
        return t.charValue() - t1.charValue();
    }
}
