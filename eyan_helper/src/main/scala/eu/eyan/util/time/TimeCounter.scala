package eu.eyan.util.time

object TimeCounter {
  def countAndPrint(msg: String)(action: => Unit) = {
    val start = System.currentTimeMillis
    action
    val dur = System.currentTimeMillis - start
    println(s"$msg $dur ms")
  }
}