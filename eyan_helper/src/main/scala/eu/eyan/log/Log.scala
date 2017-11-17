package eu.eyan.log

object Log {
  private val STACK_LEVEL = 3
  
  var actualLevel: LogLevel = None
  private var isWithPrevTime: Boolean = false
  private var prevTime = System.currentTimeMillis

  private def stack = Thread.currentThread().getStackTrace()(STACK_LEVEL)
  private def prevTimeLog = if (isWithPrevTime) { " " + (System.currentTimeMillis - prevTime) } else ""

  private def log(level: LogLevel, message: String = ""):Log.type = {
    if (actualLevel.shouldLog(level)) {
  		prevTime = System.currentTimeMillis
      val logText = stack.getClassName.substring(stack.getClassName.lastIndexOf(".") + 1) + "." + stack.getMethodName + ": " + message
      println(f"$level%-5s $prevTimeLog $logText")
      LogWindow.add(logText)
    }
    this
  }

  def start = activate
  def stop = deactivate
  def enable = activate
  def disable = deactivate
  def activate: Log.type = activate(Debug)
  def deactivate = activate(None)
  def withPrevTime = { isWithPrevTime = true; this }
  def activate(level: LogLevel = Debug): Log.type = { actualLevel = level; LogWindow.setLevel(level); this }

  def activateDebugLevel = activate
  def activateInfoLevel = activate(Info)
  def activateErrorLevel = activate(Error)
  def activateWarnLevel = activate(Warn)
  def activateTraceLevel = activate(Trace)

  def error = log(Error)
  def error(message: String) = log(Error, message)
  def error(exception: Throwable) = log(Error, exception.getClass.getName+ ": " +exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
  def error(message: String, exception: Throwable) = log(Error, message + " - " + exception.getMessage + "\r\n  " + exception.getStackTrace.mkString("  \r\n"))
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

abstract class LogLevel(val prio: Int) {
  def shouldLog(otherLevel: LogLevel) = otherLevel.prio <= prio
}
case object None extends LogLevel(100)
case object Fatal extends LogLevel(200)
case object Error extends LogLevel(300)
case object Warn extends LogLevel(400)
case object Info extends LogLevel(500)
case object Debug extends LogLevel(600)
case object Trace extends LogLevel(700)