package com.test;

import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.BeloScriptException;
import com.patonki.beloscript.Main;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeloScriptTest {

    @Test
    void imports() {
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
    void runWithDifferentOptions() throws BeloScriptException {
        String script = "var = 9";
        BeloScript.run(script,"test.bel","");
        String script2 = "name = input(\"name?: \")";
        BeloScript.run(script2,null,"testScripts/beloscript/options/",
                "logParse","logLex", "input:input.txt", "output: output.txt",
                "json: {var: 4, list:[]}");

        Assertions.assertThrows(BeloScriptException.class,
                () -> BeloScript.run(script, null, "", "input: notreal.txt"));

        BeloScript.run(script, null,"testScripts/beloscript/options/",
                "jsonfile:json.json");

        Assertions.assertThrows(BeloScriptException.class,
                () ->BeloScript.run(script, null, "","jsonfile:doesnotexist.json"));

        //parameter not defined
        Assertions.assertThrows(BeloScriptException.class, ()-> BeloScript.run(script,null,"","jsonFile: json.json"));
    }
    @Test
    void main() throws BeloScriptException {
        Main.main(new String[]{"input.belo","jsonfile:test.json"});
    }
}