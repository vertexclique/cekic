package com.vertexclique.cekic.runnablesys

import scala.util.Random

object Helpers {
  def getBetween(start: Double, end: Double, rnd: Random) = {
    val ustart = (start * 1000000).toInt
    val uend = (end * 1000000).toInt
    val r = ustart + rnd.nextInt( (uend - ustart) + 1 )
    r.toDouble / 1000000
  }
}
