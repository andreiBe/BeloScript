# Tutorial
Tutoriaali olettaa, että osaat jo ohjelmoida. En jaksa tehdä mitään parempaa ohjetta.
## Perus asiat:
```
#kommentti
#Samalla rivillä ';' merkin avulla
i = 5; i+=2
print("Hello World")
if (something) {
    doSomething()
}
if (something) doSomething()
elif (something2) {
    print("Kaiken voi joko laittaa samalle riville tai käyttää hakasulkeita")
}
else print("jou")

while (true) {
    print("Amazing")
    break
}
for (i = 0 : i < 9 : i++) {
    print(i)
}
lista = [1,2,3]
for (item in lista) {
    print(item)
}
function hello(param1, param2) {
    print("Hello " + param1 + " and " + param2)
}
hello("Jaakko", "Pete")
```
##Operaattorit
* Laskeminen: +, -, *, /, //, ^
* Vertailu: ==, <, >, <=, >=, !=, and, or, not

##Tietorakenteet
### Lista
```
lista = [3,2,1]
lista.push(9)
lista.sort() # [1, 2, 3, 9]
lista.sort(function(a1,a2) {
    return a2-a1
}) # [9, 3, 2, 1]
lista.sort(function(a1,a2) -> a1-a2) # [1, 2, 3, 9]
lista.insert(3, 4) # [1, 2, 3, 4, 9]
lista2 = lista.shallow_copy()
print(lista.size()) # 5
print(lista.is_empty()) #0
print(lista.index_of(9)) #4
print(lista.contains(5)) #0
sub = lista.sub_list(1, 3) # [2,3,4]
lista.push_all([1,2,3]) # [1, 2, 3, 4, 9, 1, 2, 3]
lista.remove_index(0) # [2, 3, 4, 9, 1, 2, 3]
lista.remove(2) # [3, 4, 9, 1, 2, 3]
lista.clear()

lista3 = List(lista)
lista3 += [1,2] # [1, 2]
lista3 *= 2 # [1, 2, 1, 2]
```
###Objekti
```
obj = {}
#kaikki tekee saman asian:
obj.ika = 9; obj["ika"] = 9; obj.put("ika", 9)
#arvon hakeminen vastaavasti
obj.ika; obj["ika"]; obj.get("ika")
print(obj.containsKey("ika")) #1
obj.remove("ika")
avaimetLista = obj.keys()
obj.clear()
print(obj.size())

#self avainsana (viittaa itseensä):
obj = {
    "nimi": "Janne",
    "printtaaNimi": function() {
        print("Minun nimeni on " + self.nimi)
    }
} 
#extend funktio
obj.extend({"osoite": "Suomi Finland"})
#nyt obj sisältää osoite avaimen
```
###Merkkijono
```
str = "makkarakeitto"
str += 6 # makkarakeitto6
str *= 2 #makkarakeitto6makkarakeitto6
str[1] #a
str.size()
str = str.upper_case()
str = str.lower_case()
str.contains("makkara") #true
str.matches("\\w*") #true
str.starts_with("makkara") #true
str.ends_with("mato") #false
str = str.trim()
str = str.replace("makkara", "sipuli")
str = str.substring(3)
str = str.substring(1, 9)
str.index_of("makkara")
str = str.replace_all("ma", "MAH")
lista = str.split("MAH")
for (merkki in str) {
    print(merkki)
}
```
### Muut
```
try {
    i = 8 / 0
} catch (e) {
    print(e)
}
n = to_num(input())
to_str(n)
content = read_file("test.txt")
print(content)
write_file("lol.txt", content)

#Tiedosto test.bel:
export {
    "i": 9,
    "func": function() {
        print("jou " + self.i)
    }
}
#Tiedosto main.bel:
obj = import "test.bel"

obj.func() #jou 9
```