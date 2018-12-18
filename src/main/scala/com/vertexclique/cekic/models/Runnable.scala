package com.vertexclique.cekic.models

case class Runnable(
  id: String,
  taskId: String,
  taskPriority: Int,
  coreId: String,
  wcet: Int,
  bcet: Int,
  bcetPercentage: Double,
  activationEvent: Option[String] = None,
  execMode: Option[String] = None,
  bswCalls: Option[Map[String, String]] = None,
  usedFeatures: Option[List[String]] = None
)
