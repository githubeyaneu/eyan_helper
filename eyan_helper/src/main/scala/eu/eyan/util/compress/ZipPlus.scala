package eu.eyan.util.compress

import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.io.InputStream
import java.io.File
import java.io.FileInputStream
import eu.eyan.util.scala.TryFinally
import eu.eyan.util.scala.TryCatchFinally
import eu.eyan.log.Log
import eu.eyan.util.scala.CloseFinally
import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import java.util.zip.ZipEntry

object ZipPlus {
  def listFiles(file: File) = {
    CloseFinally(new ZipInputStream(new FileInputStream(file)), (zip: ZipInputStream) => {
      val list = ListBuffer[ZipEntry]()
      var ze = zip.getNextEntry
      while (ze != null) {
        list += ze
        ze = zip.getNextEntry
      }
      list.toList
    })
  }
}

class ZipPlus(compressedInputStream: InputStream) extends CompressPlus {
  val _8KB = 8192

  def extractOneFileTo(filePathToExtract: String, targetFile: File) = {
    val zip = new ZipInputStream(compressedInputStream)
    var ze = zip.getNextEntry
    while (ze != null) {
      if (filePathToExtract.equals(ze.getName)) {
        val out = new FileOutputStream(targetFile, false)
        val buffer = new Array[Byte](_8KB)
        var len = zip.read(buffer)
        while (len != -1) {
          out.write(buffer, 0, len);
          len = zip.read(buffer)
        }
        out.close
      }
      ze = zip.getNextEntry
    }
    zip.close
    targetFile
  }
}