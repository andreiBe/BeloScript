package com.test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class JavaPerformance {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        try {
            list.sort((o1, o2) -> {
                if (new Random().nextInt(10) == 4) throw new NullPointerException();
                return o1-o2;
            });
        } catch (NullPointerException e) {
            System.out.println(list);
        }
    }
}
