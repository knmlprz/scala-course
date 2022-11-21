# Funkcje i metody

W tym laboratorium poćwiczymy tworzenie programów opartych
o wiele funkcji i metod.

Tak jak w poprzednim laboratorium, jedynym wymaganiem technicznym
jest zainstalowany IntelliJ Community.

## Agenda

---

1. Wstęp. Przygotowanie.
2. Zadanie pierwsze.
3. Zadanie drugie.

## Wstęp. Przygotowanie.

---

Utwórz nowy projekt w IntelliJ, tak jak na poprzednich zajęciach.
Wybierz wersję `Scala 3.2.0` lub `Scala 3.2.1`. Zaznacz `Download
sources` dla sbt i Scali.

W katalogu `scr/main/scala` utwórz katalogi: `com/prz/functionsandmethods`
i w ostatnim katalogu utwórz plik `FunctionsAndMethods.scala`.
Umieść w nim nazwę pakietu i metodę główną, a w niej prosty testowy kod:

```scala
package com.prz.functionsandmethods

@main def main(): Unit =
  println("Hello!")
```

Uruchom kod i sprawdź, czy działa.

## Zadanie 1.
## Zmiany klimatu, czyli rachunek prawdopodobieństwa w praktyce

---

Wyobraź sobie zbiornik wodny. Na skutek zmian klimatu, które powodują,
że pogoda jest coraz mniej stabilna, poziom wody w zbiorniku bez
przerwy się zmienia - codziennie przybywa wody o 1 metr lub ubywa
wody o 1 metr.

Zbadamy, jaka jest szansa, że dla zadanej liczby dni poziom
wody w zbiorniku przekroczy dany próg.

Poziom wody modelujemy za pomocą błądzenia przypadkowego,
które porusza się wzdłuż osi liczbowej. Startuje z punktu 0
i w każdym kroku porusza się losowo o jednostkę w stronę
ujemną lub dodatnią.

```scala
import scala.util.Random

def randomWalkSimulations(nSteps: Int, nWalks: Int): Unit =
  val getRandomWalkSteps: Int => Array[Int] =
    (n: Int) => Array.fill(n) {
      if Random.nextBoolean() then 1 else -1
    }
  // val getWalkPositionsFromSteps: Array[Int] => Array[Int] =
  //   (steps: Array[Int]) => steps.scanLeft(0)((i,j) => i+j)
  val getWalkPositionsFromSteps: Array[Int] => Array[Int] =
    (steps: Array[Int]) => steps.scanLeft(0)(_ + _).drop(1)
  val getRandomWalkPositions: Int => Array[Int] =
    getWalkPositionsFromSteps compose getRandomWalkSteps
  val walksList: List[Array[Int]] =
    List.fill(nWalks) {
      getRandomWalkPositions(nSteps)
    }
  val didWalkReach: (Array[Int], Int) => Boolean =
    (steps: Array[Int], position: Int) => steps.count(_ >= position) > 0
  val reaches = List.range(0, nSteps + 1)
    .map((position: Int) => walksList
      .count((steps: Array[Int]) => didWalkReach(steps, position)))
  println("Reaches:")
  for
    p <- List.range(0, nSteps + 1)
  do
    var r = reaches(p)
    println(s"$p - $r")
```

Symulację przeprowadza metoda `randomWalkSimulations`, która
przyjmuje dwa parametry typu `Int`: liczbę kroków w każdym
symulowanym błądzeniu oraz liczbę realizacji błądzenia, którą chcemy
zasymulować. W metodzie definiujemy funkcje: generującą przykładowy
zestaw kroków o zadanej liczbie, tworzącą z kroków ciąg pozycji.
Symulujemy zadaną ilość realizacji błądzenia losowego.
Następnie generujemy listę wartości, której wartość na pozycji
_i_ równa jest liczbie procesów, które _dotarły_ do pozycji _i_
(to, że proces _dotarł_ do pozycji _i_ definiujemy jako fakt,
że proces po opuszczeniu pozycji początkowej był obecny przynajmniej
raz na pozycji o numerze większym bądź równym _i_.) Wyniki
prezentujemy za pomocą funkcji `println`.

## Zadanie 2.
## Model regresji liniowej dla liczby przestępstw w Gdańsku

Zbudujemy model regresji liniowej, który opisywał będzie liczbę
przestępstw popełnionych w danej dzielnicy Gdańska na podstawie
liczby jej mieszkańców.

Naszym źródłem danych będzie portal, gdzie publikowane są otwarte dane
udostępniane przez urząd miejski w Gdańsku oraz pozostałe podmioty
[ckan.multimediagdansk.pl](https://ckan.multimediagdansk.pl/). Konkretnie,
interesować nas będą zbiory:

- Liczba przestępstw w podziale na dzielnice w Gdańsku
[https://ckan.multimediagdansk.pl/dataset/6f667d3b-4bb2-45f3-a377-f3c856b465e0/resource/7eeb36e0-e967-442e-92b1-5a05d892f29f](https://ckan.multimediagdansk.pl/dataset/6f667d3b-4bb2-45f3-a377-f3c856b465e0/resource/7eeb36e0-e967-442e-92b1-5a05d892f29f)

- Ludność Gdańska według jednostek pomocniczych
[https://ckan.multimediagdansk.pl/dataset/a0564507-fd56-4eb9-8444-1d4d70b1d5c0/resource/50c1cfc2-1608-4ba7-9f44-ffcd7f878e33](https://ckan.multimediagdansk.pl/dataset/a0564507-fd56-4eb9-8444-1d4d70b1d5c0/resource/50c1cfc2-1608-4ba7-9f44-ffcd7f878e33)

Najpierw wykorzystamy metodę z poprzednich zajęć, służącą do pobrania
i zapisania pliku.

```scala
import scala.io.Source.{fromURL, fromFile}
import java.net.URL
import java.io.{File, FileReader, FileWriter, IOException}

def downloadFile(fileToDownload: URL, outName: String): Unit =
  println(s"Tring to download file $fileToDownload")
try
val src = fromURL(fileToDownload)
val out = FileWriter(outName)
out.write(src.mkString)
out.close()
println("Done. Closing file.")
catch
case e: IOException => "Cannot download/save file."
```

Za pomocą tej metody pobierzemy ze strony automatycznie pliki CSV
i zapiszemy je w plikach na naszym komputerze.

Następnie piszemy metodę `gdanskCrimesInDistrictsExtract`, która
będzie ściągać odpowiednie dane, przetwarzać je i zwracać
listę krotek (`Tuple`), gdzie każda tupla będzie odpowiadać
którejś dzielnicy i będzie postaci (_populacja-dzielnicy_, _liczba-przestępstw-w-dzielnicy_).

(Pamiętamy o pisaniu oprócz kodu także i dokumentacji kodu -
podczas pisania programu składającego się z wielu funkcji i metod
to bardzo ważne, ponieważ umożliwia łatwe odnalezienie się w kodzie
w trakcie jego czytania i próby jego użycia :) )

```scala
import scala.util.matching.Regex

/** Extracts data about Gdansk's districts' population and number
 * of crimes
 *
 * Method downloads CSV files from https://ckan.multimediagdansk.pl,
 * saves CSV files, performs fundamental data cleaning
 * and returns data as list of tuples.
 *
 * @return list of data entries: (population, number-of-crimes)
 *         for each district
 */
def gdanskCrimesInDistrictsExtract(): List[(Int, Int)] =
  val urlStringPopulace = "https://ckan.multimediagdansk.pl/dataset/a0564507-fd56-4eb9-8444-1d4d70b1d5c0/resource/50c1cfc2-1608-4ba7-9f44-ffcd7f878e33/download/ludnosc-gdanska-wg-jednostek-pomocniczych.csv"
  downloadFile(
    URL(urlStringPopulace), "gdansk-districts-populace.csv"
  )
  val urlStringCrime = "https://ckan.multimediagdansk.pl/dataset/6f667d3b-4bb2-45f3-a377-f3c856b465e0/resource/7eeb36e0-e967-442e-92b1-5a05d892f29f/download/liczba-przestpstw-w-podziale-na-dzielnice-gdaska-w-latach-2015-2017-uszeregowano-rosnco-arkusz1.csv"
  downloadFile(
    URL(urlStringCrime), "gdansk-districts-crime.csv"
  )
  val filePopulace = fromFile("gdansk-districts-populace.csv")
  val fileCrime = fromFile("gdansk-districts-crime.csv")
  val cleanPopulDistrict = (district: String) => {
    if district.contains("Stogi") then "Stogi"
    else if district.contains("Wrzeszcz Dolny") then "Wrzeszcz Dolny"
    else if district.contains("Chełm") then "Chełm"
    else district
  }
  val quotesNumberRegex = """"([\d]+),([\d]+)"""".r
  val districtsPopulations = filePopulace.getLines()
    .map(_.split(";"))
    .filter(_(0).forall(_.isDigit))
    .filter(_(0) == "2017")
    .foldLeft(Map.empty[String, Int]) {
      (populMap, lineParts) => populMap + (cleanPopulDistrict(lineParts(1)) -> lineParts(2).toInt)
    }
  val data: List[(Int, Int)] = List()
  val districtsCrime = fileCrime.getLines()
    .map(quotesNumberRegex.replaceAllIn(_, m => m.group(1) + m.group(2)))
    .filter(_(0).isDigit)
    .map(_.split(','))
    .foldLeft(data){
      (tuplesList, parts) =>
        if districtsPopulations.isDefinedAt(parts(1)) then
          tuplesList :+ (districtsPopulations(parts(1)), parts(4).toInt)
        else tuplesList
    }
  districtsCrime
```

W powyższej metodzie najpierw pobieramy dane i zapisujemy je do plików.
Następnie zczytujemy dane z plików linijka po linijce, dokonując niezbędnych
oczyszczeń (drobne nieścisłości w nazwach dzielnic w obu plikach,
cudzysłowie wokół liczb zdarzające się w jednym z plików). Rozwiązać musimy
też inny problem - dzielnice w obu plikach są umieszczone w różnej kolejności;
do dopasowania linijek odnoszących się do poszczególnych dzielnic
używamy struktury danych `Map`.

Mamy już listę datapointów, teraz pora na implementację regresji liniowej.