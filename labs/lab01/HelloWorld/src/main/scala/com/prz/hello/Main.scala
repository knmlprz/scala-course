package com.prz.hello

def printHello(hello: String = "Hello World"): Unit =
  println(hello)

@main def run(): Unit =
  printHello()
