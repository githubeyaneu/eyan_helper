package eu.eyan.util.backup;

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import eu.eyan.util.io.CloseablePlus

class BackupException(msg: String) extends Exception(msg)

object BackupHelper {
  @throws(classOf[BackupException])
  def zipFile(inputFile: File, to: File) = {
    val buffer = Array.ofDim[Byte](1024)
    var zipOutputStream: ZipOutputStream = null
    var fileInputStream: FileInputStream = null
    // FIXME fájlnév ékezet probléma a zipben
    try {
      zipOutputStream = new ZipOutputStream(new FileOutputStream(to))
      // FIXME: mindig ez a név...
      zipOutputStream.putNextEntry(new ZipEntry("backup.xls"))
      fileInputStream = new FileInputStream(inputFile)
      var len = fileInputStream.read(buffer)
      while (len > 0) {
        zipOutputStream.write(buffer, 0, len)
        len = fileInputStream.read(buffer)
      }
      zipOutputStream.closeEntry()
    } catch {
      case ex: IOException =>
        ex.printStackTrace()
        throw new BackupException("Nem sikerült a biztonsági mentés")
    } finally {
      CloseablePlus.closeQuietly(zipOutputStream)
      CloseablePlus.closeQuietly(fileInputStream)
    }
  }
}