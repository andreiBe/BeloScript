package com.patonki.beloscript;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws BeloScriptException {
        if (args.length > 0) {
            String filename = args[0];
            String[] otherParameters = Arrays.copyOfRange(args,1,args.length);
            BeloScript.runFile(filename, otherParameters);
        }
    }
}
