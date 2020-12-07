package eu.eyan.util.time

import eu.eyan.util.time.Timer.{now, readableTime}


// TODO: remove it is in the TestPlus...
object Timer {
  val SECOND = 1000L
  val MINUTE = 60L * SECOND

  def timerStart() = new Timer(now)

  private def readableTime(ms: Long) = {
    if (ms < SECOND * 10) ms + "ms"
    else if (ms < MINUTE * 10) ms / SECOND + "s"
    else ms / MINUTE + "min"
  }

  private def now = System.currentTimeMillis
}
class Timer(var startTime: Long) {
  def timerElapsed = {
    val elapsed = readableTime(now - startTime)
    startTime = now
    elapsed
  }
  def timerPrint() = println(timerElapsed)
}