package org.slf4j.impl

import org.slf4j.ILoggerFactory
import org.slf4j.ILoggerFactory
import org.slf4j.Logger
import org.slf4j.Marker
import eu.eyan.log._

import scala.collection.mutable
import scala.collection.mutable.Map

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