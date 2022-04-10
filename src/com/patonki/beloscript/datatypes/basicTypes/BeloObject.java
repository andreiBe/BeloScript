package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;

import java.util.HashMap;

public class BeloObject extends BeloClass{
    private final HashMap<String, BeloClass> map = new HashMap<>();

    public void put(String key, BeloClass value) {
        this.map.put(key,value);
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        String key = name.toString();
        if (! map.containsKey(key)) {
            return throwError("Key not in object: "+key,name.getStart(),name.getEnd());
        }
        return map.get(key);
    }

    @Override
    public BeloClass setClassValue(String name, BeloClass newValue) {
        return map.put(name,newValue);
    }


    @Override
    public int compare(BeloClass another) {
        if (another instanceof BeloObject) {
            boolean b = this.map.equals(((BeloObject) another).map);
            return b ? 0 : -1;
        }
        return -1;
    }

    @Override
    public BeloClass index(BeloClass index) {
        String key = index.toString();
        if (! map.containsKey(key)) {
            return throwError("Key not in object: "+key,index.getStart(),index.getEnd());
        }
        return map.get(key);
    }

    @Override
    public BeloClass setIndex(BeloClass index, BeloClass value) {
        String name = index.toString();
        return map.put(name,value);
    }
    @Override
    public String toString() {
        return this.map.toString();
    }
}
