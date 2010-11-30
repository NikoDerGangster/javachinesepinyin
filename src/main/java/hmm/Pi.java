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
public class Pi {

    private Map<Integer, Double> pi = new HashMap<Integer, Double>();
    private int total = 1;

    public Pi() {
    }

    public Pi(Map<Integer, Integer> pii) {
        Set<Integer> keySet = pii.keySet();
        for (Integer i : keySet) {
            total += pii.get(i);
        }

        for (Integer i : keySet) {
            setPi(i, (double) pii.get(i) / (double) (total));
        }
    }

    public double getPi(int index) {
        if (pi.containsKey(index)) {
            return pi.get(index);
        } else {
            return 1.0 / (double) total;
        }
    }

    public void setPi(int index, double prob) {
        pi.put(index, prob);
    }
}
