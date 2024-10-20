package com.patonki.beloscript;

import com.patonki.beloscript.errors.BeloException;
import com.patonki.beloscript.interpreter.Settings;

import java.util.Arrays;

public class Main {
    private Main() {
        
    }
    public static void main(String[] args) throws BeloException {
        if (args.length > 0) {
            String filename = args[0];
            String[] otherParameters = Arrays.copyOfRange(args,1,args.length);
            BeloScript.runFile(filename, otherParameters);

            Settings.close();
        }
    }
}
