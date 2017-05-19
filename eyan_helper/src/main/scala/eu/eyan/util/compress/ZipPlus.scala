package eu.eyan.util.compress

import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.io.InputStream
import java.io.File

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