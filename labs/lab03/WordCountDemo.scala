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
