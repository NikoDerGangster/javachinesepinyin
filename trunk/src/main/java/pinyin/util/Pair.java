package pinyin.util;

import java.util.Map;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ray
 */
public class Pair<K,V> implements Map.Entry<K,V> {
    final K k;
    V v = null;

    public Pair(K k, V v){
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        v = value;
        return v;
    }


}
