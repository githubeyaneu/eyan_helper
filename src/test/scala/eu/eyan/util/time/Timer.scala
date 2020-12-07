package eu.eyan.util.time

// TODO: remove it is in the TestPlus...
object Timer {
  val SECOND = 1000L
  val MINUTE = 60L * SECOND

  def timerStart = startTime = now
  def timerElapsed = { val elapsed = readableTime(now - startTime); timerStart; elapsed }
  def timerPrint = println(timerElapsed)

  private def readableTime(ms: Long) = {
    if (ms < SECOND * 10) ms + "ms"
    else if (ms < MINUTE * 10) ms / SECOND + "s"
    else ms / MINUTE + "min"
  }

  private def now = System.currentTimeMillis
  private var startTime = now

  def time(action: => Unit) {
    timerStart
    action
    timerPrint
  }
}