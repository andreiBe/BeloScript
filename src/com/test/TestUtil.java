package com.test;

import com.patonki.beloscript.BeloScript;
import com.patonki.beloscript.errors.BeloException;
import com.patonki.helper.FileHandler;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestUtil {
    public static void testFile(String root, String... extraArgs) {
        testFile(false, root, extraArgs);
    }
    @SuppressWarnings({"CallToPrintStackTrace", "ThrowableNotThrown"})
    public static void testFile(boolean throwError, String root, String... extraArgs) {
        List<String> args = new ArrayList<>();
        args.add("output: output.txt");
        Collections.addAll(args,extraArgs);

        BeloScript script = new BeloScript();
        long now = System.currentTimeMillis();
        try {
            script.executeFile(root+"\\script.bel", args.toArray(new String[0]));
        } catch (BeloException e) {
            if (throwError) throw e;
            e.printStackTrace();
            Assertions.fail();
        }
        System.out.println("Execution took: "+(System.currentTimeMillis()-now)+" millis");
        FileHandler output = new FileHandler(root+"/output.txt");
        FileHandler correctOutput = new FileHandler(root+"/accepted.txt");
        if (correctOutput.exists()) {
            String correct = correctOutput.currentContent();
            if (correct.equals("#ignore")) return;
            assertEquals(correct,output.currentContent());
        } else {
            correctOutput.write("#ignore");
        }
    }
}
