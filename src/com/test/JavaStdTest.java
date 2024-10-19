package com.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

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

        try {
            URL url = new URL("http", "", 4, "");
            File file = new File(url.toURI());

        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
