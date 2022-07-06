package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.interfaces.IterableBeloClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BeloObject extends BeloClass implements IterableBeloClass {
    private final HashMap<String, BeloClass> map = new HashMap<>();

    public void put(String key, BeloClass value) {
        this.map.put(key,value);
    }
    public BeloClass get(String key) {
        return map.get(key);
    }
    public List<String> keysAsString()  {
        return new ArrayList<>(map.keySet());
    }
    public List<BeloClass> keys() {
        return map.keySet().stream().map(BeloString::new).collect(Collectors.toList());
    }
    @Override
    public BeloClass classValue(BeloClass name) {
        String key = name.toString();
        if (key.equals("keys")) {
            return new BeloList(this.keys());
        }
        if (key.equals("size")) {
            return new BeloDouble(map.keySet().size());
        }
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

    @Override
    public List<BeloClass> iterableList() {
        return keys();
    }
}
