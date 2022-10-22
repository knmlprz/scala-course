package com.prz.hello

def printHello(hello: String = "Hello World"): Unit = {
  println(hello)
}

class Person(val name: String, val age: Int):
  val info = name + ", " + age

@main def run(): Unit =
  val person = Person("Piotr", 23)
  val person2 = Person("Piotr", 23)
  if person == person2 then
    println("Obiekty są równe")
  else
    println("Nie są równe.")