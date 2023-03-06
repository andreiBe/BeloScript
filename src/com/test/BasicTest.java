package com.test;

import com.patonki.beloscript.BeloScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicTest {
    private BeloScript script;
    private final String root = "testScripts/basicTests";
    @BeforeEach
    void reset() {
        script = new BeloScript();
    }
    @Test
    void basicBinaryOperations() {
        TestUtil.testFile(root+"/binaryOperators");
    }
    @Test
    void theworstbug() {
        TestUtil.testFile("documentation/hirsipuu/fuck");
        System.out.println();
        System.out.println();
        System.out.println();
        TestUtil.testFile("documentation/hirsipuu/fuck2");
    }
    @Test
    void forLoops() {
        TestUtil.testFile(root+"/forLoops");
    }

    @Test
    void variables() {
        TestUtil.testFile(root+"/variables");
    }
    @Test
    void errors() {
        TestUtil.testFile(root+"/errors");
    }
    @Test
    void list() {
        TestUtil.testFile(root+"/list");
    }
    @Test
    void tryCatch() {
        TestUtil.testFile(root+"/try");
    }
    @Test
    void functions() {
        TestUtil.testFile(root+"/functions");
    }
    @Test
    void ifs() {
        TestUtil.testFile(root+"/if");
    }
    @Test
    void object() {
        TestUtil.testFile(root+"/object");
    }
    @Test
    void exportImport() {
        TestUtil.testFile(root+"/exportImport");
    }
}
