package com.patonki.beloscript.datatypes.function;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BeloScript {
    String[] args() default {};
}
