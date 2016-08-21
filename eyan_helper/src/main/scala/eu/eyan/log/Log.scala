package eu.eyan.log

import scala.collection.mutable.MutableList

object Log {
  type LogEntry = (LogLevel, String)
  val logs = new MutableList[LogEntry]
  private def log(level: LogLevel, message: String) = {
    val stack = Thread.currentThread().getStackTrace
    logs += ((level, message))
    LogWindow.add(stack(3).getClassName.substring(stack(3).getClassName.lastIndexOf(".") + 1) + "." + stack(3).getMethodName + ": " + message)
  }
  def error(message: String) = log(Error, message)
  def error(exception: Throwable) = log(Error, exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
  def warn(message: String) = log(Warn, message)
  def info(message: String) = log(Info, message)
  def debug(message: String) = log(Debug, message)
  def trace(message: String) = log(Trace, message)
}
class Log{}

abstract class LogLevel {}
case object Error extends LogLevel
case object Warn extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel
case object Trace extends LogLevel