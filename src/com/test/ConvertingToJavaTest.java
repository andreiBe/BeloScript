package com.test;

import com.patonki.beloscript.BeloScript;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class ConvertingToJavaTest {
    @SuppressWarnings("DataFlowIssue")
    @Test
    void basic() {
        File testScripts = new File("testScripts");
        for (File testFolders : testScripts.listFiles()) {
            for (File testFolder : testFolders.listFiles()) {
                System.out.println("Looking at " + testFolder.getName());
                File[] files = testFolder.listFiles();
                Optional<File> beloscriptFile = Arrays.stream(files).filter(f -> f.getName().equals("script.bel")).findFirst();
                if (!beloscriptFile.isPresent()) {
                    continue;
                }
                System.out.println("Converting");
                File beloScriptFile = beloscriptFile.get();
                String result = BeloScript.convertFileToJavaCode(beloScriptFile.getPath());
                System.out.println("Result: " + result);
            }
        }
    }
}
