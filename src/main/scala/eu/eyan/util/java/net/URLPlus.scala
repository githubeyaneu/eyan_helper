package eu.eyan.util.java.net

import java.io.File
import java.io.FileOutputStream
import java.net.URL

import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit
import eu.eyan.util.scala.TryCatchFinallyClose
import java.io.InputStream
import eu.eyan.log.Log

object URLPlus {

  implicit class URLImplicit(url: URL) {

    def saveToFile(destination: File, progressCallback: Long => Unit = l => {}) = {
      TryCatchFinallyClose(
        url.openStream, new FileOutputStream(destination, false),
        (input: InputStream, output: FileOutputStream) => input.copyTo(output, new Array[Byte](65536), progressCallback),
        t => Log.error(s"Cannot save URL($url) to file($destination)", t))
      url
    }
  }
}