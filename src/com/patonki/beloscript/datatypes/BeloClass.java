package com.patonki.beloscript.datatypes;

import com.patonki.beloscript.Position;
import com.patonki.beloscript.datatypes.basicTypes.BeloError;
import com.patonki.beloscript.errors.RunTimeError;
import com.patonki.beloscript.interpreter.Context;
import com.patonki.beloscript.interpreter.RunTimeResult;

import java.util.List;

/**
 * Kaikki beloscriptin arvot perivät tämän luokan jopa numerot.
 */
public abstract class BeloClass implements Comparable<BeloClass>{
    private Position start;
    private Position end;
    private RunTimeError error;
    protected Context context;

    public BeloClass(RunTimeError error) {
        this.error = error;
    }
    public BeloClass() {

    }

    /**
     * Tämä funktiota saa kutsua vain BeloScript sisäisesti
     * @param context Context, eli osoiteavaruus, josta arvo löytyy
     * @return palauttaa itsensä
     */
    public BeloClass setContext(Context context) {
        //TODO CHANGED
        this.context = context;
        return this;
    }

    public Context getContext() {
        return context;
    }

    /**
     * Tämän funktion totetutuksen hoitaa beloscript sisäisesti.
     * @param start Kohta tiedostosta, josta arvo alkaa
     * @param end Kohta tiedostosta, jossa arvo loppuu
     * @return palauttaa itsensä
     */
    public final BeloClass setPos(Position start, Position end) {
        if (start == null || end == null)
            throw new IllegalArgumentException("Positions should not be null! class: " +getClass().getSimpleName() +" "+start+" "+end);
        this.start = start;
        this.end = end;
        return this;
    }

    /**
     * @return Palauttaa mahdollisen errorin
     */
    public final RunTimeError getError() {
        return error;
    }

    /**
     * @return Jos sisältää virheen palauttaa true
     */
    public final boolean hasError() {
        return error != null;
    }

    /**
     * @return Kohta, josta arvo alkaa tiedostossa. Esim rivi:3 indeksi: 7
     */
    public final Position getStart() {
        return start;
    }
    /**
     * @return Kohta, jossa arvo loppuu tiedostossa. Esim rivi:3 indeksi: 15
     */
    public final Position getEnd() {
        return end;
    }

    /**
     * Käytetään kun arvoa vertaillaan yksin esim:<br>
     * <code>
     *     if (arvo) {
     *
     *     }
     * </code>
     * @return true, jos tosi
     */
    public boolean isTrue() {
        return true;
    }

    /**
     * Palauttaa kopion arvosta. BeloScript käyttää tätä funktiota
     * esimerkiksi silloin kun käytetään '++' operaattoria
     * @return kopio arvosta
     */
    public BeloClass copy() {
        return this;
    }

    /**
     * Lähinnä optimointi tarkoituksessa tehty, jotta laskeminen olisi nopeampaa.
     * Tätä metodia ei kannata ylikirjoittaa.
     * @return double arvo
     */
    public double doubleValue() {
        return Double.NaN;
    }
    public int intValue() {return (int)doubleValue();}
    public boolean isNumber() {return false;}
    public String getTypeName() {return this.getClass().getSimpleName();}
    /**
     * Toimii samoin kuin vertailu funktiot yleensä javassa.<br>
     * negatiivinen tarkoittaa, että tämä tulee ennen toista<br>
     * 0 tarkoittaa, että arvot ovat saman arvoiset<br>
     * positiivinen tarkoittaa, että tämä tulee toisen jälkeen
     * @param another toinen
     * @return negatiivinen, nolla tai positiivinen
     */
    public int compare(BeloClass another) {
        return 0;
    }
    @Override
    public int compareTo(BeloClass o) {
        return compare(o);
    }

    /**
     * Apu metodi, jolla voi helposti palauttaa virheen
     * @param message Virhe viesti
     * @param start Virheen sijainti
     * @param end Virheen sijainti
     * @return virhe
     */
    protected BeloError throwError(String message, Position start, Position end) {
        return new BeloError(new RunTimeError(start,end
                ,message + " (class:"+this.getClass().getSimpleName()+")"
                ,this.context));
    }

    /**
     * Apu metodi virheen luomiseen. Palauttaa virheen, jonka sijainti on sama kuin itse arvon.
     * @param message Virhe viesti
     * @return virhe
     */
    protected BeloError throwError(String message) {
        return throwError(message,getStart(),getEnd());
    }

    /**
     * Apumetodi virheen luomiseksi. Luo virheen, joka kertoo, että luokalla ei ole kyseistä muuttujaa.
     * <br> Virhe saattaisi ilmaantua esim. seuraavassa tilanteessa: <code>[].eiolemassa = 9</code>
     * @param name Muuttujan nimi mihin yritettiin päästä käsiksi
     * @return virhe
     */
    protected BeloError createNotAMemberOfClassError(BeloClass name) {
        return new BeloError(new RunTimeError(name.getStart(),name.getEnd(),
                "Not a member of class: '"+ name+"' class: "+this.getClass().getSimpleName(),this.context));
    }
    public BeloClass add(BeloClass another) {
        return throwError("'+' operator not defined ");
    }
    public BeloClass substract(BeloClass another) {
        return throwError("'-' operator not defined ");
    }
    public BeloClass multiply(BeloClass another) {
        return throwError("'*' operator not defined ");
    }
    public BeloClass divide(BeloClass another) {
        return throwError("'/' operator not defined ");
    }
    public BeloClass remainder(BeloClass another) {
        return throwError("'%' operator not defined ");
    }
    public BeloClass intdiv(BeloClass another) {
        return throwError("'//' operator not defined ");
    }
    public BeloClass power(BeloClass another) {
        return throwError("'^' operator not defined ");
    }

    /**
     * Kutsutaan, kun arvolta yritetään saada arvo [] merkkejä käyttämällä esim:<br>
     * <code>list[4]</code> Palauttaa arvon, joka kyseisestä indeksistä löytyy. Indeksin ei tarvitse
     * olla numero
     * @param index indeksi
     * @return arvo, joka indeksistä löytyy
     */
    public BeloClass index(BeloClass index) {
        return throwError("'[ ]' operator not defined ");
    }

    /**
     * Kutsutaan, kun arvolle yritetään asettaa arvoa tiettyyn indeksiin [] merkkien avulla esim:<br>
     * <code>list[9] = 3</code> Palauttaa uuden arvon, joka indeksistä löytyy asettamisen jälkeen.
     * Indeksin ei tarvitse olla numero.
     * @param index indeksi
     * @param value uusi arvo
     * @return uusi arvo
     */
    public BeloClass setIndex(BeloClass index, BeloClass value) {
        return throwError("Setting elements with '[]' not supported");
    }


    /**
     * Kutsutaan, kun yritetään päästä käsiksi luokan muuttujaan '.' symboolin avulla esim:
     * <br><code>muuttuja = henkilo.ika</code>
     * @param name muuttujan nimi. Voi olettaa, että kyseessä on merkkijono tyyppinen olio
     * @return arvo, joka muuttujaan on tallennettu. Voi olla funktio tai muu arvo
     */
    public BeloClass classValue(BeloClass name) {
        return throwError("Cannot access class members");
    }

    /**
     * Kutsutaan, kun yritetään muuttaa luokan sisäistä muuttujaa '.' symboolin avulla esim:
     * <br><code>henkilo.nimi = "Aarne"</code>
     * @param name muuttujan nimi. Voi olettaa, että kyseessä on merkkijono olio
     * @param newValue uusi arvo
     * @return uusi arvo
     */
    public BeloClass setClassValue(String name, BeloClass newValue) {
        return  throwError("Cannot set class member: '"+name+"' ");
    }

    /**
     * Kutsutaan, kun yritetään kutsuta muuttujaa sulkujen avulla kuin funktiota. Esim:
     * <br> <code>funktio(nimi)</code> Palauttaa RunTimeResultin {@link RunTimeResult}
     * @param args Parametrit
     * @return palautus arvo
     */
    public RunTimeResult execute(List<BeloClass> args) {
        return new RunTimeResult().failure(throwError("This class isn't callable").getError());
    }

    /**
     * Kutsutaan, kun käytetään ++ operaattoria ennen muuttujaa. Esim:<br>
     * <code>++muuttuja</code>
     * @return arvo, joka muuttujalla on ++ operaation jälkeen
     */
    public BeloClass prePlus() {
        return throwError("'++' operator not defined ");
    }
    /**
     * Kutsutaan, kun käytetään -- operaattoria ennen muuttujaa. Esim:<br>
     * <code>--muuttuja</code>
     * @return arvo, joka muuttujalla on -- operaation jälkeen
     */
    public BeloClass preMinus() {
        return throwError("'--' operator not defined ");
    }
    /**
     * Kutsutaan, kun käytetään ++ operaattoria muuttujan jälkeen Esim:<br>
     * <code>muuttuja++</code>
     * @return arvo, joka muuttujalla on ++ operaation jälkeen
     */
    public BeloClass postPlus() {
        return throwError("'++' operator not defined ");
    }
    /**
     * Kutsutaan, kun käytetään -- operaattoria muuttujan jälkeen Esim:<br>
     * <code>muuttuja--</code>
     * @return arvo, joka muuttujalla on -- operaation jälkeen
     */
    public BeloClass postMinus() {
        return throwError("'--' operator not defined ");
    }

    /**
     * Kutsutaan, kun arvon edellä on 'not'.  Esim:
     * <br> <code>if (not true)</code>
     * @return arvo, jonka not aiheuttaa
     */
    public BeloClass not() {
        return throwError("'not' operator not defined ");
    }
}
