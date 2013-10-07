
package com.google.refine.compression;

import java.io.Serializable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ValueBiMapManager {

    private BiMap<Integer, Serializable> map;
    private int index = 0;

    public ValueBiMapManager() {
        map = HashBiMap.create();
    }

    public int put(Serializable value) {
        if (map.containsValue(value)) {
            return map.inverse().get(value);
        } else {
            index++;
            map.put(index, value);
            return index;
        }
    }

    public Serializable getValue(int index) {
        return map.get(index);
    }

}
