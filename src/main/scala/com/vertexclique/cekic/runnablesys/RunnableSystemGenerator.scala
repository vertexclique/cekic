package com.vertexclique.cekic.runnablesys

import com.vertexclique.cekic.Config
import org.tubs.epoc.SMFF.ModelElements.Application.Task
import org.tubs.epoc.SMFF.ModelElements.SystemModel

import scala.collection.JavaConverters._

object RunnableSystemGenerator {
  def generateRunnables(config: Config, systemModel: SystemModel) = {
    systemModel.getApplications.asScala.map { app =>
      val taskList = new java.util.HashMap[String, Task](app.getTaskList).asScala
      taskList.foreach { case (taskId, task) =>
        // Initialize randomness of the runnable wcet and bcets
        val r = new scala.util.Random(config.seed)

        val taskWCET: Int = task.getWCET
        val taskBCET: Int = task.getBCET
      }
    }
  }
}
