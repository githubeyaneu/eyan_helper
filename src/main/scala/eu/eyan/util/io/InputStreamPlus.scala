package eu.eyan.util.io

import java.io.InputStream
import eu.eyan.util.compress.ZipPlus
import eu.eyan.util.compress.SevenZipPlus
import eu.eyan.util.compress.CompressPlus
import java.io.OutputStream
import scala.annotation.tailrec
import java.io.BufferedReader
import java.io.InputStreamReader
import eu.eyan.log.Log
import eu.eyan.util.java.io.BufferedReaderPlus.BufferedReaderImplicit

object InputStreamPlus {

  implicit class InputStreamPlusImplicit(inputStream: InputStream) {
    def asCompressedStream(extension: String): Option[CompressPlus] =
      if (extension == "zip")
        Some(new ZipPlus(inputStream))
      else if (extension == "7z")
        Some(new SevenZipPlus(inputStream))
      else None

    @tailrec
    final def copyTo(output: OutputStream, buffer: Array[Byte], progressCallback: Long => Unit, progress: Long = 0): Long = {
      val n = inputStream.read(buffer)
      if (n == -1) {
        progressCallback(progress)
        progress
      } else {
        output.write(buffer, 0, n)
        progressCallback(progress + n)
        copyTo(output, buffer, progressCallback, progress + n)
      }
    }

    def readLinesUntilEmtpyLine = new BufferedReader(new InputStreamReader(inputStream)).readLinesUntilEmtpy 
  }
}