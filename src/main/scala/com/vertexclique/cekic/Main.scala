package com.vertexclique.cekic

import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.tubs.epoc.SMFF.ModelElements.SystemModel


object Main {
  @transient var log: Logger = Logger.getLogger("com.vertexclique.cekic")

  def main(args: Array[String]): Unit = {
    log.setLevel(Level.INFO)

    ConfigParser.parser.parse(args, Config()) map { config =>
      log.info("Initializing system model...")
      // do stuff
      Generator.generateMultipleSystems(config)
    } getOrElse {
      // arguments are bad, usage message will have been displayed
      log.error("Bad arguments... Please check usage.")
    }
  }
}
