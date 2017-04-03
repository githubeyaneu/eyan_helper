package eu.eyan.log

import scala.collection.mutable.MutableList

object Log {
  val STACK_LEVEL = 3
  var isActive: Boolean = false
  var isWithPrevTime: Boolean = false
  var prevTime = System.currentTimeMillis
  type LogEntry = (LogLevel, String)
  val logs = new MutableList[LogEntry]

  private def log(level: LogLevel, message: String = "") = {
    if (isActive) {
      if (level != Trace) {
        val stack = Thread.currentThread().getStackTrace()(STACK_LEVEL)
        logs += ((level, message))
        val logText = stack.getClassName.substring(stack.getClassName.lastIndexOf(".") + 1) + "." + stack.getMethodName + ": " + message
        val prevTimeLog = if(isWithPrevTime) {" " + (System.currentTimeMillis - prevTime)}else ""
        println(level + prevTimeLog + " " + logText)
        prevTime = System.currentTimeMillis
        LogWindow.add(logText)
      }
    }
    this
  }

  def activate = {isActive = true; this}
  def deactivate = {isActive = false; this}
  def withPrevTime = {isWithPrevTime = true; this}

  def error() = log(Error)
  def error(message: String) = log(Error, message)
  def error(exception: Throwable) = log(Error, exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
  def errorOnConsoleToo(exception: Throwable) = {
    log(Error, exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
    exception.printStackTrace
  }

  def warn() = log(Warn)
  def warn(message: String) = log(Warn, message)
  def warn(o: Object) = log(Warn)

  def info() = log(Info)
  def info(message: String) = log(Info, message)
  def info(o: Object) = log(Info, String.valueOf(o))

  def debug() = log(Debug)
  def debug(message: String) = log(Debug, message)
  def debug(o: Object) = log(Debug, String.valueOf(o))

  def trace() = log(Trace)
  def trace(message: String) = log(Trace, message)
  def trace(o: Object) = log(Trace, String.valueOf(o))
}

class Log

abstract class LogLevel
case object Error extends LogLevel
case object Warn extends LogLevel
case object Info extends LogLevel
case object Debug extends LogLevel
case object Trace extends LogLevel