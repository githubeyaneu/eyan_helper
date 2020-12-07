package org.slf4j.impl

import eu.eyan.log._
import org.slf4j.{ILoggerFactory, Logger}

import scala.collection.mutable

object StaticLoggerBinder {
  lazy val staticLoggerBinder = new StaticLoggerBinder()
  def getSingleton: StaticLoggerBinder = staticLoggerBinder
}
class StaticLoggerBinder {
  def getLoggerFactory: ILoggerFactory = loggerFactory
  lazy val loggerFactory = new ILoggerFactory() {
    private lazy val loggers = mutable.Map[String, Logger]()
    def getLogger(name: String) = loggers.getOrElseUpdate(name, new LogImplSlf4j(name) ) 
  }
}