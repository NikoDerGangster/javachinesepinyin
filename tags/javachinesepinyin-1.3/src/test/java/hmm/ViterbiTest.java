/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

import hmm.ngram.TreeNode;
import hmm.ngram.TreeNodeBinarySort;
import hmm.ngram.TreeNodeSortor;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author ray
 */
public class ViterbiTest extends TestCase {

    public ViterbiTest(String testName) {
        super(testName);
    }

    public void testViterbi() {
        List<String> o = new ArrayList<String>();
        Viterbi<String, String> viterbi = new Viterbi<String, String>();
        TreeNodeSortor<String> sortor = new TreeNodeBinarySort<String>();
        sortor.setComparator(new Comparator<TreeNode<String>>() {

            @Override
            public int compare(TreeNode<String> t, TreeNode<String> t1) {
                return t.getKey().compareTo(t1.getKey());
            }
        });
        viterbi.setSortor(sortor);
        viterbi.setComparator(new Comparator<String>() {

            @Override
            public int compare(String t, String t1) {
                return t.compareTo(t1);
            }
        });

        initTestData(viterbi);
        viterbi.setN(2);

        //o = [ T H T H T H ]
        o.add("T");
        o.add("H");
        o.add("T");
        o.add("H");
        o.add("T");
        o.add("H");
        try {
            List<Node<String>> s;
            s = viterbi.caculateWithLog(o);
            StringBuilder sb = new StringBuilder();
            for (Node state : s) {
                System.out.print(state.getName() + " ");
                sb.append(state.getName()).append(" ");
            }
            assert (sb.toString().trim().equals("three three three three three two"));
        } catch (ObserveListException ex) {
            Logger.getLogger(ViterbiTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void testViterbiWithUnknownState() {
        List<String> o = new ArrayList<String>();
        Viterbi<String, String> viterbi = new Viterbi<String, String>();
        TreeNodeSortor<String> sortor = new TreeNodeBinarySort<String>();
        sortor.setComparator(new Comparator<TreeNode<String>>() {

            @Override
            public int compare(TreeNode<String> t, TreeNode<String> t1) {
                return t.getKey().compareTo(t1.getKey());
            }
        });
        viterbi.setSortor(sortor);
        viterbi.setComparator(new Comparator<String>() {

            @Override
            public int compare(String t, String t1) {
                return t.compareTo(t1);
            }
        });

        initTestData(viterbi);
        viterbi.setN(2);

        //o = [ T H T H T H ]
        o.add("A");
        o.add("H");

        try {
            List<Node<String>> s;
            s = viterbi.caculateWithLog(o);
            StringBuilder sb = new StringBuilder();
            for (Node state : s) {
                System.out.print(state.getName() + " ");
                sb.append(state.getName()).append(" ");
            }
        } catch (ObserveListException ex) {
            assert (true);
        }

    }

    public static void initTestData(Viterbi<String, String> v) {
        State<String> s1 = new State("one");
        v.stateBank.add(s1);
        State<String> s2 = new State("two");
        v.stateBank.add(s2);
        State<String> s3 = new State("three");
        v.stateBank.add(s3);

        Observe<String> o1 = new Observe("H");
        v.observeBank.add(o1);
        Observe<String> o2 = new Observe("T");
        v.observeBank.add(o2);

        //transition
        //0.8 0.1 0.1
        //0.1 0.8 0.1
        //0.1 0.1 0.8
        v.tran.setStateBank(v.stateBank);
        v.tran.setProb(s1.getIndex(), s1.getIndex(), 0.3);
        v.tran.setProb(s1.getIndex(), s2.getIndex(), 0.3);
        v.tran.setProb(s1.getIndex(), s3.getIndex(), 0.4);
        v.tran.setProb(s2.getIndex(), s1.getIndex(), 0.2);
        v.tran.setProb(s2.getIndex(), s2.getIndex(), 0.6);
        v.tran.setProb(s2.getIndex(), s3.getIndex(), 0.2);
        v.tran.setProb(s3.getIndex(), s1.getIndex(), 0.2);
        v.tran.setProb(s3.getIndex(), s2.getIndex(), 0.2);
        v.tran.setProb(s3.getIndex(), s3.getIndex(), 0.6);
        v.tran.getRoot().printTreeNode("");
        //emission
        //0.5 0.5
        //0.8 0.2
        //0.2 0.8
        v.e.setProb(o1.getIndex(), s1.getIndex(), 0.5);
        v.e.setProb(o2.getIndex(), s1.getIndex(), 0.5);
        v.e.setProb(o1.getIndex(), s2.getIndex(), 0.8);
        v.e.setProb(o2.getIndex(), s2.getIndex(), 0.2);
        v.e.setProb(o1.getIndex(), s3.getIndex(), 0.2);
        v.e.setProb(o2.getIndex(), s3.getIndex(), 0.8);
        v.e.setObserveBank(v.observeBank);

        //Pi = [0.2 0.3 0.5]
        v.pi.setPi(s1.getIndex(), 0.2);
        v.pi.setPi(s2.getIndex(), 0.4);
        v.pi.setPi(s3.getIndex(), 0.4);
    }
}
