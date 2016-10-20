package eu.eyan.log

import scala.collection.mutable.MutableList

object Log {
  var isActive: Boolean = false
  var prevTime = System.currentTimeMillis
  type LogEntry = (LogLevel, String)
  val logs = new MutableList[LogEntry]

  private def log(level: LogLevel, message: String = "") = {
    if (isActive) {
      if (level != Trace) {
        val stack = Thread.currentThread().getStackTrace
        logs += ((level, message))
        val logText = stack(3).getClassName.substring(stack(3).getClassName.lastIndexOf(".") + 1) + "." + stack(3).getMethodName + ": " + message
        println(level + " " + (System.currentTimeMillis - prevTime) + " " + logText)
        prevTime = System.currentTimeMillis
        LogWindow.add(logText)
      }
    }
  }

  def activate() = isActive = true

  def error() = log(Error)
  def error(message: String) = log(Error, message)
  def error(exception: Throwable) = log(Error, exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
  def errorOnConsoleToo(exception: Throwable) = {
    log(Error, exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
    exception.printStackTrace
  }

  def warn() = log(Warn)
  def warn(message: String) = log(Warn, message)

  def info() = log(Info)
  def info(message: String) = log(Info, message)

  def debug() = log(Debug)
  def debug(message: String) = log(Debug, message)

  def trace() = log(Trace)
  def trace(message: String) = log(Trace, message)
}
class Log {}

abstract class LogLevel {}
case object Error extends LogLevel
case object Warn extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel
case object Trace extends LogLevel