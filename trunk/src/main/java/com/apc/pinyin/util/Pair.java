package com.apc.pinyin.util;

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
    K k = null;
    V v = null;

    public Pair(K k, V v){
        this.k = k;
        this.v = v;
    }

    public K getKey() {
        return k;
    }

    public V getValue() {
        return v;
    }

    public V setValue(V value) {
        v = value;
        return v;
    }


}
