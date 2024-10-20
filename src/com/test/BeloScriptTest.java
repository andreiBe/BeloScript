package com.test;

import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.errors.LocalizedBeloException;
import com.patonki.beloscript.Main;
import com.patonki.beloscript.errors.BeloException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BeloScriptTest {
    private static final String ROOT = "testScripts/beloscript/";
    @SuppressWarnings("CallToPrintStackTrace")
    @Test
    void imports() throws BeloException {
        //tyhjä jar tiedosto
        Assertions.assertThrows(LocalizedBeloException.class,
                () -> BeloScript.runFile("testScripts/beloscript/importEmptyJar/script.bel"));
        try {
            BeloScript.runFile("testScripts/beloscript/imports/script.bel");
            //pitäis tulla error
            Assertions.fail("No error");
        } catch (LocalizedBeloException e) {
            e.printStackTrace();
        }
        //error toisessa tiedostossa
        Assertions.assertThrows(RuntimeException.class,
                () -> BeloScript.runFile("testScript/beloscript/imports/script2.bel"));
        try {
            BeloScript.runFile("testScripts/beloscript/imports/script2.bel");
        } catch (LocalizedBeloException e) {
            e.printStackTrace();
        }
    }
    @Test
    void reflectionLibraries() throws BeloException {
        TestUtil.testFile("testScripts/beloscript/reflectionlibs");
    }
    @Test
    void runWithDifferentOptions() throws BeloException {
        String script = "var = 9";
        BeloScript.run(script,"test.bel","");
        String script2 = "name = input(\"name?: \")";
        BeloScript.run(script2,null,"testScripts/beloscript/options/",
                "logParse","logLex", "input:input.txt", "output: output.txt",
                "json: {var: 4, list:[]}");

        Assertions.assertThrows(BeloException.class,
                () -> BeloScript.run(script, null, "", "input: notreal.txt"));

        BeloScript.run(script, null,"testScripts/beloscript/options/",
                "jsonfile:json.json");

        Assertions.assertThrows(BeloException.class,
                () ->BeloScript.run(script, null, "","jsonfile:doesnotexist.json"));

        //parameter not defined
        Assertions.assertThrows(BeloException.class, ()-> BeloScript.run(script,null,"","jsonFile: json.json"));
    }
    @Test
    void testMain() throws BeloException {
        Main.main(new String[]{"input.belo","jsonfile:test.json"});
        Main.main(new String[0]);
    }
    @Test
    void random() throws BeloException {
        TestUtil.testFile(ROOT+"random");
    }
    @Test
    void errorMessages() {
        TestUtil.testFile(ROOT+"errorMessages");
    }
    @Test
    void jsonLibrary() {
        TestUtil.testFile(ROOT+"jsonLibrary",
                "json:{\"list\":[3,2,3], \"value\":\"string\", \"l\":null, \"d\": 5.3, \"lo\":999999999999}");
    }
    @Test
    void jsonLibraryNoInput() {
        TestUtil.testFile(ROOT+"jsonLibraryNoInput");
    }
    @Test
    void jsonLibraryMalformedInput() {
        TestUtil.testFile(ROOT+"jsonLibraryMalformedInput",
                "json:{\"list\"3,2,3], \"value\":\"string\", \"l\":null}");
    }
    @Test
    void importingLibrary() {
        TestUtil.testFile(ROOT+"importingLibrary");
    }

    @Test
    void files() {
        TestUtil.testFile(ROOT+"files");
    }
    @Test
    void buildInLibraries() {
        TestUtil.testFile(ROOT+"buildInLibraries");
    }
}