package eu.eyan.testutil

object TestPlus {
  def waitFor(assertion: () => Unit, timeout: Long = 1000): Unit = {
    val start = System.currentTimeMillis()
    def elapsedTime = System.currentTimeMillis() - start
    while (true)
      try { assertion(); return }
      catch { case c: AssertionError => if (timeout < elapsedTime) throw c else Thread.sleep(10) }
  }
}