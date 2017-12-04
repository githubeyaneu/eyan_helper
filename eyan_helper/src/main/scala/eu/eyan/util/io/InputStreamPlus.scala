package eu.eyan.util.io

import java.io.InputStream
import eu.eyan.util.compress.ZipPlus
import eu.eyan.util.compress.SevenZipPlus
import eu.eyan.util.compress.CompressPlus
import java.io.OutputStream

object InputStreamPlus {

  implicit class InputStreamPlusImplicit(inputStream: InputStream) {
    def asCompressedStream(extension: String): Option[CompressPlus] =
      if (extension == "zip")
        Some(new ZipPlus(inputStream))
      else if (extension == "7z")
        Some(new SevenZipPlus(inputStream))
      else None

    def copyTo(output: OutputStream, buffer: Array[Byte], progressCallback: Int => Unit): Long = {
      val n = inputStream.read(buffer)
      if (n == -1) 0
      else {
        output.write(buffer, 0, n)
        progressCallback(n)
        n + copyTo(output, buffer, progressCallback)
      }
    }

  }

}