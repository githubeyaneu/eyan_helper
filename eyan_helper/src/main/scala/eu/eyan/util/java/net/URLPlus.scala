package eu.eyan.util.java.net

import java.net.URL
import java.io.File
import java.io.FileOutputStream
import eu.eyan.util.scala.TryFinally
import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit

object URLPlus {

  implicit class URLImplicit(url: URL) {
    def saveToFile(destination: File, progressCallback: Int => Unit = i => {}) = {
      val input = url.openStream
      val output = new FileOutputStream(destination, false)
      val buffer = new Array[Byte](65536)
      TryFinally(input.copyTo(output, buffer, progressCallback), { output.close; input.close })
      url
    }
  }

}