package eu.eyan.util.io

import java.io.OutputStream
import java.io.PrintStream
import java.util.Timer
import java.util.TimerTask
import eu.eyan.util.awt.AwtHelper
import scala.collection.mutable.MutableList
import eu.eyan.util.scala.Try
import eu.eyan.log.Log
import java.io.FileWriter

object OutputStreamPlus {
  def apply(onWrite: Int => Unit, onFlush: => Unit, onClose: => Unit) = new OutputStream {
    def write(i: Int) = onWrite(i)
    override def flush = onFlush
    override def close = onClose
  }

  def timebuffered(callback: String => Unit, time: Int = 100) = {
    var chars = MutableList[Char]()
    def newChar(i: Int) = chars.synchronized { chars += i.toChar }
    def flushCars = chars.synchronized { callback(new String(chars.toArray.map(_.toChar))); chars.clear }
    val timer = new Timer("OutputStream timebuffered")
    val task = new TimerTask { def run = flushCars }
    timer.scheduleAtFixedRate(task, time, time)
    OutputStreamPlus(i => newChar(i), flushCars, { flushCars; Try(timer.cancel) })
  }

  val CR = 13
  val LF = 10

  def carriageReturnReplacer(out: OutputStream) = {
    var lastChar = -1;
    OutputStreamPlus(c => {
      if (c == CR) out.write(LF)
      if (!(c == LF && lastChar == CR)) out.write(c)
      lastChar = c
    }, out.flush, out.close)
  }
}