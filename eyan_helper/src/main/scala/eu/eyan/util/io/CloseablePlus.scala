package eu.eyan.util.io

import java.io.Closeable
import eu.eyan.log.Log

object CloseablePlus {
  def closeQuietly(closeable: Closeable) =
    try {
      closeable.close()
    } catch {
      case e: Exception => Log.error(e)
    }
}