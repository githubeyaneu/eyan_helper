package eu.eyan.util.io

import java.io.Closeable
import eu.eyan.log.Log
import eu.eyan.util.scala.TryCatch
import eu.eyan.util.scala.Try

object CloseablePlus {
  def closeQuietly(closeable: Closeable) = Try( if(closeable!=null) closeable.close else Log.error("closeable null"))
}