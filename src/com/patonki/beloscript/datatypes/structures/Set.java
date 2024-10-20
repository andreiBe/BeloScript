package com.patonki.beloscript.datatypes.structures;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;

import java.util.Iterator;
import java.util.TreeSet;

@BeloScript
public class Set extends CustomBeloClass implements Iterable<BeloClass> {
    private final TreeSet<BeloClass> treeSet = new TreeSet<>();

    @Override
    public Iterator<BeloClass> iterator() {
        return treeSet.iterator();
    }

    @Override
    public String toString() {
        return treeSet.toString();
    }

    //-------------BeloScript annotated methods----------
    @BeloScript
    private Set() {}
    @BeloScript
    public void push(BeloClass item) {
        treeSet.add(item);
    }
    @BeloScript
    public void remove(BeloClass item) {
        treeSet.remove(item);
    }
    @BeloScript
    public int size() {
        return treeSet.size();
    }

}
