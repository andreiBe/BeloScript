package com.test;

import org.junit.jupiter.api.Test;

public class JavaStdTest {
    private static final String ROOT = "testScripts/javaStd";

    @Test
    void stringBuilder() {
        TestUtil.testFile(ROOT+"/stringBuilder");
    }
}
