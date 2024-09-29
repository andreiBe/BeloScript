package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.errors.BeloException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Obj extends CustomBeloClass implements Iterable<BeloClass> {
    private final HashMap<BeloClass, BeloClass> map = new HashMap<>();

    public static Obj create() throws BeloException {
        Obj obj = new Obj();
        obj.init_self();
        return obj;
    }
    @Override
    public Iterator<BeloClass> iterator() {
        return map.keySet().iterator();
    }


    @Override
    public BeloClass index(BeloClass index) {
        if (! map.containsKey(index)) {
            return throwError("Key not in object: "+index,index.getStart(),index.getEnd());
        }
        return map.get(index);
    }


    @Override
    public BeloClass setIndex(BeloClass index, BeloClass value) {
        BeloClass r = map.put(index,value);
        return r == null ? new Null() : r;
    }
    //NÄITÄ EI TODELLAKAAN OLE TARKOITUS YLIKIRJOITTAA, MUTTA OBJEKTIT OVAT POIKKEUS
    @Override
    public BeloClass classValue(BeloClass name) {
        BeloClass b = classValues.get(name);
        if (b != null) return b;
        BeloClass item = map.get(name);
        if (item != null) return item;

        return createNotAMemberOfClassError(name);
    }

    @Override
    public BeloClass setClassValue(String name, BeloClass newValue) {
        BeloString key = BeloString.create(name);
        map.put(key, newValue);
        classValues.put(key,newValue);
        return newValue;
    }
    // ------------BELOSCRIPT ANNOTATED METHODS::: -----------------
    @BeloScript
    private Obj(){

    }
    @BeloScript
    public Obj put(BeloClass key, BeloClass value) {
        this.map.put(key,value);
        return this;
    }
    @BeloScript
    public BeloClass get(BeloClass key) {
        BeloClass val = map.get(key);
        return val == null ? new Null() : val;
    }
    @BeloScript
    public boolean containsKey(BeloClass key) {
        return map.containsKey(key);
    }
    @BeloScript
    public BeloClass remove(BeloClass key) {
        return map.remove(key);
    }
    @BeloScript
    public List keys() {
        return List.create(map.keySet());
    }

    @BeloScript
    public void clear() {
        this.map.clear();
    }
    @BeloScript
    public int size() {
        return this.map.size();
    }
    @BeloScript
    public Obj extend(Obj obj) {
        for (BeloClass key : obj.map.keySet()) {
            if (!this.map.containsKey(key)) {
                this.map.put(key, obj.get(key));
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
