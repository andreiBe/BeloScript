package com.test;

import org.junit.jupiter.api.Test;

public class JavaStdTest {
    private static final String ROOT = "testScripts/javaStd";

    @Test
    void stringBuilder() {
        TestUtil.testFile(ROOT+"/stringBuilder");
    }
    @Test
    void multiThreadingMaybe() {
        TestUtil.testFile(ROOT+"/multithreadingMaybe");
    }
    @Test
    void files() {
        TestUtil.testFile(ROOT+"/files");
    }
}
