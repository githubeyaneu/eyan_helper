package eu.eyan.util.compress

import java.io.File
import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel
import org.apache.commons.compress.utils.IOUtils

class SevenZipPlus(compressedInputStream: InputStream) extends CompressPlus {
  def extractOneFileTo(filePathToExtract: String, targetFile: File) =
    extractOneFileTo(IOUtils.toByteArray(compressedInputStream), filePathToExtract, targetFile)

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