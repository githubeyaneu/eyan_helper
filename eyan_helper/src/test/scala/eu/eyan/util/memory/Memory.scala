package eu.eyan.util.memory

import java.text.NumberFormat

object Memory {
  def printmem = {
    val runtime = Runtime.getRuntime()
    val format = NumberFormat.getInstance()
    val maxMemory = runtime.maxMemory()
    val allocatedMemory = runtime.totalMemory()
    val freeMemory = runtime.freeMemory()
    def f(amount:Long)= format.format(amount / 1024 / 1024) + " MB"
    println("free: " + f(freeMemory)+", allocated: " + f(allocatedMemory)+ ", max: " + f(maxMemory)+ ", total free: " + f(freeMemory + maxMemory - allocatedMemory))
    println
  }
}