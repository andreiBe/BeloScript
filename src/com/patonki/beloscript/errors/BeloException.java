package com.patonki.beloscript.errors;

//Virhe luokka, joka annetaan, jos virhe ei liity itse BeloScript koodiin vaan esimerkiksi
//kun on kyse vääristä alku parametreista (String[] args) tai jos ajettavaa kooditiedostoa ei löydy
public class BeloException extends RuntimeException{
    public BeloException(String message) {
        super(message);
    }
}
