package com.patonki.beloscript.datatypes.basicTypes;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;

/**
 * BeloScriptin double luku arvo
 */
public class BeloDouble extends BeloClass {
    private final double value;
    public static final BeloDouble FALSE = new BeloDouble(0);
    public static final BeloDouble TRUE = new BeloDouble(1);
    public BeloDouble(double value) {
        this.value = value;
    }
    public BeloDouble(boolean b) {
        this.value = b ? 1 : 0;
    }

    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public boolean isTrue() {
        return this.value > 0;
    }

    @Override
    public String toString() {
        if (value >= 10e6 && value <= Long.MAX_VALUE && value % 1 == 0) { //muuten double käyttää E syntaksia
            long lo = (long) value;
            return String.valueOf(lo);
        }
        String val = String.valueOf(value);
        //otetaan desimaali nolla pois
        if (val.endsWith(".0")) val = val.substring(0,val.length()-2);
        return val;
    }
    @Override
    public int compare(BeloClass another) {
        double d = another.doubleValue();
        if (Double.isNaN(d)) return -1;
        return Double.compare(value, d);
    }

    @Override
    public BeloClass not() {
        if (value > 0) {
            return new BeloDouble(0);
        }
        return new BeloDouble(1);
    }

    @Override
    public BeloClass power(BeloClass another) {
        return new BeloDouble(Math.pow(value,another.doubleValue()));
    }

    @Override
    public BeloClass multiply(BeloClass another) {
        return new BeloDouble(value * another.doubleValue());
    }

    @Override
    public BeloClass divide(BeloClass another) {
        if (another.doubleValue() == 0) {
            return new BeloError(new RunTimeError(another.getStart(),another.getEnd(), "Division by zero",context));
        }
        return new BeloDouble(value / another.doubleValue());
    }

    @Override
    public BeloClass add(BeloClass another) {
        double d = another.doubleValue();
        //double arvo ei määritelty
        if (!Double.isNaN(d)) {
            return new BeloDouble(value + another.doubleValue());
        } else if (another instanceof BeloString){
            return BeloString.create(this + another.toString());
        }
        else return new BeloError(new RunTimeError(getStart(),another.getEnd(),
                    "Can't add double to "+another.getClass().getSimpleName(),
                    context));
    }
    public static BeloDouble createFromBoolean(boolean b) {
        return b ? TRUE : FALSE;
    }
    @Override
    public BeloClass substract(BeloClass another) {
        if (Double.isNaN(another.doubleValue())) {
                    return new BeloError(new RunTimeError(getStart(),another.getEnd(),
                    "Can't substract " +another.getClass().getSimpleName()+" from double",
                    context));
        }
        return new BeloDouble(value - another.doubleValue());
    }
    @Override
    public BeloClass remainder(BeloClass another) {
        return new BeloDouble(value % another.doubleValue());
    }

    @Override
    public BeloClass intdiv(BeloClass another) {
        return new BeloDouble((int)value / (int)another.doubleValue());
    }

    @Override
    public BeloClass prePlus() {
        return new BeloDouble(this.value+1);
    }

    @Override
    public BeloClass preMinus() {
        return new BeloDouble(this.value-1);
    }

    @Override
    public BeloClass postPlus() {
        return new BeloDouble(this.value+1);
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public BeloClass postMinus() {
        return new BeloDouble(this.value-1);
    }

    @Override
    public BeloClass copy() {
        return new BeloDouble(this.value);
    }
}
