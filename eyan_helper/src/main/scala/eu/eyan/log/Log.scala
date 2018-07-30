package eu.eyan.log

import scala.collection.mutable.MutableList

import eu.eyan.util.string.StringPlus.StringPlusImplicit
import rx.subjects.PublishSubject

case class Log(level: LogLevel, text: String)

object Log {
  private lazy val LOG_CLASS_NAME = Log.getClass.getName

  private var actualLevel: LogLevel = None
  def getActualLevel = actualLevel

  var logToConsole = true
  var errToConsole = true

  private val logger = PublishSubject.create[Log]
  val logsObservable = logger.asObservable
  private val actualLevelPublisher = PublishSubject.create[LogLevel]
  val levelObservable = actualLevelPublisher.asObservable
  actualLevelPublisher.onNext(actualLevel)

  private def log(level: LogLevel, message: String = ""): Log.type = {
    def stackElementsNotToLog(stackTraceElement: StackTraceElement) = stackTraceElement.getClassName != LOG_CLASS_NAME
    def stackElementWhereLogWasCalled = Thread.currentThread.getStackTrace.filter(stackElementsNotToLog)(1) // TODO use lift
    def stackClassAndMethod = stackElementWhereLogWasCalled.getClassName.substring(stackElementWhereLogWasCalled.getClassName.lastIndexOf(".") + 1) + "." + stackElementWhereLogWasCalled.getMethodName
    def logText = stackClassAndMethod + (if (message != "") { ": " + message } else "")
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

  def activate(level: LogLevel = Debug): Log.type = { actualLevel = level; actualLevelPublisher.onNext(level); this }

  def activateNone = activate(None)
  def activateFatalLevel = activate(Fatal)
  def activateErrorLevel = activate(Error)
  def activateWarnLevel = activate(Warn)
  def activateInfoLevel = activate(Info)
  def activateDebugLevel = activate(Debug)
  def activateTraceLevel = activate(Trace)
  def activateAllLevel = activateTraceLevel

  def fatal = log(Fatal)
  def fatal(o: Object) = log(Fatal, String.valueOf(o))

  def error = log(Error)
  def error(message: Any): Log.type =
    message match {
      case t: Throwable => error("", t)
      case o: Any       => log(Error, String.valueOf(o))
    }

  def error(message: String, t: Throwable) = {
    val msg = if (message != "") { message + " -" } else ""
    val tName = t.getClass.getName
    val tMsg = t.getMessage
    val tSt = t.getStackTrace.mkString("\r\n  ")
    log(Error, s"$msg $tName: $tMsg\r\n  $tSt")
  }

  def warn = log(Warn)
  def warn(o: Object) = log(Warn, String.valueOf(o))

  def info = log(Info)
  def info(o: Object) = log(Info, String.valueOf(o))

  def debug = log(Debug)
  def debug(o: Object) = log(Debug, String.valueOf(o))

  def trace = log(Trace)
  def trace(o: Object) = log(Trace, String.valueOf(o))

  
  def logToConsoleText(log: Log) = f"${log.level}%-5s ${log.text}"
  logsObservable.filter(_.level > Error).subscribe(log => { logToConsoleText(log).println })
  logsObservable.filter(_.level <= Error).subscribe(log => { logToConsoleText(log).printlnErr })
  logsObservable.subscribe(log => saveLog(log))
  
  val allLogs = MutableList[Log]() // TODO Memory leak, make fixed list / fixed size
  def saveLog(log: Log) = allLogs += log 
  def getAllLogs = allLogs.map(logToConsoleText).mkString("\r\n") 
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