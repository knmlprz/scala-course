package com.prz.hello

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

@main def run(): Unit =
  downloadFile(URL("https://wolnelektury.pl/media/book/txt/pan-tadeusz.txt"), "pan-tadeusz.txt")
  val file = fromFile("pan-tadeusz.txt")
  val res = file.getLines()
    .flatMap(_.split(" "))
    .map(_.replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}]", ""))
    .foldLeft(Map.empty[String, Int]) {
      (counter, word) => counter + (word -> (counter.getOrElse(word, 0) + 1))
    }.toList
    .sortBy(_._2)
    .foreach(println)
