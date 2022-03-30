# 1 Popis aplikácie
Mobilná aplikácia pre zobrazenie mapy/pôdorysu budov FAI (pre začiatok) UTB s možnosťou prepínania medzi jednotlivými podlažiami.
Na pôdoryse bude možné vidieť jednotlivé miestnosti na podlaží, približovať/odďaľovať, vyhľadávať a označovať ich na mape pomocou hraničného polygónu.
Bonus: Zobrazenie GPS pozície s horizontálnou prednosťou (vertikálna by bola zrejmä zložitejšia). Údaje o miestnostiach po výbere.

Motivácia: Napriek relatívne malej rozlohe FAI UTB je pre študentov kombinovaného štúdia zo začiatku zložité nájsť správnu miesnosť.
Mapová aplikácia by im uľahčila orientáciu po budove a zredukovala čas potrebný pre vyhľadanie potrebnej miestnosti.

# 2 Požiadavky
* zobrazenie mapového podkladu FAI UTB
* menu pre prepínanie podlaží
* vyhľadávacie pole pre vyhľadanie miestnosti s našepkávaním
  * po vyhľadaní konkrétnej miestnosti dôjde k:
    * prepnutiu podlažia a teda i mapového podkladu (ak to bude potreba)
	* vycentrovaniu na miestnosť
	* vyznačeniu miestnosti v podkladoch
* možnosť uloženia niektorých miestností do obľúbených a ich opätovného vyhľadania

# 3 Plán:
* Návrh UI/storyboard/zistenie potrebných komponent: 4 h
* Návrh dátového modelu/tabuliek databáze pre uloženie mapových podkladov a dát o miestnostiach: 3 h
* Vytvorenie dátových tried/repository/dependency injection: 4 h
* Vytvorenie základného UI mapy a aplikácie: 4 h
* Načítavanie máp z DB pri prepínaní podlaží: 4 h
* Vytvorenie polygónov vyznačenia jednotlivých miestností a možnosť označovania miestností(polygónov) nad mapovým podkladom: 4 h
* UI pre vyhľadávanie v mape: 1 h
* Vyhľadávanie miestností v mapových podkladoch a ich označovanie: 4 h
* UI a navigácia medzi obrazovkami mapových podkladov a obľúbených miestností: 3 h
* Ukladanie označených miestností do obľúbených a ich spätné vyhľadávanie; mazanie obľúbených: 4 h
* Bonus:
  * GPS poloha a zobrazenie na mape: 3 h
  * Metadáta o miestnostiach po ich výbere: 4 h

# 4 Použité technológie
* [Android SDK] (https://developer.android.com/studio)
* [Kotlin](https://kotlinlang.org/)
* AndroidX Jetpack knižnice
* Dagger-Hilt dependency injection
* Android Jetpack Room SQLite databáza

ALEBO

* [Flutter] (https://flutter.dev/)
* [Dart] (https://dart.dev/)
* Material widgets
* sqflite plugin pre databázu

