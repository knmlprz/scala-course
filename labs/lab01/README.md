# Absolutny wstęp do programowania w języku scala

Kurs ten prowadzony jest w środowisku InteliJ Community. I jest to jedyne wymaganie.

## Agenda

1. Tworzenie nowego projektu
2. Podstawowa składnia języka scala
3. Tworzenie programów w scali

## Utworzenie projektu

Aby utworzyć nowy projekt dla języka Scala wybierz z wstążki 
`File > New > Project`, następnie przy wyborze języka kliknij `+`, z listy wybierz `Scala` i kliknij `Install` przy ikonie scalii. Kliknij `OK` i wybierz `Scala`. 

Wybierz wersję `Scala: 3.2.0` oraz zaznacz `Download sources` zarówno dla Scali jaki i sbt.

Kliknij przycisk `Create`.

## Podstawowa składnia języka Scala

### Deklarowanie zmiennych i printowanie

Podstawową składnię poznamy z pomocą Scala REPL. Kliknij `Tools > Scala REPL` lub wciśnij `Ctrl + Shift + D`. Na dole edytora powinno otworzyć Ci sie okno `Scala REPL`. Pisać będziemy po znaku zachęty `scala>`

Najprostsza rzecz, jaką można zrobić w scali to wypisanie tekstu na konsolę:

```scala
println("Hello world")
```

Niemutowalne zmienne deklarujemy w następujący sposób `val <nazwa>:<typ> = <wartość>`. Na przykład:

```scala
val number: Int = 100
```

Przy czym typ jest opcjinalny, często Scala sama rozpozna typ zmiennej. Poprzedni przykład można uprościć do:

```scala
val number = 100
```

Opcji na wypisanie naszego numeru na konsolę jest kilka, naprostrzy z nich to:

```scala
println(number)
```

ale możemy też sformatować tekst wklajając do niego `number`:

```scala
println(s"Mój numer to: $number")
```

Co do zmiennych niemutowalnych, to oznacza to iż nie możemy nadpisać jej wartości tj. taki kod nie zadziała:

```scala
number = 101
```
i otrzymamy błąd:
```
-- [E052] Type Error: ------------
1 |number = 101
  |^^^^^^^^^^^^
  |Reassignment to val number
```

Jeżeli planujemy nadpisywac zmienne (co nie jest rekomendowane, ale możemy to robić), należy deklarować zmienne z słowem kluczowym `var`. Na przykład:

```scala
var canChange = 999
```

### Podstawowe typy danych w scali

Tekst:
```scala
val hello: String = "Hello world"
```
przypominam, że możemy go formatować:

```scala
val helloNumber: String = s"Hello $number"
```

Typy liczbowe:
```scala
val wholeNumber: Int = 100 // liczby całkowite
val floatNumber: Float = 1.0 // pojedyncza precyzja
val doubleNumber: Fouble = 1.0 // podwójna prezyzja
```

### Bloki kodu

Możemy łączyć wyrażenia, poprzez obłożenie ich w `{}`. Wynikiem bloku, jest ostatnia operacja w nim wykonana. W `Scala REPL` przepisuj go linkja po linijce! Przykład:

```scala
println({
  val temp = 45
  temp + 50
})
```
Wypisze `95`.

### Funkcje

W scali funkcje to wyrażenia mające parametry i przyjmujące argumenty. Naprostszymi funkcjami są funkcje anonimowe / lambdy. Na przykład, ta funkcja przyjmuje liczbę i zwraca tę liczbę zwiększoną o 1.

```scala
(x: Int) => x + 1
```

Funkcję możemy nazwać, poprzez przypisanie jej do zmiennej i wywołać.

```scala
val addOne = (x: Int) => x + 1
addOne(3) // 4
```

Funkcje mogą posiadać wiele parametrów.

```scala
val div = (x: Int, y: Int) => x / y
```

Mając tą wiedzę możemy już napisać pierwszy program w scali

## Hello world - pierwszy program

Zaczniemy od napisania hello world w scali. Wybierz folder `src/main/scala` i kliknij na niego prawym przyciskiem myszy. 


Następnie wybierz `New > Scala class`. Konwencja mówi iż nazwy pakietów (klasy tworzone są w ramach pakietów), powinny mieć nazwę `com.domena.pakiet.klasa`. Stwórzymy więc klasę `com.prz.hello.Main`. 
Teraz w utworzonym pliku `Main` umieść kod:

```scala
// Nazwa pakietu
package com.prz.hello

// Metoda wejściowa dla programu
@main def run(): Unit =
  println("Hello")
```

Punktem startowym dla programów w scali jest metoda oznaczona `@main`. `:Unit` oznacza iż metoda ta nie zwraca, żadnych obiektów. 

Program uruchamia się klikając zielony trójkąt obok `@main`. 

### Czym są metody

Metody są podobne do funkcji. Definiuje się je podobnie jak powyżej:

```scala
def printHello(hello: String = "Hello World"): Unit = 
  println(hello)
```
i wywołuje się je w następujący sposób:

```scala
printHello()
printHello("Hello World 2")
printHello(hello = "Hello World 3")
```

Cały kod programu mógłby wyglądać następująco.

```scala
package com.prz.hello

def printHello(hello: String = "Hello World"): Unit =
  println(hello)

@main def run(): Unit =
  printHello()
```

Wcięty kod po znaku `=` traktuje się jako blok kodu tej funkcji. Jej równoważny zapis to:

```scala
def 
```

