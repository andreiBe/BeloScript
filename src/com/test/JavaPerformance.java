package com.test;

import java.util.ArrayList;

public class JavaPerformance {
    public static void main(String[] args) {
        long nanotime = System.nanoTime();
        ArrayList<ArrayList<ArrayList<Integer>>> lista = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            ArrayList<ArrayList<Integer>> list = new ArrayList<>();
            for (int j = 0; j < 51; j++) {
                ArrayList<Integer> numbers = new ArrayList<>();
                for (int k = 0; k < 51; k++) {
                    numbers.add(0);
                }
                list.add(numbers);
            }
            lista.add(list);
        }
        System.out.println("Took: " + (System.nanoTime()-nanotime));
    }
}
