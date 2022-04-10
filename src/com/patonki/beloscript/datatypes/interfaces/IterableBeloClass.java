package com.patonki.beloscript.datatypes.interfaces;

import com.patonki.beloscript.datatypes.BeloClass;

import java.util.List;

/**
 * Rajapinta, joka mahdollistaa sen, että elementin kanssa voi käyttää for-silmukkaa.
 * Esimerkiksi {@link com.patonki.beloscript.datatypes.basicTypes.BeloList} toteuttaa tämän
 * rajapinnan.
 */
public interface IterableBeloClass {
    /**
     * @return Lista, jonka läpi iteroidaan
     */
    List<BeloClass> iterableList();
}
