# BeloScript

Ohjelmointikieli koodattu javalla. Vielä hyvin rajoittunut ominaisuuksiltaan, mutta kehittyy koko ajan. Kielellä kirjoitettuja scriptejä voi ajaa joko
omassa java ohjelmassaan tai exe tiedoston avulla omalla koneellaan.

## Ohjeet koneella ajamiseen

1. Lataa exe tiedosto releases kohdasta githubista
2. Luo script tiedosto (tiedostopäätteellä .bel) samaan kansioon kuin exe tiedosto
3. Avaa command prompt samassa kansiossa kuin exe tiedosto
4. Syötä seuraava komento:
```BeloScript.exe <tiedoston nimi>```
Esim:
```BeloScript.exe script.bel```

## Ohjeet ajamiseen omassa Java ohjelmassa
En ole lisännyt kirjastoa maveniin, joten toimii eri taktiikalla
1. Lataa jar tiedosto releases kohdasta githubista
2. Sisällytä jar tiedosto java projektiisi
3. Käytä "BeloScript" luokkaa scriptien ajamiseen
## Kehittämiseen osallistuminen

Lähetä pull request