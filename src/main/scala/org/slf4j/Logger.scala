package org.slf4j
trait Logger {
  val ROOT_LOGGER_NAME: String = "ROOT"
  def getName(): String
  def isTraceEnabled(): Boolean
  def trace(msg: String): Unit
  def trace(format: String, arg: AnyRef): Unit
  def trace(format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def trace(format: String, arguments: AnyRef*): Unit
  def trace(msg: String, t: Throwable): Unit
  def isTraceEnabled(marker: Marker): Boolean
  def trace(marker: Marker, msg: String): Unit
  def trace(marker: Marker, format: String, arg: AnyRef): Unit
  def trace(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def trace(marker: Marker, format: String, argArray: AnyRef*): Unit
  def trace(marker: Marker, msg: String, t: Throwable): Unit
  def isDebugEnabled(): Boolean
  def debug(msg: String): Unit
  def debug(format: String, arg: AnyRef): Unit
  def debug(format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def debug(format: String, arguments: AnyRef*): Unit
  def debug(msg: String, t: Throwable): Unit
  def isDebugEnabled(marker: Marker): Boolean
  def debug(marker: Marker, msg: String): Unit
  def debug(marker: Marker, format: String, arg: AnyRef): Unit
  def debug(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def debug(marker: Marker, format: String, arguments: AnyRef*): Unit
  def debug(marker: Marker, msg: String, t: Throwable): Unit
  def isInfoEnabled(): Boolean
  def info(msg: String): Unit
  def info(format: String, arg: AnyRef): Unit
  def info(format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def info(format: String, arguments: AnyRef*): Unit
  def info(msg: String, t: Throwable): Unit
  def isInfoEnabled(marker: Marker): Boolean
  def info(marker: Marker, msg: String): Unit
  def info(marker: Marker, format: String, arg: AnyRef): Unit
  def info(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def info(marker: Marker, format: String, arguments: AnyRef*): Unit
  def info(marker: Marker, msg: String, t: Throwable): Unit
  def isWarnEnabled(): Boolean
  def warn(msg: String): Unit
  def warn(format: String, arg: AnyRef): Unit
  def warn(format: String, arguments: AnyRef*): Unit
  def warn(format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def warn(msg: String, t: Throwable): Unit
  def isWarnEnabled(marker: Marker): Boolean
  def warn(marker: Marker, msg: String): Unit
  def warn(marker: Marker, format: String, arg: AnyRef): Unit
  def warn(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def warn(marker: Marker, format: String, arguments: AnyRef*): Unit
  def warn(marker: Marker, msg: String, t: Throwable): Unit
  def isErrorEnabled(): Boolean
  def error(msg: String): Unit
  def error(format: String, arg: AnyRef): Unit
  def error(format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def error(format: String, arguments: AnyRef*): Unit
  def error(msg: String, t: Throwable): Unit
  def isErrorEnabled(marker: Marker): Boolean
  def error(marker: Marker, msg: String): Unit
  def error(marker: Marker, format: String, arg: AnyRef): Unit
  def error(marker: Marker, format: String, arg1: AnyRef, arg2: AnyRef): Unit
  def error(marker: Marker, format: String, arguments: AnyRef*): Unit
  def error(marker: Marker, msg: String, t: Throwable): Unit
}
