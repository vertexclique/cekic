package com.vertexclique.cekic.models

case class RunnableC(
  id: String,
  appId: Int,
  taskId: String,
  taskPriority: Int,
  coreId: Int,
  wcet: Double,
  bcet: Double,
  bcetPercentage: Double,
  activationEvent: Option[String] = None,
  execMode: Option[String] = None,
  bswCalls: Option[Map[String, String]] = None,
  usedFeatures: Option[List[String]] = None
)

case class RunnableBare(
  id: String,
  appId: Int,
  taskId: String,
  taskPriority: Int,
  coreId: Int,
  wcet: Double,
  bcet: Double,
  rpm: Double,
  period: Int,
  bcetPercentage: Double
)
