# Laboratorium 3 – Praktyczny wstęp do Apache Spark

Zacznijmy od utworzenia nowego projektu. **BARDZO WAŻNE** jest abyście zrobili to **dokładnie tak jak pokazałem to na zdjęciu** (poza ścieżkami do plików). 

![Instrukcja](img/project-init.png)

## Operacje na danych w Scali

Zanim zaczniemy opowiadać czym jest spark i do czego można go wykorzystać, musimy nauczyć się jakie operacje na danych dostępne są w samej Scali. Gdyż, analogiczne operacje będziemy później przeprowadzać w Sparku.

Zacznijmy od prostego programu. Utwórz plik .scala, a w nim obiekt z metodą `main`.

```scala
package org.hello

object ScalaOperationsDemo {
  def main(args : Array[String]): Unit = {
    val data = List(1, 2, 3, 4, 5, 5, 5)
  }
}
```

### Map

To operacja, która wykonuje pewną operację, na każdym z elementów na wejściu. Operacja ta musi zwrócić wartość.

![Map](img/ScalaCourse-Map.drawio.png)

Kod w scali:

```scala
println("Map: ", data.map((x: Int) => x * x))
```

### Reduce

Nazwa sugeruje, że coś redukujemy. I tak faktycznie jest. Operacja ta polega, na tym iż konsekwentnie uruchamiamy funkcję, która przyjmuje dwa argumenty i zwraca jeden (typy wejściowe i wyjściowe są takie same). Co w sprawia, iż po wielu iteracjach pozostaje nam jeden element.

![Reduce](img/ScalaCourse-Reduce.drawio.png)

Kod w scali:

```scala
println("Reduce: ", data.reduce((a: Int, b: Int) => a + b))
```

### Filter

Każda operacja wcześniej wykonywała się na wszystkich podanych elementach, a co jeżeli nie chcemy tego robić? Na ratunek przychodzi nam filter. Przyjmuje on funkcję, która zwraca prawdę lub fałsz. Zwrócone zostaną wyłącznie te elementy, dla których funkcja ta zwróci prawdę.

![Filter](img/ScalaCourse-Filter.drawio.png)

Kod w scali:

```
println("Filter: ", data.filter(_ > 2))
```

### GroupBy

Łączy elementy w grupy, na podstawie klucza, który jest wartością zwracaną przez funkcję wejściową.

Na przykład, mamy funkcję, która zwraca czy liczba jest parzysta lub nieparzysta. Jeżeli użyjemy jej wraz z GroupBy na liście elementów, to otrzymamy mapę, zawierającą dwa klucze: parzysta i nieparzysta, a pod tymi kluczami znajdować się będą listy elementów, które do niej pasują.

![groupBy](img/ScalaCourse-groupBy.drawio.png)

```scala
def isEven(x: Int): String = x % 2 match {
    case 0 => "even"
    case 1 => "odd"
}
val grouped = data.groupBy((x: Int) => isEven(x))
println("GroupBy result: ", grouped)
println("GroupBy values: ", grouped.values)
```

### Flatten

Mamy listę list, jak zrobić z niej jedną długą listę? Użyj flatten!

![Flatten](img/ScalaCourse-Flatten.drawio.png)

Kod w scali:

```scala
println("Flatten: ", grouped.values.flatten)
```

## Co to Spark i jak go postawić

Apache Spark to framework do przetwarzania danych w ramach rozproszonego systemu obliczeniowego. Jest to wydajne narzędzie do pracy z dużymi zbiorami danych, umożliwiające wykonywanie wielowątkowych obliczeń na rozproszonych zasobach obliczeniowych. Pozwala na szybkie przetwarzanie danych w pamięci i umożliwia stworzenie skalowalnych aplikacji do analizy danych.

Typowe scenariusze dla danych big data, które można wykonywać z pomocą Apache Spark:

1. Filtrowanie
2. Sortowanie
3. Agregowanie
4. Łączenie źródeł danych
5. Czyszczenie danych
6. Deduplikacja
7. Walidacja danych

**Komponenty sparka**

Aplikacje Sparka działają jako procesy w klastrze obliczeniowym, zarządzane są przez obiekt `SparkContext` w programie który napiszecie, nazwanym `Driver Program`.

Uruchamianie aplikacji zaczyna się od `SparkContext`, łączy się on z `Cluster Manager`, który odpowiada za przydzielenie zasobów. Po połączeniu `Cluster Manager`, przydziela on naszej aplikacji `Executor`y, czyli procesy, w których wykonywać możemy obliczenia oraz przechowywać dane. Następnie kod naszej aplikacji jest wysyłany to `Executor`ów. I ostatecznie `SparkContext` nakazuje `Executor`om wykonywać `Task`i.

![cluster-overview](img/cluster-overview.png)

### Uruchamianie Sparka lokalnie

Pierwsza rzecz o jakiej musicie wiedzieć, to to, że żeby pisać aplikacje na sparka nie potrzebujecie niczego pobierać – InteliJ zrobi wszystko za was. Musicie tylko w pliku `build.sbt` zdefiniować z jakiej wersji sparka chcecie korzystać. Będziemy uruchamiać aplikacje w trybie standalone – czyli lokalnie, na jednym komputerze.

Wersje bibliotek definiuje się dodając następującą linijkę do `.settings`:
```
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"
```

Przykładowy plik `build.sbt` po dodaniu zależności:

```sbt
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "HelloSpark",
    libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"
  )
```

**A dlaczego nie uruchamiamy Sparka w klastrze?**

Niestety nie jest to takie proste na systemie windows bez dostępu do konta administratora. Dla zainteresowanych podrzucam artykuł jak to zrobić: https://phoenixnap.com/kb/install-spark-on-windows-10

## Pierwszy program w Spark

Nasz pierwszy program będzie bardzo prosty. Zliczy on słowa w wybranym tekście. Wykorzystamy do tego sporo gotowych funkcji, które daje Spark, na przykład załadowanie pliku z adresu URL i otworzenie go.

```scala
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkFiles

object WordCountDemo {
  def main(args : Array[String]): Unit ={
    // Setup SparkContext
    val sparkSession = SparkSession.builder
      .master("local[4]")
      .appName("Simple app")
      .getOrCreate()
    val sc = sparkSession.sparkContext

    // Load data from a file
    sc.addFile("https://wolnelektury.pl/media/book/txt/pan-tadeusz.txt")
    val lines = sc.textFile(SparkFiles.get("pan-tadeusz.txt"))

    // Word count
    lines
      .flatMap(line => line.split(' '))
      .map(x => (x,1))
      .reduceByKey((x,y) => x + y)
      .filter(_._2 > 10)
      .sortBy(_._2, ascending = false)
      .foreach(println)
  }
}
```

## Spark i obliczanie PI metodą Monte Carlo

Jak obliczyć PI? Jeżeli pamiętacie wzór na pole koła, to wygląda on  tak:
$$P = \pi r^2$$
Wokół tego pola narysować możemy kwadrat, którego boki są stycznymi okręgu.
Pole tego kwadratu wynosić będzie:
$$P = (2r)^2 = 4r^2$$

Teraz ich stosunek wynosi:

![](img/pi-1.png)

i zawiera w sobie liczbę $\pi$! Możemy to wykorzystać. Będziemy losować punkty należące do kwadratu. Pole koła zastąpimy liczbą punktów należących do koła. A liczba punktów należących do kwadratu, to liczba wszystkich punktów. 

![](img/pi-2.png)

Tak oto dostajemy wzór:
$$\pi = 4 \frac{P_\circ}{P_\square} \approx 4 \frac{\#czerwone}{\#wszystkie}$$

```scala
import org.apache.spark.sql.SparkSession
import scala.math.random

object PiDemo {
  def main(args: Array[String]): Unit = {
    // Setup SparkContext
    val sparkSession = SparkSession.builder
      .master("local[4]")
      .appName("Simple app")
      .getOrCreate()
    val sc = sparkSession.sparkContext

    // Liczba kropek
    val ndots = math.min(10_000_000L, Int.MaxValue).toInt

    val count = sc.parallelize(1 to ndots, 10)
      .map {
        i =>
          val x = random * 2 - 1
          val y = random * 2 - 1
          if (x*x + y*y <= 1) 1 else 0
      }
      .sum()
    println(s"Pi jest równe: ${4 * count / ndots}")
  }
}

```