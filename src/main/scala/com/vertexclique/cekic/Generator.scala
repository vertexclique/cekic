package com.vertexclique.cekic

import java.io.File
import java.io.IOException
import java.io.PrintWriter

import org.tubs.epoc.SMFF.ImportExport.Pdf.PdfPrinter
import org.tubs.epoc.SMFF.ImportExport.XML.ModelLoader
import org.tubs.epoc.SMFF.ImportExport.XML.ModelSaver
import org.tubs.epoc.SMFF.ModelElements.SystemModel
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory.TaskChainApplicationFactory
import org.tubs.epoc.SMFF.SystemFactories.ApplicationFactories.Implementations.TaskChainApplicationFactory.TaskChainApplicationFactoryData
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory.LaxityConstraintFactory
import org.tubs.epoc.SMFF.SystemFactories.ConstraintFactories.Implementations.StdConstraintFactory.LaxityConstraintFactoryData
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapper
import org.tubs.epoc.SMFF.SystemFactories.Mappers.Implementations.StdMapper.SensActMapperData
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactory
import org.tubs.epoc.SMFF.SystemFactories.PlatformFactories.Implementations.StdPlatformFactory.StdPlatformFactoryData
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner.TaskChainPriorityAssigner
import org.tubs.epoc.SMFF.SystemFactories.SchedulingParameterFactories.Implementations.TaskChainPriorityAssigner.TaskChainPriorityAssignerData
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast.UUniFastTimingFactory
import org.tubs.epoc.SMFF.SystemFactories.TimingFactories.Implementations.UUniFast.UUniFastTimingFactoryData
import org.json4s.Xml._
import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._

import scala.xml.XML

object Generator {
  def generateMultipleSystems(config: Config) = {
    (1 to config.systemCount).foreach { x =>
      var systemModel = new SystemModel
      val rc = config.copy(seed = config.seed + (x - 1))
      generate(x, rc, systemModel)
    }
  }

  def generate(systemIndex: Int, config: Config, systemModel: SystemModel) = {
    config.outputPath.mkdirs()

    val rc = (config.automotiveApp, config.osekApp) match {
      case (true, false) =>
        config.copy(

        )
      case (_, _) => config
    }

    //--------------------------------------
    // PARAMETRIZATION OF GENERATION PROCESS
    //--------------------------------------
    // PLATFORM FACTORY
    // this parameter set will create platforms consisting of 5 processors and 2 communication media
    var platformFactoryData = new StdPlatformFactoryData(
      rc.processorCount, //numRes
      rc.commRes, //numCRes
      rc.seed //seed
    )

    // APPLICATION FACTORY
    // this parameter set will create chains of tasks consisting of 6 tasks
    var appFactoryData = new TaskChainApplicationFactoryData(
      rc.taskCnt, //taskCnt
      rc.seed //seed
    )

    // MAPPER
    // this parameter set will map task chains such that only tasks that are adjacent in the task graph are mapped to the same processor
    // (uses standard parameters and works only for task chains)
    var mapperData = if (rc.taskMapper) {
      Some(new SensActMapperData(rc.seed))
    } else { None }

    // PRIORITY ASSIGNER
    // this parameter set assigns priorities randomly but ensures that tasks in the front of a task chain receives higher priorities
    // (works only for task chains)
    var prioAssignerData = if (rc.assignRandomPrio) {
      Some(new TaskChainPriorityAssignerData(rc.seed))
    } else { None }

    // TIMING FACTORY
    // this parameter assigns timing properties according to the UUniFast algorithm. Activation Periods are uniformly distributed between
    // minActPeriod and maxActPeriod. BCET is set as percentage of WCET
    var timingFactoryData = new UUniFastTimingFactoryData(
      rc.minResUtil, //minResU
      rc.maxResUtil, //maxResU
      rc.minActPeriod, //minActPeriod
      rc.maxActPeriod, //maxActPeriod
      rc.bcetPercentage, //bcetPercentage
      rc.seed //seed
    )

    // CONSTRAINT FACTORY
    // creates path latency constraints such that the constraint is LaxityFactor*sum of WCETs along the path. LaxityFactor is randomly selected
    // from the interval minLaxityFactor,maxLaxityFactor
    var constrFactoryData = new LaxityConstraintFactoryData(
      rc.minLaxityConstrFactor, //minLaxityFactor
      rc.maxLaxityConstrFactor, //maxLaxityFactor
      rc.seed //seed
    )

    // create the actual factories with the parameter sets and the system model
    val platformFactory: StdPlatformFactory = new StdPlatformFactory(systemModel, platformFactoryData)
    val appFactory: TaskChainApplicationFactory = new TaskChainApplicationFactory(systemModel, appFactoryData)

    val mapper: Option[SensActMapper] =
      mapperData.map(new SensActMapper(systemModel, _))
    val prioAssigner: Option[TaskChainPriorityAssigner] =
      prioAssignerData.map(new TaskChainPriorityAssigner(systemModel, _))

    val timingFactory: UUniFastTimingFactory = new UUniFastTimingFactory(systemModel, timingFactoryData)
    val constrFactory: LaxityConstraintFactory = new LaxityConstraintFactory(systemModel, constrFactoryData)

    //-----------------------------
    // START THE GENERATION PROCESS
    //-----------------------------
    // generate platform
    platformFactory.generatePlatform()

    // generate applications and map them on the platform
    (1 to rc.appCount).map { _ =>
      val app = appFactory.generateApplication()
      mapper.foreach(_.map(app))
    }

    // assign priorities
    prioAssigner.map(_.assignPriorities())

    // assign timing properties
    timingFactory.generateTimings()

    // generate constraints
    constrFactory.generateConstraints()
    //-----------------------------
    // END OF TESTCASE GENERATION

    //-----------------------------
    // WRITE THE TESTCASE TO A FILE
    //-----------------------------
    try {
      val f = new File(rc.outputPath, rc.outputFileName)
      val fSystem = s"${f.getPath}-$systemIndex.xml"
      val fSystemJson = s"${f.getPath}-$systemIndex.json"
      val fGraph = s"${f.getPath}-$systemIndex.pdf"

      // save the model to an XML file
      new ModelSaver(fSystem).saveModel(systemModel)

      val xmlData = XML.loadFile(fSystem)
      new PrintWriter(fSystemJson) { write(pretty(render(toJson(xmlData)))); close }
//      val loadedModel = new ModelLoader(fSystem).generateSystem

      //-------------------------------------
      // PRINT THE SYSTEM MODEL AS PDF
      //-------------------------------------
      PdfPrinter.convertToPdf(systemModel, fGraph)
    } catch {
      case e: IOException =>
        e.printStackTrace()
    }
  }
}
