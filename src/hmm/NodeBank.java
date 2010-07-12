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
public class NodeBank<T,N extends Node<T>>   implements java.io.Serializable {

    List<N> bank = null;
    Map<T, Integer> indexMap = null;

    public NodeBank() {
        bank = new ArrayList<N>();
        indexMap = new HashMap<T, Integer>();
    }

    public N add(N node) {
        T name = node.getName();
        if (!indexMap.containsKey(name)) {
            int index = bank.size();
            node.setIndex(index);
            bank.add(node);
            indexMap.put(name, index);
            return node;
        } else {
            return bank.get(indexMap.get(name));
        }
    }

    public N get(T name) {
        if (indexMap.containsKey(name)) {
            int index = indexMap.get(name);
            return bank.get(index);
        } else {
            return null;
        }
    }

    public N get(int index) {
        if (bank.size() > index) {
            return bank.get(index);
        } else {
            return null;
        }
    }

    public Set<T> keySet(){
        return indexMap.keySet();
    }
}
