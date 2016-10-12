package eu.eyan.util.time

object Timer {
  val SECOND = 1000L
  val MINUTE = 60L * SECOND

  def timerStart = startTime = now
  def timerElapsed = { val elapsed = readableTime(now - startTime); timerStart; elapsed}
  def timerPrint = println(timerElapsed)

  private def readableTime(ms: Long) = {
    if (ms < SECOND) ms + "ms"
    else if (ms < MINUTE) ms / SECOND + "s"
    else ms / MINUTE + "min"
  }
  
  private def now = System.currentTimeMillis
  private var startTime = now
}