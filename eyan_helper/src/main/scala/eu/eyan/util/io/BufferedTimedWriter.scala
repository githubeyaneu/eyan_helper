package eu.eyan.util.io

import java.io.Writer

object BufferedTimedWriter {
  def apply(writer: Writer, timeInterval: Int = 100) = WriterPlus.flushPeriodically(writer, timeInterval)
}