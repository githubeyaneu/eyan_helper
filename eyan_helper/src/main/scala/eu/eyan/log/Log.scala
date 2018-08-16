package eu.eyan.log

import scala.collection.mutable.MutableList

import eu.eyan.util.string.StringPlus.StringPlusImplicit
import rx.subjects.PublishSubject
import rx.lang.scala.Observable
import rx.subjects.ReplaySubject
import rx.subjects.BehaviorSubject
import org.slf4j.Logger
import org.slf4j.Marker

case class Log(level: LogLevel, text: String)

object Log {
  private lazy val LOG_CLASS_NAME = Log.getClass.getName
  private lazy val LOG_SLF4J_NAME = classOf[LogImplSlf4j].getName

  var logToConsole = true
  var errToConsole = true

  private var actualLevel: LogLevel = None
  private val actualLevelPublisher = BehaviorSubject.create[LogLevel](actualLevel)
  lazy val levelObservable = actualLevelPublisher.asObservable

  private val logger = ReplaySubject.create[Log](1000 * 1000)
  lazy val logsObservable = logger.asObservable

  def log(level: LogLevel, message: => String, t: => Throwable): Log.type = {
    lazy val throwable = t
    lazy val logText = message
    def msg = if (logText != "") { logText + " -" } else ""
    def tName = throwable.getClass.getName
    def tMsg = throwable.getMessage
    def tSt = throwable.getStackTrace.mkString("\r\n  ")
    log(level, s"$msg $tName: $tMsg\r\n  $tSt")
  }

  def log(level: LogLevel, message: => String = ""): Log.type = {
    lazy val messageText = message
    def stackElementsNotToLog(stackTraceElement: StackTraceElement) =
      stackTraceElement.getClassName != LOG_CLASS_NAME &&
        !stackTraceElement.getClassName.contains("LogImplicit") &&
        stackTraceElement.getClassName != LOG_SLF4J_NAME
    def stackElementWhereLogWasCalled = Thread.currentThread.getStackTrace.filter(stackElementsNotToLog)(1) // TODO use lift
    def stackClassAndMethod = stackElementWhereLogWasCalled.getClassName.substring(stackElementWhereLogWasCalled.getClassName.lastIndexOf(".") + 1) + "." + stackElementWhereLogWasCalled.getMethodName
    def logText = stackClassAndMethod + (if (messageText != "") { ": " + messageText } else "")
    if (actualLevel.shouldLog(level)) logger.onNext(new Log(level, logText))
    this
  }

  def start = enable
  def stop = disable
  def enable = activateDebugLevel
  def disable = deactivate
  def deactivate = activate(None)

  // with prev time logic
  //  private var prevTime = System.currentTimeMillis
  //  private var isWithPrevTime: Boolean = false
  //  def withPrevTime = { isWithPrevTime = true; this }
  //  private def prevTimeLog = if (isWithPrevTime) { " " + (System.currentTimeMillis - prevTime) } else ""
  //  change the method in log method: def consoleText = f"$level%-5s $prevTimeLog $logText"
  //  prevTime = System.currentTimeMillis

  def activate(level: LogLevel = Debug): Log.type = {
    Log.info("activating log level: " + level)
    actualLevel = level
    actualLevelPublisher.onNext(level)
    Log.info("activated log level: " + level)
    this
  }

  def activateNone = activate(None)
  def activateFatalLevel = activate(Fatal)
  def activateErrorLevel = activate(Error)
  def activateWarnLevel = activate(Warn)
  def activateInfoLevel = activate(Info)
  def activateDebugLevel = activate(Debug)
  def activateTraceLevel = activate(Trace)
  def activateAllLevel = activateTraceLevel

  def fatal = log(Fatal)
  def fatal(o: => Object) = log(Fatal, String.valueOf(o))

  def error = log(Error)
  def error(message: => Any): Log.type = {
    if (actualLevel.shouldLog(Error))
      message match {
        case t: Throwable => error("", t)
        case o: Any       => log(Error, String.valueOf(o))
      }
    this
  }

  def error(message: => String, t: => Throwable) = log(Error, message, t)

  def warn = log(Warn)
  def warn(o: => Object) = log(Warn, String.valueOf(o))

  def info = log(Info)
  def info(o: => Object) = log(Info, String.valueOf(o))

  def debug = log(Debug)
  def debug(o: => Object) = log(Debug, String.valueOf(o))

  def trace = log(Trace)
  def trace(o: => Object) = log(Trace, String.valueOf(o))

  def logToConsoleText(log: Log) = f"${log.level}%-5s ${log.text}"
  logsObservable.filter(_.level > Error).subscribe(log => { logToConsoleText(log).println })
  logsObservable.filter(_.level <= Error).subscribe(log => { logToConsoleText(log).printlnErr })

  implicit class LogImplicitString(val s: String) {
    def logFatal = Log.fatal(s)
    def logError = Log.error(s)
    def logWarn = Log.warn(s)
    def log = logInfo
    def logInfo = Log.info(s)
    def logDebug = Log.debug(s)
    def logTrace = Log.trace(s)
  }

  def isEnabled(level: LogLevel) = actualLevel.shouldLog(level)
}

abstract class LogLevel(val prio: Int) {
  def shouldLog(otherLevel: LogLevel) = otherLevel.prio <= prio
  def <(otherLevel: LogLevel) = otherLevel.prio > prio
  def >(otherLevel: LogLevel) = otherLevel.prio < prio
  def <=(otherLevel: LogLevel) = otherLevel.prio >= prio
  def >=(otherLevel: LogLevel) = otherLevel.prio <= prio
}
case object None extends LogLevel(100)
case object Fatal extends LogLevel(200)
case object Error extends LogLevel(300)
case object Warn extends LogLevel(400)
case object Info extends LogLevel(500)
case object Debug extends LogLevel(600)
case object Trace extends LogLevel(700)

class LogImplSlf4j(name: String) extends Logger {
  def getName(): String = name
  def nameLog = if (name == null || name == "") "" else s"Logger: $name, "

  implicit class MarkerToString(marker: Marker) {
    def getLog = if (marker == null || marker.getName == null || marker.getName == "") "" else s"Marker: ${marker.getName}, "
  }

  def isTraceEnabled(): Boolean = Log.isEnabled(Trace)
  def isTraceEnabled(marker: Marker): Boolean = isTraceEnabled()
  def trace(msg: String): Unit = Log.log(Trace, nameLog + msg)
  def trace(format: String, arg: AnyRef): Unit = Log.log(Trace, nameLog + String.format(format, arg))
  def trace(format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Trace, nameLog + String.format(format, arg1, arg2))
  def trace(format: String, arguments: AnyRef*): Unit = Log.log(Trace, nameLog + String.format(format, arguments))
  def trace(msg: String, t: Throwable): Unit = Log.log(Trace, nameLog + msg, t)
  def trace(marker: Marker, msg: String): Unit = Log.log(Trace, nameLog + marker.getLog + msg)
  def trace(marker: Marker, format: String, arg: AnyRef): Unit = Log.log(Trace, nameLog + marker.getLog + String.format(format, arg))
  def trace(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Trace, nameLog + marker.getLog + String.format(format, arg1, arg2))
  def trace(marker: Marker, format: String, arguments: AnyRef*): Unit = Log.log(Trace, nameLog + marker.getLog + String.format(format, arguments))
  def trace(marker: Marker, msg: String, t: Throwable): Unit = Log.log(Trace, nameLog + marker.getLog + msg, t)

  def isDebugEnabled(): Boolean = Log.isEnabled(Debug)
  def isDebugEnabled(marker: Marker): Boolean = isDebugEnabled()
  def debug(msg: String): Unit = Log.log(Debug, nameLog + msg)
  def debug(format: String, arg: AnyRef): Unit = Log.log(Debug, nameLog + String.format(format, arg))
  def debug(format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Debug, nameLog + String.format(format, arg1, arg2))
  def debug(format: String, arguments: AnyRef*): Unit = Log.log(Debug, nameLog + String.format(format, arguments))
  def debug(msg: String, t: Throwable): Unit = Log.log(Debug, nameLog + msg, t)
  def debug(marker: Marker, msg: String): Unit = Log.log(Debug, nameLog + marker.getLog + msg)
  def debug(marker: Marker, format: String, arg: AnyRef): Unit = Log.log(Debug, nameLog + marker.getLog + String.format(format, arg))
  def debug(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Debug, nameLog + marker.getLog + String.format(format, arg1, arg2))
  def debug(marker: Marker, format: String, arguments: AnyRef*): Unit = Log.log(Debug, nameLog + marker.getLog + String.format(format, arguments))
  def debug(marker: Marker, msg: String, t: Throwable): Unit = Log.log(Debug, nameLog + marker.getLog + msg, t)

  def isInfoEnabled(): Boolean = Log.isEnabled(Info)
  def isInfoEnabled(marker: Marker): Boolean = isInfoEnabled()
  def info(msg: String): Unit = Log.log(Info, nameLog + msg)
  def info(format: String, arg: AnyRef): Unit = Log.log(Info, nameLog + String.format(format, arg))
  def info(format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Info, nameLog + String.format(format, arg1, arg2))
  def info(format: String, arguments: AnyRef*): Unit = Log.log(Info, nameLog + String.format(format, arguments))
  def info(msg: String, t: Throwable): Unit = Log.log(Info, nameLog + msg, t)
  def info(marker: Marker, msg: String): Unit = Log.log(Info, nameLog + marker.getLog + msg)
  def info(marker: Marker, format: String, arg: AnyRef): Unit = Log.log(Info, nameLog + marker.getLog + String.format(format, arg))
  def info(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Info, nameLog + marker.getLog + String.format(format, arg1, arg2))
  def info(marker: Marker, format: String, arguments: AnyRef*): Unit = Log.log(Info, nameLog + marker.getLog + String.format(format, arguments))
  def info(marker: Marker, msg: String, t: Throwable): Unit = Log.log(Info, nameLog + marker.getLog + msg, t)

  def isWarnEnabled(): Boolean = Log.isEnabled(Warn)
  def isWarnEnabled(marker: Marker): Boolean = isWarnEnabled()
  def warn(msg: String): Unit = Log.log(Warn, nameLog + msg)
  def warn(format: String, arg: AnyRef): Unit = Log.log(Warn, nameLog + String.format(format, arg))
  def warn(format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Warn, nameLog + String.format(format, arg1, arg2))
  def warn(format: String, arguments: AnyRef*): Unit = Log.log(Warn, nameLog + String.format(format, arguments))
  def warn(msg: String, t: Throwable): Unit = Log.log(Warn, nameLog + msg, t)
  def warn(marker: Marker, msg: String): Unit = Log.log(Warn, nameLog + marker.getLog + msg)
  def warn(marker: Marker, format: String, arg: AnyRef): Unit = Log.log(Warn, nameLog + marker.getLog + String.format(format, arg))
  def warn(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Warn, nameLog + marker.getLog + String.format(format, arg1, arg2))
  def warn(marker: Marker, format: String, arguments: AnyRef*): Unit = Log.log(Warn, nameLog + marker.getLog + String.format(format, arguments))
  def warn(marker: Marker, msg: String, t: Throwable): Unit = Log.log(Warn, nameLog + marker.getLog + msg, t)

  def isErrorEnabled(): Boolean = Log.isEnabled(Error)
  def isErrorEnabled(marker: Marker): Boolean = isErrorEnabled()
  def error(msg: String): Unit = Log.log(Error, nameLog + msg)
  def error(format: String, arg: AnyRef): Unit = Log.log(Error, nameLog + String.format(format, arg))
  def error(format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Error, nameLog + String.format(format, arg1, arg2))
  def error(format: String, arguments: AnyRef*): Unit = Log.log(Error, nameLog + String.format(format, arguments))
  def error(msg: String, t: Throwable): Unit = Log.log(Error, nameLog + msg, t)
  def error(marker: Marker, msg: String): Unit = Log.log(Error, nameLog + marker.getLog + msg)
  def error(marker: Marker, format: String, arg: AnyRef): Unit = Log.log(Error, nameLog + marker.getLog + String.format(format, arg))
  def error(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit = Log.log(Error, nameLog + marker.getLog + String.format(format, arg1, arg2))
  def error(marker: Marker, format: String, arguments: AnyRef*): Unit = Log.log(Error, nameLog + marker.getLog + String.format(format, arguments))
  def error(marker: Marker, msg: String, t: Throwable): Unit = Log.log(Error, nameLog + marker.getLog + msg, t)
}