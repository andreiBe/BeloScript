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
    public Obj put(BeloClass key, BeloClass value) {
        this.map.put(key,value);
        return this;
    }
    public BeloClass get(BeloClass key) {
        return map.get(key);
    }
    public java.util.List<BeloClass> keys() {
        return new ArrayList<>(map.keySet());
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
        map.put(new BeloString(name), newValue);
        classValues.put(new BeloString(name),newValue);
        return newValue;
    }
    // ------------BELOSCRIPT ANNOTATED METHODS::: -----------------
    @BeloScript
    private Obj(){

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
        for (BeloClass key : obj.keys()) {
            if (!this.map.containsKey(key)) {
                this.map.put(key, obj.get(key));
            }
        }
        return this;
    }
}
