/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

import hmm.ngram.*;
import java.util.*;

/**
 * http://www.cs.umb.edu/~srevilak/viterbi/
 * @author ray
 */
public class Viterbi<S, O> {

    NodeBank<S, Node<S>> stateBank = new NodeBank<S, Node<S>>();
    NodeBank<O, Node<O>> observeBank = new NodeBank<O, Node<O>>();
    Transition<S> tran = new Transition<S>();
    Pi pi = new Pi();
    Emission<O> e = new Emission<O>();
    int n = 3; // ngram

//    Comparator<T> comparator = null;
//    TreeNodeSort<T> sortor = null;
    public void setComparator(Comparator<S> comparator) {
        tran.setComparator(comparator);
    }

    public void setSortor(TreeNodeSortor<S> sortor) {
        tran.setSortor(sortor);
    }

    public void setN(int n) {
        this.n = n;
    }

    public Emission getE() {
        return e;
    }

    public void setE(Emission e) {
        this.e = e;
    }

    public NodeBank<O, Node<O>> getObserveBank() {
        return observeBank;
    }

    public void setObserveBank(NodeBank<O, Node<O>> observeBank) {
        this.observeBank = observeBank;
    }

    public Pi getPi() {
        return pi;
    }

    public void setPi(Pi pi) {
        this.pi = pi;
    }

    public NodeBank<S, Node<S>> getStateBank() {
        return stateBank;
    }

    public void setStateBank(NodeBank<S, Node<S>> stateBank) {
        this.stateBank = stateBank;
    }

    public Transition<S> getTran() {
        return tran;
    }

    public void setTran(Transition<S> tran) {
        this.tran = tran;
    }

    

    public static int[] getStatePath(int[][] states, int[][] psai, int end, int depth, int pos) {
        int maxDepth = end + 1 > depth ? depth : end + 1;
        int[] ret = new int[maxDepth];

        for (int i = 0; i < maxDepth; i++) {
            int state = states[end - i][pos];
            pos = psai[end - i][pos];
            ret[ret.length - i - 1] = state;
        }

        return ret;
    }

    public HmmResult caculateHmmResult(List<O> listObserve) {
        HmmResult ret = new HmmResult();

        O o = listObserve.get(0);
        Node<O> o1 = observeBank.get(o);
        if (o1 == null) {
            o1 = new Observe<O>(o);
            observeBank.add(o1);
        }
//        Set<Integer> relatedStates = e.getStateProbByObserve(o1.getIndex());
        Set<Integer> relatedStates = e.getStateProbByObserve(o1.getName());
        ret.states = new int[listObserve.size()][];
        ret.delta = new double[listObserve.size()][];
        ret.psai = new int[listObserve.size()][];
        ret.states[0] = new int[relatedStates.size()];
        ret.delta[0] = new double[relatedStates.size()];
        ret.psai[0] = new int[relatedStates.size()];
        int index = 0;
        for (Integer s : relatedStates) {
            ret.states[0][index] = s;
            ret.delta[0][index] = Math.log(pi.getPi(s)) + Math.log(e.getProb(o1.getIndex(), s));
            ret.psai[0][index] = 0;
            index++;
        }

        //
        for (int p = 1; p < listObserve.size(); p++) {
            o = listObserve.get(p);
            Node<O> oi = observeBank.get(o);
            if (oi == null) {
                oi = new Observe<O>(o);
                observeBank.add(oi);
            }
//            Map<Integer, Double> iStates = e.getStateProbByObserve(oi.getIndex());
            Set<Integer> stateSet = e.getStateProbByObserve(oi.getName());
            ret.states[p] = new int[stateSet.size()];
            ret.delta[p] = new double[stateSet.size()];
            ret.psai[p] = new int[stateSet.size()];
            int i = 0;
            for (int state : stateSet) {
                ret.states[p][i] = state;
                double maxDelta = Double.NEGATIVE_INFINITY;
                double maxPsai = Double.NEGATIVE_INFINITY;
                int ls = 0;
                for (int j = 0; j < ret.states[p - 1].length; j++) {
                    int[] statePath = getStatePath(ret.states, ret.psai, p - 1, n - 1, j);
                    double b = Math.log(e.getProb(oi.getIndex(), state));
                    double Aij = Math.log(tran.getProb(statePath, state));
                    double psai_j = ret.delta[p - 1][j] + Aij;
                    double delta_j = psai_j + b;
                    if (delta_j > maxDelta) {
                        maxDelta = delta_j;
                    }

                    if (psai_j > maxPsai) {
                        maxPsai = psai_j;
                        ls = j;
                    }
                }

                ret.delta[p][i] = maxDelta;
                ret.psai[p][i] = ls;

                i++;
            }
        }

        return ret;
    }

    public List<Node<S>> caculateWithLog(List<O> listObserve) {
        List<Node<S>> path = new ArrayList<Node<S>>();
        HmmResult ret = caculateHmmResult(listObserve);
        //
        double maxProb = Double.NEGATIVE_INFINITY;
        int pos = 0;
        for (int j = 0; j < ret.delta[listObserve.size() - 1].length; j++) {
            double p = ret.delta[listObserve.size() - 1][j];
            if (p > maxProb) {
                maxProb = p;
                pos = j;
            }
        }

        int[] statePath = getStatePath(ret.states, ret.psai, listObserve.size() - 1, listObserve.size(), pos);
        for (int state : statePath) {
            path.add(stateBank.get(state));
        }

        return path;
    }

    
}
