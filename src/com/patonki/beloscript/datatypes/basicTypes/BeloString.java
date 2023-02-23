package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BeloString extends BeloClass implements Iterable<BeloClass>{
    private final String value;

    public BeloString(String value) {
        this.value = value;
    }
    public BeloString(char c) {
        this.value = c+"";
    }

    @Override
    public int compare(BeloClass another) {
        if (another instanceof BeloString) {
            return value.compareTo(another.toString());
        }
        return -1;
    }

    @Override
    public BeloClass add(BeloClass another) {
        return new BeloString(this.value + another.toString());
    }

    @Override
    public BeloClass multiply(BeloClass another) {
        if (Double.isNaN(another.doubleValue())) {
            return new BeloError(new RunTimeError(getStart(),another.getEnd(),"Can't multiply string with "+
                    another.getClass().getSimpleName(),context));
        }
        StringBuilder builder = new StringBuilder();
        for (int i = (int) another.doubleValue(); i > 0; i--) {
            builder.append(this.value);
        }
        return new BeloString(builder.toString());
    }

    @Override
    public BeloClass index(BeloClass index) {
        if (Double.isNaN(index.doubleValue())) {
            return new BeloError(new RunTimeError(index.getStart(),index.getEnd(),
                    "Index must be a number",context));
        }
        return new BeloString(String.valueOf(value.charAt((int)index.doubleValue())));
    }

    @Override
    public BeloClass classValue(BeloClass name) {
        switch (name.toString()) {
            case "length":
                return new BeloDouble(this.value.length());
            case "capitalized":
                return new BeloString(value.substring(0,1).toUpperCase()+value.substring(1));
            case "reversed":
                return new BeloString(new StringBuilder(value).reverse().toString());
        }
        return createNotAMemberOfClassError(name);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeloString that = (BeloString) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public Iterator<BeloClass> iterator() {
        return new Iterator<BeloClass>() {
            private int i = 0;
            @Override
            public boolean hasNext() {
                return i < value.length();
            }

            @Override
            public BeloClass next() {
                return new BeloString(value.charAt(i++));
            }
        };
    }
}
