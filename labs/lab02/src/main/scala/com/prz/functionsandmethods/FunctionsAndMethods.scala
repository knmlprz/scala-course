package com.prz.functionsandmethods

import scala.util.Random
import scala.util.matching.Regex
import scala.io.Source.{fromURL, fromFile}
import scala.math.pow
import java.net.URL
import java.io.{File, FileReader, FileWriter, IOException}

@main def main(): Unit =
  randomWalkSimulations(30, 100)
  regressionForCrimesInGdansk()


/** Performs simulation of number of realizations of random walk process
 * in order to observe how far coordinates it reaches.
 *
 * @param nSteps: how many steps should each walk contain
 * @param nWalks: how many walks to simulate
 */
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
    .map((position: Int) => walksList.count((steps: Array[Int]) => didWalkReach(steps, position)))
  println("Reaches:")
  for
    p <- List.range(0, nSteps + 1)
  do
    var r = reaches(p)
    println(s"$p - $r")


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

/**
 * Class representing immutable model of fitted linear regression
 * Y = a*X + b
 */
case class UnivariateLinearRegressionModel(a: Double, b: Double)

/** Returns fitted model of univariate linear regression
 * Y = x * X + b
 *
 * @param X: list of values of the predictor
 * @param Y: list of values of the predicted variable corresponding
 *           to values in X
 * @return fitted model of univariate linear regression
 */
def fitUnivariateLinearRegression(X: List[Double], Y: List[Double]): UnivariateLinearRegressionModel =
  val det2x2 = (A: Double, B: Double, C: Double, D: Double) => {
    // determinant of matrix:
    // [ A  B ]
    // [ C  D ]
    A * D - B * C
  }
  val n = X.length
  val sumXi = X.sum
  val sumYi = Y.sum
  val sumXiSquared = X.map(pow(_, 2.0)).sum
  val sumXiYi = List.range(0, n)
    .map((i: Int) => X(i) * Y(i))
    .sum
  val W = det2x2(sumXiSquared, sumXi, sumXi, n.toDouble)
  val Wa = det2x2(sumXiYi, sumXi, sumYi, n)
  val Wb = det2x2(sumXiSquared, sumXiYi, sumXi, sumYi)
  UnivariateLinearRegressionModel(Wa / W, Wb / W)


/** For given fitted linear regression model and predictor value x,
 * computes the value predicted by this model for this value x
 */
def predictValueWithULR(model: UnivariateLinearRegressionModel, x: Double): Double =
  model.a * x + model.b


/** Performs fitting linear regression model to population of Gdansk's districts
 * and number of crimes in each of them.
 *
 * Downloads CSV files from https://ckan.multimediagdansk.pl
 */
def regressionForCrimesInGdansk(): Unit =
  val crimesData = gdanskCrimesInDistrictsExtract()
  val populaces = crimesData.map(_ (0).toDouble)
  val crimes = crimesData.map(_ (1).toDouble)
  val model = fitUnivariateLinearRegression(populaces, crimes)
  crimesData
    .map((popul: Int, crimes: Int) => (popul, crimes, predictValueWithULR(model, popul)))
    .foreach(println)
