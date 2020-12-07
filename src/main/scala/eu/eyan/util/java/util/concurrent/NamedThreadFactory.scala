package eu.eyan.util.java.util.concurrent

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

object NamedThreadFactory {
  val poolNumber = new AtomicInteger(1)
}

class NamedThreadFactory(name: String) extends ThreadFactory {
  val threadNumber = new AtomicInteger(0)

  val s = System.getSecurityManager
  val group = if (s != null) s.getThreadGroup else Thread.currentThread().getThreadGroup
  val namePrefix = name+"-pool-" + NamedThreadFactory.poolNumber.getAndIncrement() + "-thread-"

  override def newThread(r: Runnable):Thread = {
    val t = new Thread(group, r, namePrefix + threadNumber.incrementAndGet(), 0)
    if (t.isDaemon) t.setDaemon(false)
    if (t.getPriority != Thread.NORM_PRIORITY) t.setPriority(Thread.NORM_PRIORITY)
    t
  }
}