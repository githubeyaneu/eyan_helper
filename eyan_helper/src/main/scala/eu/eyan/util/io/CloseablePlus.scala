package eu.eyan.util.io

import java.io.Closeable
import eu.eyan.log.Log

object CloseablePlus {
  // TODO write tests
  def closeQuietly(closeables: Closeable*) =
    if (closeables != null) {
      closeables foreach {
        closeable =>
          try {
            if (closeable != null) closeable.close
            else Log.error("closeable null")
          } catch {
            case t: Throwable => Log.error(s"Cannot close closeable.closeable", t)
          }
      }
    } else Log.error("closeables null")
}