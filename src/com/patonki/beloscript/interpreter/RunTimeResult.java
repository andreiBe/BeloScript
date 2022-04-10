package com.patonki.beloscript.interpreter;


import com.patonki.beloscript.datatypes.BeloClass;
import com.patonki.beloscript.errors.RunTimeError;

/**
 * Objekti, jonka moni operaatio palauttaa BeloScriptissä. Mahdollistaa virheiden tuottamisen
 * , break komennot, continue komennot ja arvojen palauttamisen. Kun funktion kuuluu palauttaa
 * RunTimeResult, funktio kutsuu joko success() tai failure() metodia ennen palauttamista viestiäkseen
 * lopputulosta.
 */
public class RunTimeResult {
    private BeloClass value;
    private RunTimeError error;
    private BeloClass functionReturnValue;
    private boolean shouldBreak;
    private boolean shouldContinue;

    public BeloClass register(RunTimeResult res) {
        this.error = res.error;
        this.functionReturnValue = res.functionReturnValue;
        this.shouldBreak = res.shouldBreak;
        this.shouldContinue = res.shouldContinue;
        return res.value;
    }

    private void reset() {
        this.value = null;
        this.error = null;
        this.functionReturnValue = null;
        this.shouldContinue = false;
        this.shouldBreak = false;
    }

    /**
     * Kutsutaan, kun virheitä ei syntynyt ja parametrina annetaan palautettava arvo
     * Suositeltu käyttö funktiossa, joka palauttaa RunTimeResultin:
     * <br><code>return res.success(arvo)</code>, jossa res on RunTimeResult olio
     * @param value palautettava arvo
     * @return palauttaa itsensä
     */
    public RunTimeResult success(BeloClass value) {
        this.reset();
        this.value = value;
        return this;
    }

    public RunTimeResult successReturn(BeloClass value) {
        this.reset();
        this.functionReturnValue = value;
        return this;
    }

    public RunTimeResult successContinue() {
        this.reset();
        this.shouldContinue = true;
        return this;
    }

    public RunTimeResult successBreak() {
        this.reset();
        this.shouldBreak = true;
        return this;
    }

    /**
     * Kutsutaan, kun virhe syntyy. Parametrina annetaan virhe.
     * Suositeltu käyttö funktiossa, joka palauttaa RunTimeResultin:
     * <br><code>return res.failure(virhe)</code>, jossa res on RunTimeResult olio
     * @param error virhe
     * @return palauttaa itsensä
     */
    public RunTimeResult failure(RunTimeError error) {
        this.reset();
        this.error = error;
        return this;
    }

    public boolean shouldReturn() {
        return hasError() || functionReturnValue != null
                || shouldContinue || shouldBreak;
    }

    public BeloClass getFunctionReturnValue() {
        return functionReturnValue;
    }

    public boolean isShouldBreak() {
        return shouldBreak;
    }

    public boolean isShouldContinue() {
        return shouldContinue;
    }

    public RunTimeError getError() {
        return error;
    }

    public boolean hasError() {
        return error != null;
    }
}
