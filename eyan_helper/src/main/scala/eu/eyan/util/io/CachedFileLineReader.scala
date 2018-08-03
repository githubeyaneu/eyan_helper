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
import java.io.Closeable
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import scala.collection.mutable.ListBuffer
import eu.eyan.util.scala.Try
import eu.eyan.util.scala.TryFinallyClose

object CachedFileLineReader {
  def apply(file: String) = new CachedFileLineReader().load(file.asFile)
  def apply(file: File) = new CachedFileLineReader().load(file)
  def apply() = new CachedFileLineReader()
}

class CachedFileLineReader extends Iterable[String] with Closeable {

  private val LINE_COUNT_EARLY_READ = 100
  protected var lineOffsets = Vector[Array[Long]]() // TODO make it immutable
  def getLineOffsets = lineOffsets // FIMXE getter for java? for sortable  
  def getLineOffsets(idx: Int) = lineOffsets(idx) // FIMXE getter for java? for sortable

  // FIXME dont use java
  protected val lineCache: java.util.Map[Int, String] = MapsPlus.newMaxSizeHashMap(Runtime.getRuntime().availableProcessors() * LINE_COUNT_EARLY_READ)
  // FIXME must be thread safe!
  // protected val lineCache = MapsPlus.newMaxSizeMutableMap[Int, String](Runtime.getRuntime().availableProcessors() * LINE_COUNT_EARLY_READ) // FIXME: this makes test errors immutable too... 
  val NL = "\r\n"

  private var fileChannel: FileChannel = null
  private var fileInputStream: FileInputStream = null
  private var longestLine = ""

  private def readFromFile(index: Int) = {
    val start = lineOffsets(index)(0) // FIXME slow
    val end = lineOffsets(index)(1) // FIXME slow
    val byteBuffer = ByteBuffer.allocate(Math.toIntExact(end - start))
    fileChannel.read(byteBuffer, start)
    val line = new String(byteBuffer.array, Charset.forName("UTF-8"))
    line
  }

  def get(index: Int) = {
    val line = lineCache.get(index)
    if (line == null) Try
    lineOffsets.synchronized {
      for { i <- index until index + LINE_COUNT_EARLY_READ if i < lineOffsets.size } lineCache.put(i, readFromFile(i))
    }

    lineCache.get(index)
  }

  def load(file: File, loadFileProgressChangedEvent: Int => Unit = i => {}) = {
    val fileLength = file.length

    Log.info("Loading " + file + " " + fileLength + " bytes")

    var progressPercent = 0L
    var longestLineIndex = 0
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
    longestLine = get(longestLineIndex);
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
    lineCache.clear
    longestLine = ""
  }

  def getLongestLine = longestLine

  override def size = lineOffsets.synchronized { lineOffsets.size }

  def lines = iterator
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
    } else null
  }
}