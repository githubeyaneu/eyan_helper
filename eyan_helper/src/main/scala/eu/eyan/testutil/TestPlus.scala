package eu.eyan.testutil

object TestPlus {
  val DEFAULT_WAIT_TIME = 1000
  val DEFAULT_SLEEP_TIME = 10
  def waitFor(assertion: () => Unit, timeout: Long = DEFAULT_WAIT_TIME): Unit = {
    val start = System.currentTimeMillis()
    def elapsedTime = System.currentTimeMillis() - start
    var ok = false
    while (!ok)
      try { assertion(); ok = true }
      catch { case c: AssertionError => if (timeout < elapsedTime) throw c else Thread.sleep(DEFAULT_SLEEP_TIME) }
  }
}