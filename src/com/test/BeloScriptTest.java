package com.test;

import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.Main;
import com.patonki.beloscript.errors.BeloException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeloScriptTest {
    private static final String ROOT = "testScripts/beloscript/";
    @Test
    void imports() throws BeloException {
        //tyhjä jar tiedosto
        Assertions.assertThrows(BeloScriptException.class,
                () -> BeloScript.runFile("testScripts/beloscript/importEmptyJar/script.bel"));
        try {
            BeloScript.runFile("testScripts/beloscript/imports/script.bel");
            //pitäis tulla error
            Assertions.fail("No error");
        } catch (BeloScriptException e) {
            e.printStackTrace();
        }
        //error toisessa tiedostossa
        Assertions.assertThrows(RuntimeException.class,
                () -> BeloScript.runFile("testScript/beloscript/imports/script2.bel"));
        try {
            BeloScript.runFile("testScripts/beloscript/imports/script2.bel");
        } catch (BeloScriptException e) {
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
    void main() throws BeloException {
        Main.main(new String[]{"input.belo","jsonfile:test.json"});
    }
    @Test
    void random() throws BeloException {
        TestUtil.testFile(ROOT+"random");
    }
}