import scala.annotation.tailrec

def factorial_regular(n: Int): Int =
    if n == 0 then 1
    else n * factorial_regular(n - 1)

@tailrec
def factorial_tr(n: Int, acc: Int): Int =
    if n == 0 then acc
    else factorial_tr(n - 1, acc * n)

def main(): Unit =
  factorial_regular(6)
