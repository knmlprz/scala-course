package org.hello

object ScalaOperationsDemo {
  def main(args : Array[String]): Unit = {
    val data = List(1, 2, 3, 4, 5, 5, 5)
    println("Map: ", data.map((x: Int) => x * x))
    println("Reduce: ", data.reduce((a: Int, b: Int) => a + b))
    println("Filter: ", data.filter(_ > 2))


    def isEven(x: Int): String = x % 2 match {
      case 0 => "even"
      case 1 => "odd"
    }
    val grouped = data.groupBy((x: Int) => isEven(x))
    println("GroupBy result: ", grouped)
    println("GroupBy values: ", grouped.values)

    println("Flatten: ", grouped.values.flatten)
  }
}