package eu.eyan.util.io

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.regex.Pattern

import eu.eyan.log.Log
import eu.eyan.util.collection.MapsPlus._
import java.io.Closeable
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import scala.collection.mutable.ListBuffer
import eu.eyan.util.scala.Try
import eu.eyan.util.scala.TryFinallyClose
import eu.eyan.util.java.lang.RuntimePlus._

object CachedFileLineReader {
  def apply(file: String) = new CachedFileLineReader().load(file.asFile)
  def apply(file: File) = new CachedFileLineReader().load(file)
  def apply() = new CachedFileLineReader()
}

class CachedFileLineReader extends Iterable[String] with Closeable {

  private val LINE_COUNT_EARLY_READ = 100
  protected var lineOffsets = Vector[Array[Long]]()
  var longestLineIndex = 0

  def getLineOffsets = lineOffsets // FIMXE getter for java? for sortable
  def getLineOffsets(idx: Int) = lineOffsets(idx) // FIMXE getter for java? for sortable

  // FIXME must be thread safe!
//  private val lineCache = maxSizeMutableMap[Int, String](availableProcessors * LINE_COUNT_EARLY_READ) 
  private var lineCache = maxSizeImmutableMap[Int, String](availableProcessors * LINE_COUNT_EARLY_READ) 
  val NL = "\r\n"

  private var fileChannel: FileChannel = null
  private var fileInputStream: FileInputStream = null

  private def readFromFile(index: Int) = {
    val startEnd = getLineStartEnd(index)
    val start = startEnd(0)
    val end = startEnd(1)
    val byteBuffer = allocateBuffer(start, end)
    fileChannelRead(byteBuffer, start)
    val line = createLine(byteBuffer)
    line
  }

  private def readFromFile(startLineIndex: Int, endLineIndex: Int) = {
    val lastIndex = Math.min(endLineIndex, lineOffsets.size - 1)
    val start = getLineStartEnd(startLineIndex)(0)
    val end = getLineStartEnd(lastIndex)(1)

    val byteBuffer = allocateBuffer(start, end)
    fileChannelRead(byteBuffer, start)
    val linesArray = byteBuffer.array
    val lines = for (index <- startLineIndex to lastIndex) yield {
      val lineArray = linesArray.slice((lineOffsets(index)(0) - start).toInt, (lineOffsets(index)(1) - start).toInt)
      val line = createLine(lineArray)
      (index, line)
    }
    lines
  }

  def getLineStartEnd(index: Int) = lineOffsets(index) // FIXME slow ?
  def allocateBuffer(start: Long, end: Long) = ByteBuffer.allocate(Math.toIntExact(end - start))
  def fileChannelRead(byteBuffer: ByteBuffer, start: Long) = fileChannel.read(byteBuffer, start)
  def createLine(byteBuffer: ByteBuffer): String = createLine(byteBuffer.array)
  def createLine(array: Array[Byte]) = new String(array, Charset.forName("UTF-8"))

  def get(index: Int) = {
    val line = lineCache.get(index)
    if (line.isEmpty) Try {
      lineOffsets.synchronized {
        lineCache ++= readFromFile(index, index + 1 + LINE_COUNT_EARLY_READ)
      }
    }

    lineCache.get(index)
  }

  def load(file: File, loadFileProgressChangedEvent: Int => Unit = i => {}) = {
    close
    val fileLength = file.length

    Log.info("Loading " + file + " " + fileLength + " bytes")

    var progressPercent = 0L
    var longestLineLength = 0L
    var endIndex = 0L

    //TODO exchange to observable
    def readerCallback(index: Int, startOffset: Long, endOffset: Long) = {
      if (endOffset - startOffset > longestLineLength) {
        longestLineIndex = index
        longestLineLength = endOffset - startOffset
      }
      val newProgressPercent = if (fileLength == 0) 100 else endOffset * 100 / fileLength
      if (progressPercent != newProgressPercent && loadFileProgressChangedEvent != null) {
        loadFileProgressChangedEvent(Math.toIntExact(newProgressPercent))
        progressPercent = newProgressPercent
      }
      endIndex = endOffset
    }

    TryFinallyClose(FileLineStartOffsetReader(file), { lnr: FileLineStartOffsetReader => { lineOffsets = lnr.iterator(readerCallback).toVector } })

    fileInputStream = new FileInputStream(file)
    fileChannel = fileInputStream.getChannel()
    if (fileLength != endIndex) {
      // special characters brake the offsets
      Log.error("special characters brake the offsets: " + file.getAbsolutePath)
      Log.error("Length: " + fileLength + " endOffset:" + endIndex + NL + "Error at loading file. There are newline problems! ")
    }

    this
  }

  def close = Try {
    CloseablePlus.closeQuietly(fileInputStream, fileChannel)
    lineOffsets = Vector[Array[Long]]()
    lineCache = Map()
  }

  def getLongestLine = get(longestLineIndex)

  override def size = lineOffsets.synchronized { lineOffsets.size }

  def lines = iterator
  def iterator = new Iterator[String] {
    private var index = 0
    def hasNext = lineOffsets.synchronized { index < lineOffsets.size }
    def next = {
      if (lineOffsets.size != 0) Log.debug("Line " + (index + 1) + " " + (100 * (index + 1) / lineOffsets.size) + "%")
      val line = get(index)
      index += 1
      line.get
    }
  }

  def findFirst(pattern: String) = {
    val matcher = Pattern.compile(pattern).matcher("")

    val first = this.iterator.find(s => { matcher.reset(s.replaceAll(NL, "")); matcher.matches })

    if (first.nonEmpty) {
      val m = Pattern.compile(pattern).matcher(first.get.replaceAll(NL, ""))
      m.matches
      m
    } else null
  }
}