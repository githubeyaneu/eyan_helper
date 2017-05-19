package eu.eyan.util.io

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.regex.Pattern

import eu.eyan.log.Log
import eu.eyan.util.collection.MapsPlus

class CachedFileLineReader extends Iterable[String] {

  private val LINE_COUNT_EARLY_READ = 100
  // FIXME dont use java
  protected var lineOffsets: java.util.List[Array[Long]] = new java.util.ArrayList()
  // FIXME dont use java
  protected val lineCache: java.util.Map[Int, String] = MapsPlus.newMaxSizeHashMap(Runtime.getRuntime().availableProcessors() * LINE_COUNT_EARLY_READ)
  val NL = "\r\n"

  private var fileChannel: FileChannel = null
  private var fileInputStream: FileInputStream = null
  private var longestLine = ""
  private var fileLength: Long = -1

  private def readFromFile(index: Int) = {
    val start = lineOffsets.get(index)(0)
    val end = lineOffsets.get(index)(1)
    val byteBuffer = ByteBuffer.allocate(Math.toIntExact(end - start))
    fileChannel.read(byteBuffer, start)
    val line = new String(byteBuffer.array, Charset.forName("UTF-8"))
    line
  }

  def get(index: Int) = {
    val line = lineCache.get(index)
    if (line == null) {
      try lineOffsets.synchronized {
        for { i <- index until index + LINE_COUNT_EARLY_READ if i < lineOffsets.size } lineCache.put(i, readFromFile(i))
      }
      catch {
        case e: IOException => Log.error(e)
      }
    }
    lineCache.get(index)
  }

  def load(file: File, loadFileProgressChangedEvent: Int => Unit) = {
    fileLength = file.length()

    Log.info("Loading " + file + " " + fileLength + " bytes")

    val lnr = FileLineStartOffsetReader(file)
    var endIndex = 0L
    var startOffset = 0L
    var progressPercent = 0L
    var longestLineIndex = 0
    var longestLineLength = 0L

    lineOffsets.synchronized {
      lineOffsets.clear
      try {
        while (startOffset != -1) {
          startOffset = lnr.readLine
          if (startOffset != -1) {
            endIndex = lnr.getOffset
            lineOffsets.add(Array(startOffset, endIndex))
            if (endIndex - startOffset > longestLineLength) {
              longestLineIndex = lineOffsets.size() - 1
              longestLineLength = endIndex - startOffset
            }
          }

          val newProgressPercent = if (fileLength == 0) 100 else endIndex * 100 / fileLength
          if (progressPercent != newProgressPercent && loadFileProgressChangedEvent != null) {
            loadFileProgressChangedEvent(Math.toIntExact(newProgressPercent))
            progressPercent = newProgressPercent
          }
        }
        lnr.close()

        fileInputStream = new FileInputStream(file)
        fileChannel = fileInputStream.getChannel()
        longestLine = get(longestLineIndex);
        if (fileLength != endIndex) {
          // special characters brake the offsets
          val isActive = Log.isActive
          Log.activate
          Log.error("special characters brake the offsets: " + file.getAbsolutePath)
          Log.error("Length: " + fileLength + " endOffset:" + endIndex + NL + "Error at loading file. There are newline problems! ")
          if (!isActive) Log.deactivate
        }
      }
      catch {
        case e: IOException => e.printStackTrace()
      }
    }
  }

  def close = {
    try {
      if (fileInputStream != null) fileInputStream.close()
      if (fileChannel != null) fileChannel.close()
      lineOffsets.clear
      lineCache.clear
      longestLine = ""
      fileLength = 0
    }
    catch {
      case e: IOException => e.printStackTrace()
    }
  }

  def getLongestLine = longestLine

  override def size = lineOffsets.synchronized { lineOffsets.size }

  def iterator = new Iterator[String] {
    private var index = 0
    def hasNext = lineOffsets.synchronized { index < lineOffsets.size }
    def next = {
      if (lineOffsets.size != 0) Log.debug("Line " + (index + 1) + " " + (100 * (index + 1) / lineOffsets.size) + "%")
      val line = get(index)
      index += 1
      line
    }
  }

  def findFirst(pattern: String) = {
    val matcher = Pattern.compile(pattern).matcher("")

    val first = this.iterator.find(s => { matcher.reset(s.replaceAll(NL, "")); matcher.matches })

    if (first.nonEmpty) {
      val m = Pattern.compile(pattern).matcher(first.get.replaceAll(NL, ""))
      m.matches
      m
    }
    else null
  }
}