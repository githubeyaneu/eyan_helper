package eu.eyan.util.io

import java.io.Closeable
import eu.eyan.log.Log

object CloseablePlus {
  
  implicit class CloseablePlusImplicit[CLOSEABLE <: Closeable](closeable:CLOSEABLE) {
    def closeQuietly = CloseablePlus.closeQuietly(closeable)
  }
  
  // TODO write tests
  def closeQuietly(closeables: Closeable*) =
    if (closeables != null) {
      closeables foreach {
        closeable =>
          try {
            if (closeable != null) closeable.close
            else Log.error("closeable null") //TODO is it quiet?
          } catch {
            case t: Throwable => Log.error(s"Cannot close closeable.closeable", t)
          }
      }
    } else Log.error("closeable_S_ null")//TODO is it quiet?
}