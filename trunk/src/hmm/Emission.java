/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hmm;

import java.util.*;

/**
 *
 * @author ray
 */
public class Emission<T> {

    //observe -> states
    private Map<Integer, Map<Integer, Double>> matrix = new HashMap<Integer, Map<Integer, Double>>();
    int total = 0;
    private NodeBank<T, Node<T>> observeBank = null;

    public NodeBank<T, Node<T>> getObserveBank() {
        return observeBank;
    }

    public void setObserveBank(NodeBank<T, Node<T>> observeBank) {
        this.observeBank = observeBank;
    }
//    private Map<Integer, Integer> total = new HashMap<Integer, Integer>();

    public Emission() {
    }

    public Emission(Map<Integer, Map<Integer, Integer>> emisMatrix) {
        //state -> observes
        Set<Integer> states = emisMatrix.keySet();
        for (Integer state : states) {
            Map<Integer, Integer> mapO = emisMatrix.get(state);
            int sum = 1;
            Set<Integer> observes = mapO.keySet();
            for (Integer o : observes) {
                sum += mapO.get(o);
            }

            for (Integer o : observes) {
                double prob = (double) mapO.get(o) / (double) sum;
                setProb(o, state, prob);
            }
            total += sum;
        }
    }

    public double getProb(int o, int s) {
        Map<Integer, Double> e = matrix.get(o);
        if (null != e) {
            if (e.containsKey(s)) {
                return e.get(s);
            }
        }
        double ret = 0.0000001D;
        {
            ret = 1.0 / (double) total;
        }
        return ret;

    }

//    public Set<Integer> getStateProbByObserve(int index) {
//        Map<Integer, Double> map = matrix.get(index);
//        if (null != map) {
//            return map.keySet();
//        } else {
//            return null;
//        }
//    }
    public Set<Integer> getStateProbByObserve(T observe) {
        int index = observeBank.get(observe).getIndex();
        Map<Integer, Double> map = matrix.get(index);
        if (null != map) {
            return map.keySet();
        } else {
            map = add(observe);
            return null != map ? map.keySet() : null;
        }
    }

    private Map<Integer, Double> add(T observe) {
        Set<T> keySet = observeBank.keySet();
        Set<T> simSet = new HashSet<T>();
        String o = String.valueOf(observe);
        for (T k : keySet) {
            String key = String.valueOf(k);
            if (key.startsWith(o)) {
                simSet.add(k);
            }
        }

        Map<Integer, Double> probMap = new HashMap<Integer, Double>();
        for (T k : simSet) {
            Map<Integer, Double> map = matrix.get(observeBank.get(k).getIndex());
            if (map != null) {
                Set<Integer> set = map.keySet();
                for (Integer state : set) {
                    probMap.put(state, map.get(state));
                }
            }
        }

        matrix.put(observeBank.get(observe).getIndex(), probMap);
        return probMap;
    }

    public void setProb(int o, int s, double prob) {
        Map<Integer, Double> map = matrix.get(o);
        if (null == map) {
            map = new HashMap<Integer, Double>();
            matrix.put(o, map);
        }
        map.put(s, prob);
    }
}
