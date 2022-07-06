package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.function.BeloScriptFunction;
import com.patonki.beloscript.datatypes.interfaces.IterableBeloClass;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class BeloList extends BeloClass implements IterableBeloClass {
    private final ArrayList<BeloClass> list;

    private final BeloScriptFunction add = new BeloScriptFunction("list.add") {
        @Override
        public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
            list.addAll(args);
            return res.success(BeloList.this);
        }
    };
    private final BeloScriptFunction sort = new BeloScriptFunction("list.sort") {
        @Override
        public RunTimeResult execute(Context context, List<BeloClass> args, RunTimeResult res) {
            if (args.size() != 0) return throwParameterSizeError(res,context,0,args.size());
            ArrayList<BeloClass> newlist = new ArrayList<>(list);
            newlist.sort(BeloClass::compare);
            return res.success(new BeloList(newlist));
        }
    };
    public BeloList(Collection<BeloClass> list) {
        this.list = new ArrayList<>(list);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public List<BeloClass> iterableList() {
        return list;
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        switch (name.toString()) {
            case "size":
                return new BeloDouble(list.size());
            case "add":
                return this.add;
            case "sort":
                return this.sort;
        }
        return createNotAMemberOfClassError(name);
    }

    @Override
    public BeloClass index(BeloClass index) {
        if (Double.isNaN(index.doubleValue())) {
            return new BeloError(new RunTimeError(index.getStart(),index.getEnd(),
                    "Index must be a number",context));
        }
        return get((int)index.doubleValue());
    }

    @Override
    public BeloClass setIndex(BeloClass index, BeloClass value) {
        if (Double.isNaN(index.doubleValue())) {
            return new BeloError(new RunTimeError(index.getStart(),index.getEnd(),
                    "Index must be a number",context));
        }
        return this.list.set((int)index.doubleValue(),value);
    }

    protected int size() {
        return list.size();
    }
    public BeloClass get(int index) {
        return list.get(index);
    }
    public void addItem(BeloClass item) {list.add(item);}
    @Override
    public int compare(BeloClass another) {
        if (another instanceof BeloList) {
            return new ListComparator().compare(this, (BeloList) another);
        }
        return -1;
    }
    private static class ListComparator implements Comparator<BeloList> {
        @Override
        public int compare(BeloList o1, BeloList o2) {
            for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
                int c = o1.get(i).compare(o2.get(i));
                if (c != 0) {
                    return c;
                }
            }
            return Integer.compare(o1.size(), o2.size());
        }
    }
}
