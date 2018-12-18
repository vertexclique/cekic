package com.vertexclique.cekic

import java.io.File

import buildinfo.BuildInfo

case class Config(
  automotiveApp: Boolean = false,
  osekApp: Boolean = true,
  outputPath: File = new File("build"),
  outputFileName: String = "system.xml",
  systemCount: Int = 1, // generate only one system by default
  appCount: Int = 1,
  // System generation parameters
  seed: Int = 0,
  processorCount: Int = 5,
  commRes: Int = 2,
  taskCnt: Int = 6,
  taskMapper: Boolean = true,
  assignRandomPrio: Boolean = true,
  // timing params
  minResUtil: Double = 0.45,
  maxResUtil: Double = 0.55,
  minActPeriod: Int = 300,
  maxActPeriod: Int = 1000,
  bcetPercentage: Double = 1.0,
  minLaxityConstrFactor: Double = 1.5,
  maxLaxityConstrFactor: Double = 2.0,

  // Runnable specific config
)

object ConfigParser {
  val parser = new scopt.OptionParser[Config](BuildInfo.name) {
    override def renderingMode =
      scopt.RenderingMode.OneColumn

    override def showUsageOnError = true

    head(BuildInfo.name, BuildInfo.version)

    cmd("automotive") action { (x, c) =>
      c.copy(automotiveApp = true, osekApp = false) } text "Generates automotive industry conforming application"

    cmd("osek") action { (x, c) =>
      c.copy(osekApp = true, automotiveApp = false) } text "Generates bare OSEK style application (default)"

    opt[File]('o', "out") required() valueName "<file>" action { (x, c) =>
      c.copy(outputPath = x) } text "out is the path to which the testcases are to be stored"

    opt[String]('f', "fout") action { (x, c) =>
      c.copy(outputFileName = x) } text "fout is the generic name of the test cases"

    opt[Int]('a', "sysCount") action { (x, c) =>
      c.copy(systemCount = x) } text "a is the count of the systems that will be generated from the specification"

    opt[Int]('k', "appCount") action { (x, c) =>
      c.copy(appCount = x) } text "k is the amount of the application that will be generated for the system"

    // System generation parameters

    opt[Int]('s', "seed") action { (x, c) =>
      c.copy(seed = x) } text "s is seed for system generation"

    opt[Int]('p', "processors") action { (x, c) =>
      c.copy(processorCount = x) } text "p is processor count for the underlying system"

    opt[Int]('c', "commRes") action { (x, c) =>
      c.copy(commRes = x) } text "c is communication resource count"

    opt[Int]('t', "taskCnt") action { (x, c) =>
      c.copy(taskCnt = x) } text "t is task count inside of a task chain"

    opt[Boolean]('m', "taskMapper") action { (x, c) =>
      c.copy(taskMapper = x) } text "m maps task chains such that only tasks that are adjacent in the task graph are mapped to the same processor"

    opt[Boolean]('r', "assignRandomPrio") action { (x, c) =>
      c.copy(assignRandomPrio = x) } text "r assigns priorities randomly but ensures that tasks in the front of a task chain receives higher priorities"

    // timing parameters

    opt[Double]("minu") action { (x, c) =>
      c.copy(minResUtil = x) } text "minu is minimum resource utilization"

    opt[Double]("maxu") action { (x, c) =>
      c.copy(maxResUtil = x) } text "maxu is maximum resource utilization"

    opt[Int]("minap") action { (x, c) =>
      c.copy(minActPeriod = x) } text "minap is minimum activation period"

    opt[Int]("maxap") action { (x, c) =>
      c.copy(maxActPeriod = x) } text "maxap is maximum activation period"

    opt[Double]("bcet") action { (x, c) =>
      c.copy(bcetPercentage = x) } text "bcet is set as percentage of WCET"

    opt[Double]("minlax") action { (x, c) =>
      c.copy(minLaxityConstrFactor = x) } text "minlax is minimum constraint laxity"

    opt[Double]("maxlax") action { (x, c) =>
      c.copy(maxLaxityConstrFactor = x) } text "maxlax is minimum constraint laxity"

    // Runnable specific config

  }
}
