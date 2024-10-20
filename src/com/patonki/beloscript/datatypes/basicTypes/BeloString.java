package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.function.BeloScript;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

public class BeloString extends CustomBeloClass implements Iterable<BeloClass>{
    private final String value;

    public static BeloString create(String value) {
        return new BeloString(value).init_self();
    }
    public static BeloString create(char c) {
        return new BeloString(c).init_self();
    }

    public static BeloString create_optimized(String s) {
        return new BeloString(s);
    }
    private BeloString(String value) {
        this.value = value;
    }
    private BeloString(char c) {
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
        return BeloString.create(this.value + another.asString());
    }

    @Override
    public BeloClass multiply(BeloClass another) {
        if (!(another instanceof BeloDouble)) {
            return throwError("Can't multiply string with " + another.getClass().getSimpleName());
        }
        StringBuilder builder = new StringBuilder();
        for (int i = another.intValue(); i > 0; i--) {
            builder.append(this.value);
        }
        return BeloString.create(builder.toString());
    }


    @Override
    public BeloClass index(BeloClass index) {
        if (!(index instanceof BeloDouble)) {
            return throwError("Index must be a number");
        }
        try {
            return BeloString.create(value.charAt(index.intValue()));
        } catch (IndexOutOfBoundsException e) {
            return throwError("IndexOutOfBounds index:"+index.intValue()+" size:"+size());
        }
    }
    @BeloScript
    public int size() {
        return this.value.length();
    }
    @BeloScript
    public String upper_case() {
        return this.value.toUpperCase();
    }
    @BeloScript
    public String lower_case() {
        return this.value.toLowerCase();
    }
    @BeloScript
    public boolean contains(String s) {
        return this.value.contains(s);
    }
    @BeloScript
    public boolean matches(String regex) {
        return this.value.matches(regex);
    }
    @BeloScript
    public boolean starts_with(String s) {
        return this.value.startsWith(s);
    }
    @BeloScript
    public boolean ends_with(String s) {
        return this.value.endsWith(s);
    }
    @BeloScript
    public String trim() {
        return this.value.trim();
    }
    @BeloScript
    public String replace(String s, String s2) {
        return this.value.replace(s,s2);
    }
    @BeloScript
    public String substring(int start, int end) {
        return this.value.substring(start,end);
    }
    @BeloScript
    public String substring(int start) {
        return this.value.substring(start);
    }
    @BeloScript
    public int index_of(String s) {
        return this.value.indexOf(s);
    }
    @BeloScript
    public String replace_all(String regex, String with) {
        return this.value.replaceAll(regex,with);
    }
    @BeloScript
    public boolean isDigits() {
        for (int i = 0; i < this.value.length(); i++) {
            if (!Character.isDigit(this.value.charAt(i))) return false;
        }
        return !this.value.isEmpty();
    }
    @BeloScript
    public boolean isLetters() {
        for (int i = 0; i < this.value.length(); i++) {
            if (!Character.isLetter(this.value.charAt(i))) return false;
        }
        return !this.value.isEmpty();
    }
    @BeloScript
    public List split(String sep) {
        String[] splitted = this.value.split(sep);
        return List.create(Arrays.stream(splitted).map(BeloString::create).collect(Collectors.toList()));
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
                return BeloString.create(value.charAt(i++));
            }
        };
    }
}
