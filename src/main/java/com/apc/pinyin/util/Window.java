/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apc.pinyin.util;

import java.util.*;

/**
 *
 * @author ray
 */
public class Window<T> {

    private int size = 0;
    private List<T> datas = new ArrayList<T>();

    public Window(int size) {
        this.size = size;
    }

    public void clear() {
        datas.clear();
    }

    public void add(T data) {
        datas.add(data);
        if (datas.size() > size) {
            datas.remove(0);
        }
    }

    public T[] toArray(T[] t) {
        return datas.toArray(t);
    }
}
