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
    @Test
    void classes() {
        TestUtil.testFile(ROOT+"class");
    }
    @Test
    void enums() {
        TestUtil.testFile(ROOT+"enum");
    }
    @Test
    void beloscriptCompiler() {
        TestUtil.testFile(ROOT+"beloscriptCompiler");
    }
    @Test
    void advancedListAndMap() {
        TestUtil.testFile(ROOT+"advancedListAndMap");
    }
    @Test
    void inheritance() {
        TestUtil.testFile(ROOT+"inheritance");
    }
    @Test
    void staticProperties() {
        TestUtil.testFile(ROOT+"/staticProperties");
    }
    @Test
    void accessModifiers() {
        TestUtil.testFile(ROOT+"/accessModifiers");
    }
    @Test
    void finalVariables() {
        TestUtil.testFile(ROOT+"/finalVariables");
    }
    @Test
    void finalClassProperties() {
        TestUtil.testFile(ROOT+"/finalClassProperties");
    }
    @Test
    void superConstructor() {
        TestUtil.testFile(ROOT+"/superConstructor");
    }
}

