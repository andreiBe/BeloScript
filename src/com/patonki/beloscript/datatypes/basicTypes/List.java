package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BaseFunction;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.interfaces.RandomAccessCollection;
import com.patonki.beloscript.errors.BeloException;

import java.util.*;

@BeloScript
public class List extends CustomBeloClass implements RandomAccessCollection, Iterable<BeloClass>{
    private ArrayList<BeloClass> list;

    public static List create(Collection<BeloClass> list) throws BeloException {
        List o = new List();
        o.list = new ArrayList<>(list);
        o.init_self();
        return o;
    }
    public static List create() throws BeloException{
        return new List().init_self();
    }
    // ------------BELOSCRIPT ANNOTATED METHODS::: -----------------
    @BeloScript
    private List() {
        this.list = new ArrayList<>();
    }
    @BeloScript
    private List(Iterable<BeloClass>iterable) {
        this.list = new ArrayList<>();
        for (BeloClass beloClass : iterable) {
            this.list.add(beloClass);
        }
    }
    @BeloScript
    public void push(BeloClass item) {
        this.list.add(item);
    }

    @BeloScript
    public void sort(BaseFunction f) {
        this.list.sort((o1, o2) -> {
            BeloClass c = f.run(new BeloClass[]{o1,o2});
            return c.intValue();
        });
    }
    @BeloScript
    public void sort() {
        this.list.sort(BeloClass::compare);
    }
    @BeloScript
    public void insert(int index, BeloClass item) throws BeloException {
        handleIndexOutOfBounds(() -> this.list.add(index,item),index);
    }
    @BeloScript
    public List shallow_copy() throws BeloException {
        return create(this.list);
        //return new List(this.list);
    }
    @BeloScript
    @Override
    public int size() {
        return list.size();
    }
    @BeloScript
    public boolean is_empty() {
        return list.isEmpty();
    }
    @BeloScript
    public int index_of(BeloClass value) {
        for (int i = 0; i < list.size(); i++) {
            BeloClass item = list.get(i);
            if (item.compare(value) == 0) return i;
        }
        return -1;
    }
    @BeloScript
    public boolean contains(BeloClass value) {
        return index_of(value) != -1;
    }
    @BeloScript
    public List sub_list(int from, int to) throws BeloException {
        return handleIndexOutOfBounds(() -> create(this.list.subList(from,to)), from,to);
    }
    @BeloScript
    public void clear() {
        this.list.clear();
    }
    @BeloScript
    public void push_all(Iterable<BeloClass> iterable) {
        for (BeloClass beloClass : iterable) {
            this.list.add(beloClass);
        }
    }
    @BeloScript
    public BeloClass remove_index(int index) throws BeloException {
        return handleIndexOutOfBounds(() -> this.list.remove(index),index);
    }
    @BeloScript
    public boolean remove(BeloClass value) throws BeloException {
        int index = index_of(value);
        if (index == -1) return false;
        remove_index(index);
        return true;
    }

    @Override
    public Iterator<BeloClass> iterator() {
        return this.list.iterator();
    }

    interface SupplierWithError<T> {
        T get() throws BeloException;
    }
    //-------------------------------OTHER METHODS-------------
    private<T> T handleIndexOutOfBounds(SupplierWithError<T> runnable, int... index) throws BeloException {
        try {
            return runnable.get();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BeloException("ArrayIndexOutOfBounds indexes:"+ Arrays.toString(index) +" size:"+size());
        }
    }
    private void handleIndexOutOfBounds(Runnable runnable, int... index) {
        try {
            runnable.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new BeloException("ArrayIndexOutOfBounds indexes:"+ Arrays.toString(index) +" size:"+size());
        }
    }
    @Override
    public BeloClass add(BeloClass another) {
        try {
            Iterable<BeloClass> it = (Iterable<BeloClass>) another;
            List newlist = create(list);
            newlist.push_all(it);
            return newlist;
        } catch (ClassCastException e) {
            return throwError("Can't add list with " + another.getClass().getSimpleName());
        }
    }
    @Override
    public BeloClass multiply(BeloClass another) {
        if (!another.isNumber()) {
            return throwError("Wrong type, should be number but was " + another.getTypeName());
        }
        int times = another.intValue();
        if (times < 0) return throwError("Multiplying number should not be negative!");
        List newlist = new List();
        while (times-- > 0) {
            for (BeloClass item : list) {
                newlist.push(item);
            }
        }
        return newlist;
    }
    @Override
    public BeloClass index(BeloClass index) {
        if (!index.isNumber()) {
            return throwError("Index must be a number but was " + index.getTypeName());
        }

        try {
            return get(index.intValue());
        } catch (IndexOutOfBoundsException e) {
            return throwError("IndexOutOfBounds index:"+index.intValue()+" size:"+size());
        }
    }

    @Override
    public BeloClass setIndex(BeloClass index, BeloClass value) {
        if (!index.isNumber()) {
            return throwError("Index must be a number but was " + index.getTypeName());
        }
        try {
            set(index.intValue(),value);
            return value;
        } catch (IndexOutOfBoundsException e) {
            return throwError("IndexOutOfBounds index:"+index.intValue()+" size:"+size());
        }
    }
    @Override
    public int compare(BeloClass another) {
        if (another instanceof List) {
            return compareWithAnotherBeloList((List) another);
        }
        return -1;
    }
    private int compareWithAnotherBeloList(List another) {
        for (int i = 0; i < Math.min(this.size(), another.size()); i++) {
            int c = this.get(i).compare(another.get(i));
            if (c != 0) {
                return c;
            }
        }
        return Integer.compare(this.size(), another.size());
    }

    @Override
    public BeloClass get(int index) {
        return list.get(index);
    }

    @Override
    public void set(int index, BeloClass value) {
        list.set(index,value);
    }


    @Override
    public String toString() {
        return list.toString();
    }
}
