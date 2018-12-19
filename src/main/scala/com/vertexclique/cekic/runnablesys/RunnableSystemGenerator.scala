package com.vertexclique.cekic.runnablesys

import com.vertexclique.cekic.Config
import com.vertexclique.cekic.models.RunnableBare
import com.vertexclique.cekic.runnablesys.RunnableTimings._
import org.json4s._
import org.tubs.epoc.SMFF.ModelElements.Application.Task
import org.tubs.epoc.SMFF.ModelElements.SystemModel

import scala.collection.JavaConverters._
import purecsv.safe._


object RunnableSystemGenerator {

  def writeRunnables(runnables: Iterable[RunnableBare], file: String): Unit = {
    runnables.toSeq.writeCSVToFileName(file, header=Some(Seq("id", "appId", "taskId",
      "taskPriority", "coreId", "wcet", "bcet", "rpm", "period", "bcetPercentage")))
  }

  def calculateSporadicPeriod(rpm: Double, cylinders: Int): Int = {
    val asSeconds = 120 / (rpm * cylinders)
    (asSeconds * 1000000).toInt // as microseconds
  }

  def generateRunnables(config: Config, systemModel: SystemModel): Iterable[RunnableBare] = {
    systemModel.getApplications.asScala.flatMap { app =>
      val taskList = new java.util.HashMap[String, Task](app.getTaskList).asScala
      val r = new scala.util.Random(config.seed)
      val runnableAmount = Helpers.getBetween(1000, 1500, r).toInt
      val pd = runnablePeriodDistribution.mapValues(_ * (runnableAmount / 100))

      val runnablePerTask = (runnableAmount / taskList.size)

      val runnables = taskList.flatMap { case (taskuuid, task) =>
        // Initialize randomness of the runnable wcet and bcets

        val taskWCET: Int = task.getWCET
        val taskBCET: Int = task.getBCET

        val taskRunnableAmount = pd.mapValues(runnablePerTask * _ / 100)

        taskRunnableAmount.flatMap { case (duration, runAmount) =>
          (1 to runAmount).map { runId =>
            val runnableBCETWCET = calculateRunnableBCETWCET(config.seed + runId)

            val runnableId = if (duration == 0) {
              s"R_AngleSync_${runId}"
            }
            else { s"R_${duration}us_${runId}" }

            val isPeriodicOrSporadic: String = if (duration == 0) "sporadic" else "periodic"

            RunnableBare(
              id = runnableId,
              appId = app.getAppId,
              taskId = taskuuid,
              taskPriority = task.getPrio,
              coreId = task.getMappedTo.getResId,
              wcet = runnableBCETWCET.get(duration).head._2,
              bcet = runnableBCETWCET.get(duration).head._1,
              rpm = 0,
              period = duration,
              bcetPercentage = config.bcetPercentage
            )
          }
        }
      }

      // Sporadic task assignment
      val minRpm = 600D
      val rpms: Seq[BigDecimal] = Range.BigDecimal(minRpm, config.maxRpm, 1D) ++ Range.BigDecimal(config.maxRpm, minRpm-1, -1D)
      val rpmWave = Stream.continually(rpms.toStream).flatten

      (runnables zip rpmWave).map { case (runnable, givenRpm) =>
        val genPeriod = if (runnable.period == 0) { calculateSporadicPeriod(givenRpm.toDouble, config.cylinders) } else runnable.period
        runnable.copy(
          period = genPeriod,
          rpm = givenRpm.toDouble)
      }
    }
  }
}
