package com.patonki.beloscript.builtInLibraries;

import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.datatypes.basicTypes.CustomBeloClass;
import com.patonki.beloscript.datatypes.basicTypes.List;
import com.patonki.beloscript.datatypes.basicTypes.Null;
import com.patonki.beloscript.datatypes.function.BeloScript;
import com.patonki.beloscript.datatypes.interfaces.RandomAccessCollection;
import com.patonki.beloscript.errors.BeloException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

@BeloScript
public class BeloRandom extends CustomBeloClass {
    private static final Random RNG = new Random();
    @BeloScript
    public static int rand(int start, int end) {
        return RNG.nextInt(end-start+1) + start;
    }
    @BeloScript
    public static BeloClass pick_random(RandomAccessCollection list) {
        if (list.size() == 0) return new Null();
        return list.get(rand(0, list.size()-1));
    }
    @BeloScript
    public static List pick_random(RandomAccessCollection list, int x) throws BeloException {
        HashMap<Integer, BeloClass> map = new HashMap<>();

        List result = List.create();
        int n = list.size()-1;
        if (x > n+1)
            throw new BeloException("X is larger than collection size");
        for (int i = 0; i < x; i++) {
            int randomIndex = rand(i,n);

            BeloClass mapValue = map.get(i);
            BeloClass current = mapValue != null ? mapValue : list.get(i);

            mapValue = map.get(randomIndex);
            BeloClass random = mapValue != null ? mapValue : list.get(randomIndex);

            map.put(randomIndex, current);
            result.push(random);
        }
        assert result.size() == x;

        return result;
    }

    @BeloScript
    public static void shuffle(RandomAccessCollection list) {
        ArrayList<BeloClass> all = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            all.add(list.get(i));
        }
        Collections.shuffle(all);
        for (int i = 0; i < list.size(); i++) {
            list.set(i, all.get(i));
        }
    }
}
