package com.test;

import org.junit.jupiter.api.Test;

public class ExercisesTest {
    private static final String ROOT = "testScripts/exercises/";
    @Test
    void kysyNimiJaIka() {
        TestUtil.testFile(ROOT+"nimiJaIka");
    }
    @Test
    void positiiviset() {
        TestUtil.testFile(ROOT+"positiiviset");
    }
    @Test
    void invertMap() {
        TestUtil.testFile(ROOT+"invertMap");
    }
    @Test
    void set() {
        TestUtil.testFile(ROOT+"set");
    }
    @Test
    void merkkijonot() {
        TestUtil.testFile(ROOT+"merkkijonot", "input:input.txt");
    }
    @Test
    void oop() {
        TestUtil.testFile(ROOT+"oop");
    }
}
