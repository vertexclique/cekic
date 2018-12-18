package com.vertexclique.cekic.runnablesys

import scala.util.Random

/**
 * Values are obtained from:
 *
 * Kramer, Simon, Dirk Ziegenbein, and Arne Hamann. 2015.
 * “Real World Automotive Benchmarks for Free.” In 6th
 * International Workshop on Analysis Tools and Methodologies
 * for Embedded and Real-Time Systems (WATERS).
 * https://www.ecrts.org/forum/viewtopic.php?f=20&t=23.
 */
object RunnableTimings {
  /**
   * Key => Runnable Duration
   * Value => Share for the Period
   *
   * Angle-Synchronous varies in time, it is denoted with 0.
   * Duration for it will be added at generation time.
   */
  val runnablePeriodDistribution = Map(
    1 -> 3,
    2 -> 2,
    5 -> 2,
    10 -> 25,
    20 -> 25,
    50 -> 3,
    100 -> 20,
    200 -> 1,
    1000 -> 4,
    0 -> 15
  )

  /**
   * Runnable Average CET in microseconds
   */
  val runnableACETS = Map(
    1 -> 5.00,
    2 -> 4.20,
    5 -> 11.04,
    10 -> 10.09,
    20 -> 8.74,
    50 -> 17.56,
    100 -> 10.53,
    200 -> 2.56,
    1000 -> 0.43,
    0 -> 6.52
  )

  /**
   * Key => Runnable Duration
   * Value => (minFactorForBCETGeneration, maxFactorForBCETGeneration)
   */
  val runnableBCETFactors = Map(
    1 -> (0.19, 0.92),
    2 -> (0.12, 0.89),
    5 -> (0.17, 0.94),
    10 -> (0.05, 0.99),
    20 -> (0.11, 0.98),
    50 -> (0.32, 0.95),
    100 -> (0.09, 0.99),
    200 -> (0.45, 0.98),
    1000 -> (0.68, 0.80),
    0 -> (0.13, 0.92)
  )

  /**
   * Key => Runnable Duration
   * Value => (minFactorForWCETGeneration, maxFactorForWCETGeneration)
   */
  val runnableWCETFactors = Map(
    1 -> (1.30, 29.11),
    2 -> (1.54, 19.04),
    5 -> (1.13, 18.44),
    10 -> (1.06, 30.03),
    20 -> (1.06, 15.61),
    50 -> (1.13, 7.76),
    100 -> (1.02, 8.88),
    200 -> (1.03, 4.90),
    1000 -> (1.84, 4.75),
    0 -> (1.20, 28.17)
  )

  def getRandomFactor(start: Double, end: Double, rnd: Random) = {
    val ustart = (start * 1000).toInt
    val uend = (end * 1000).toInt
    val r = ustart + rnd.nextInt( (uend - ustart) + 1 )
    (r / 1000).toDouble
  }

  def calculateRunnableBCETWCET(seed: Int): Map[Int, (Double, Double)] = {
    val r: Random = new scala.util.Random(seed)

    runnableACETS.map { case (duration, acet) =>
      // BCETS
      val bcetFactor = runnableBCETFactors.get(duration).head
      val generatedBCET = getRandomFactor(bcetFactor._1, bcetFactor._2, rnd = r)

      // WCETS
      val wcetFactor = runnableWCETFactors.get(duration).head
      val generatedWCET = getRandomFactor(wcetFactor._1, wcetFactor._2, rnd = r)

      duration -> (generatedBCET, generatedWCET)
    }
  }
}
