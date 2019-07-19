package eu.eyan.util.io

import java.io.Writer
import java.util.Timer
import java.util.TimerTask
import eu.eyan.util.scala.Try

object WriterPlus {
  private case class Writing(chars:Array[Char], off:Int, len:Int)
  
  def apply(onWrite: Writing => Unit, onFlush: => Unit, onClose: => Unit) = new Writer {
    def write(chars: Array[Char], off: Int, len: Int) = onWrite(Writing(chars, off, len))
    override def flush = onFlush
    override def close = onClose
  }

  def flushPeriodically(writer: Writer, timeInterval: Int = 100) = {
    val timer = new Timer(s"Flushable flusher interval $timeInterval ms")
    val task = new TimerTask { def run = writer.flush }
    timer.scheduleAtFixedRate(task, timeInterval, timeInterval)
    WriterPlus(writing => writer.write(writing.chars, writing.off, writing.len), writer.flush, { writer.flush; Try(timer.cancel) })
  }
}