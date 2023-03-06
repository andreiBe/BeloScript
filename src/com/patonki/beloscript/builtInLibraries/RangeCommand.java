package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;

import java.util.Iterator;
class RangeIterable extends BeloClass implements Iterable<BeloClass> {
    int start;
    int end;
    int step;

    public RangeIterable(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
    }

    @Override
    public Iterator<BeloClass> iterator() {
        return new RangeIterator(start,end,step);
    }
}
class RangeIterator extends BeloClass implements Iterator<BeloClass> {
    int start;
    int end;
    int step;
    int i;

    public RangeIterator(int start, int end, int step) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.i = start;
    }

    @Override
    public boolean hasNext() {
        return i < end;
    }

    @Override
    public BeloClass next() {
        int copy = i;
        i += step;
        return new BeloDouble(copy);
    }
}
@BeloScript
public class RangeCommand extends CustomBeloClass {
    @BeloScript
    public static RangeIterable range(int start, int end, int step) {
        return new RangeIterable(start, end, step);
    }
    @BeloScript
    public static RangeIterable range(int start, int end) {
        return new RangeIterable(start,end, 1);
    }
    @BeloScript
    public static RangeIterable range(int end) {
        return new RangeIterable(0,end, 1);
    }

}
