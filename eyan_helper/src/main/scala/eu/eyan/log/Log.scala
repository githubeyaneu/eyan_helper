package eu.eyan.log

import scala.collection.mutable.MutableList

import eu.eyan.util.string.StringPlus.StringPlusImplicit
import rx.subjects.PublishSubject
import rx.lang.scala.Observable
import rx.subjects.ReplaySubject
import rx.subjects.BehaviorSubject

case class Log(level: LogLevel, text: String)

object Log {
  private lazy val LOG_CLASS_NAME = Log.getClass.getName

  var logToConsole = true
  var errToConsole = true

  private var actualLevel: LogLevel = None
  private val actualLevelPublisher = BehaviorSubject.create[LogLevel](actualLevel)
  lazy val levelObservable = actualLevelPublisher.asObservable

  private val logger = ReplaySubject.create[Log](1000 * 1000)
  lazy val logsObservable = logger.asObservable

  private def log(level: LogLevel, message: => String = ""): Log.type = {
	  lazy val messageText = message
    def stackElementsNotToLog(stackTraceElement: StackTraceElement) = stackTraceElement.getClassName != LOG_CLASS_NAME && !stackTraceElement.getClassName.contains("LogImplicit")
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

  def error(message: => String, t: => Throwable) = {
    lazy val throwable = t
		lazy val logText = message
    def msg = if (logText != "") { logText + " -" } else "" 
    def tName = throwable.getClass.getName
    def tMsg = throwable.getMessage
    def tSt = throwable.getStackTrace.mkString("\r\n  ")
    log(Error, s"$msg $tName: $tMsg\r\n  $tSt")
  }

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