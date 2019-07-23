package eu.eyan.util.compress

import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.io.InputStream
import java.io.File
import java.io.FileInputStream

import eu.eyan.util.scala.TryCatchFinally
import eu.eyan.log.Log

import scala.collection.mutable.MutableList
import scala.collection.mutable.ListBuffer
import java.util.zip.ZipEntry

import eu.eyan.util.scala.TryCatchFinallyClose
import java.util.zip.GZIPInputStream

import scala.io.Source
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayInputStream

import scala.collection.mutable
import scala.io.BufferedSource
import scala.util.Failure
import scala.util.Try
import scala.util.Success
import scala.util.control.Breaks

object ZipPlus {
  def listFiles(file: File) = {
    TryCatchFinallyClose({ new ZipInputStream(new FileInputStream(file)) }, (zip: ZipInputStream) => {
      val list = ListBuffer[ZipEntry]()
      var ze = zip.getNextEntry
      while (ze != null) {
        list += ze
        ze = zip.getNextEntry
      }
      list.toList
    }, t => { Log.error("Error extracting " + file); List() })
  }

  def unzipToString(file: File) = {
    TryCatchFinallyClose(
      Source.fromInputStream(new GZIPInputStream(new FileInputStream(file))),
      (source: BufferedSource) => Success(source.getLines.mkString),
      t => { Log.error("Error unzipping input array", t); Failure(t) })
  }

  def unzipToString(input: Array[Byte]) = {
    TryCatchFinallyClose(
      Source.fromInputStream(new GZIPInputStream(new ByteArrayInputStream(input))),
      (source: BufferedSource) => Success(source.getLines.mkString),
      t => { Log.error("Error unzipping input array", t); Failure(t) })
  }

  case class NameAndContent(filename: String, content: Array[Byte])

  def unTarAllFilesToMemory(tarPath: File) = {
    val debInputStream = new ArchiveStreamFactory().createArchiveInputStream("tar", new FileInputStream(tarPath)).asInstanceOf[TarArchiveInputStream]

    val files = new mutable.MutableList[NameAndContent]
    var entry: TarArchiveEntry = debInputStream.getNextEntry.asInstanceOf[TarArchiveEntry]
    while (entry != null) {
      if (entry.isFile) {
        val name = entry.getName
        val bytes = IOUtils.toByteArray(debInputStream)
        files += NameAndContent(name, bytes)
      }
      entry = debInputStream.getNextEntry.asInstanceOf[TarArchiveEntry]
    }
    debInputStream.close
    files
  }
}

class ZipPlus(compressedInputStream: InputStream) extends CompressPlus {
  val _8KB = 8192

  def extractOneFileTo(filePathToExtract: String, targetFile: File) = {
    Log.debug(filePathToExtract, targetFile)
    val zip = new ZipInputStream(compressedInputStream)
    var ze = zip.getNextEntry
    var result: Option[File] = None
    Breaks.breakable {
      while (ze != null) {
        if (filePathToExtract.equals(ze.getName)) {
          Log.debug("found " + ze.getName)
          val out = new FileOutputStream(targetFile, false)
          val buffer = new Array[Byte](_8KB)
          var len = zip.read(buffer)
          while (len != -1) {
            out.write(buffer, 0, len)
            len = zip.read(buffer)
          }
          out.close
          Log.debug("extracted to " + targetFile)
          result = Option(targetFile)
          Breaks.break
        }
        ze = zip.getNextEntry
      }
    }
    zip.close
    targetFile // FIXME: result
  }
}