package com.patonki.beloscript;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.BeloDouble;
import com.patonki.beloscript.datatypes.basicTypes.JavaClassWrapper;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.basicTypes.BeloString;

public class ImportUtil {
    public static boolean isNotValidType(Class<?> clazz) {
        if (clazz.isPrimitive()) return false;
        if (String.class.isAssignableFrom(clazz)) return false;
        return !BeloClass.class.isAssignableFrom(clazz);
    }
    public static Class<?> objectArvo(Class<?> c) {
        if (c == Integer.TYPE) return Integer.class;
        if (c == Long.TYPE) return Long.class;
        if (c == Short.TYPE) return Short.class;
        if (c == Double.TYPE) return Double.class;
        if (c == Float.TYPE) return Float.class;
        if (c == Character.TYPE) return Character.class;
        if (c == Boolean.TYPE) return Boolean.class;
        if (c == Byte.TYPE) return Byte.class;
        return c;
    }
    public static BeloClass matchingBeloClass(Object object) {
        if (object == null) return new Null();
        if (object instanceof Integer) return new BeloDouble((Integer) object);
        if (object instanceof Byte) return new BeloDouble((Byte)object);
        if (object instanceof Character) return BeloString.create(object.toString());
        if (object instanceof String) return BeloString.create(object.toString());
        if (object instanceof Long) return new BeloDouble((Long)object);
        if (object instanceof Float) return new BeloDouble((Float)object);
        if (object instanceof Double) return new BeloDouble((Double)object);
        if (object instanceof Short) return new BeloDouble((Short)object);
        if (object instanceof Boolean) return new BeloDouble((Boolean)object ? 1 : 0);
        if (object instanceof BeloClass) return (BeloClass) object;
        return null;
    }
    public static Object matchingJavaClass(Object object, Class<?> shouldBe) {
        if (object instanceof Null) return null;
        if (object instanceof BeloDouble) {
            double d = ((BeloDouble) object).doubleValue();
            if (shouldBe.equals(Integer.class) || shouldBe.equals(Integer.TYPE)) return (int) d;
            if (shouldBe.equals(Float.class) ||shouldBe.equals(Float.TYPE)) return (float)d;
            if (shouldBe.equals(Short.class) || shouldBe.equals(Short.TYPE)) return (short)d;
            if (shouldBe.equals(Long.class) || shouldBe.equals(Long.TYPE)) return (long)d;
            if (shouldBe.equals(Boolean.class) ||shouldBe.equals(Boolean.TYPE)) return d > 0;
            if (shouldBe.equals(Byte.class) || shouldBe.equals(Byte.TYPE)) return (byte)d;
            return d;
        }
        if (object instanceof BeloString) {
            if (shouldBe.equals(Character.class) || shouldBe.equals(Character.TYPE)) {
                if (object.toString().isEmpty()) return '\0';
                return object.toString().charAt(0);
            }
            return object.toString();
        }
        if (object instanceof JavaClassWrapper) {
            return ((JavaClassWrapper) object).getWrappedObject();
        }
        return null;
    }
}
