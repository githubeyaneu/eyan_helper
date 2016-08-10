package eu.eyan.log

import scala.collection.mutable.MutableList

object Log {
  type LogEntry = Tuple2[LogLevel, String]
  val logs = new MutableList[LogEntry]
  def log(level: LogLevel, message: String) = {
    logs.+=((level, message))
    LogWindow.add(message)
  }
  def error(message: String) = log(Error, message)
  def warn(message: String) = log(Warn, message)
  def info(message: String) = log(Info, message)
  def debug(message: String) = log(Debug, message)
  def trace(message: String) = log(Trace, message)
}

abstract class LogLevel {}
case object Error extends LogLevel
case object Warn extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel
case object Trace extends LogLevel