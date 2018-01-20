package eu.eyan.util.compress

import java.io.File
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.apache.commons.compress.utils.IOUtils
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import scala.collection.mutable.ListBuffer
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry
import eu.eyan.util.scala.TryCatchFinallyClose
import eu.eyan.log.Log

object SevenZipPlus {
  def listFiles(file: File) = {
    TryCatchFinallyClose({new SevenZFile(file)}, (sevenZFile: SevenZFile) => {
      val list = ListBuffer[SevenZArchiveEntry]()
      var entry = sevenZFile.getNextEntry
      while (entry != null) {
        list += entry
        entry = sevenZFile.getNextEntry
      }
      sevenZFile.close
      list.toList
    }
    ,t=>{Log.error("Error extracting "+file);List()}
    )
  }
}

class SevenZipPlus(compressedInputStream: InputStream) extends CompressPlus {
  def extractOneFileTo(filePathToExtract: String, targetFile: File) =
    extractOneFileTo(IOUtils.toByteArray(compressedInputStream), filePathToExtract, targetFile)

  //  def copy(input: InputStream, output: OutputStream, buffersize: Int): Long = {
  //    val buffer = new Array[Byte](buffersize)
  //    var n = 0
  //    var count = 0L
  //    n = input.read(buffer)
  //    while (-1 != n) {
  //      output.write(buffer, 0, n)
  //      count += n
  //      n = input.read(buffer)
  //    }
  //    count
  //  }
  //  def copy(input: InputStream, output: OutputStream): Long = copy(input, output, 8024)
  //  def toByteArray(input: InputStream): Array[Byte] = {
  //
  //    val output = new ByteArrayOutputStream()
  //    copy(input, output)
  //    output.toByteArray()
  //  }

  def extractOneFileTo(bytes: Array[Byte], filePathToExtract: String, targetFile: File): File = {
    val channel = new SeekableInMemoryByteChannel(bytes)
    try
      extractOneFileTo(channel, filePathToExtract, targetFile)
    finally
      channel.close
  }

  def extractOneFileTo(channel: SeekableInMemoryByteChannel, filePathToExtract: String, targetFile: File) = {
    val sevenZFile = new SevenZFile(channel)
    var entry = sevenZFile.getNextEntry()
    while (entry != null) {
      val content = new Array[Byte](entry.getSize.toInt)
      sevenZFile.read(content, 0, content.length)
      if (entry.getName.equals(filePathToExtract)) {
        val out = new FileOutputStream(targetFile, false)
        out.write(content)
        out.close
      }
      entry = sevenZFile.getNextEntry()
    }
    sevenZFile.close
    targetFile
  }
}