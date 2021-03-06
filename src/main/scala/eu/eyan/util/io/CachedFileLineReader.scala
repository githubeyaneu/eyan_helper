package eu.eyan.util.io

import java.io.{Closeable, File, FileInputStream}
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.util.regex.Pattern

import eu.eyan.log.Log
import eu.eyan.util.collection.MapsPlus._
import eu.eyan.util.java.lang.RuntimePlus._
import eu.eyan.util.scala.{Try, TryFinallyClose}
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object CachedFileLineReader {
  def apply(file: String) = new CachedFileLineReader().load(file.asFile)
  def apply(file: File) = new CachedFileLineReader().load(file)
  def apply() = new CachedFileLineReader()
}

//FIXME make it immutable so dont need synchronized around lineOffsets
class CachedFileLineReader extends Closeable with Iterable[String] {

  private val LINE_COUNT_EARLY_READ = 100
  protected var lineOffsets = Vector[Array[Long]]()
  var longestLineIndex = 0

  // FIXME must be thread safe!
  protected var lineCache = clearLineCache
  protected def clearLineCache = maxSizeImmutableMap[Int, String](availableProcessors * LINE_COUNT_EARLY_READ)
  // FIXME: speed up: idea: new dataconstruct: availableProcessors number of LINE_COUNT_EARLY_READ(or bigbuffer) arrays for lines
  val NL = "\r\n"

  private var fileChannel: FileChannel = _
  private var fileInputStream: FileInputStream = _

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
    val lastLineIndex = Math.min(endLineIndex, lineOffsets.size - 1)
    val start = getLineStartEnd(startLineIndex)(0)
    val end = getLineStartEnd(lastLineIndex)(1)

    val byteBuffer = allocateBuffer(start, end)
    fileChannelRead(byteBuffer, start)
    val linesArray = byteBuffer.array
    
    def getLineFromBuffer(index:Int) = {
      val lineArray = linesArray.slice((lineOffsets(index)(0) - start).toInt, (lineOffsets(index)(1) - start).toInt)
      val line = bytesToString(lineArray)
      (index, line)
    }
    
    val lines = (startLineIndex to lastLineIndex) map getLineFromBuffer
    lines
  }

  def getLineStartEnd(index: Int) = lineOffsets(index) // FIXME slow ?
  def allocateBuffer(start: Long, end: Long) = ByteBuffer.allocate(Math.toIntExact(end - start))
  def fileChannelRead(byteBuffer: ByteBuffer, start: Long) = fileChannel.read(byteBuffer, start)
  def createLine(byteBuffer: ByteBuffer): String = bytesToString(byteBuffer.array)
  def bytesToString(array: Array[Byte]) = new String(array, Charset.forName("UTF-8"))

  def get(index: Int) = {
    val line = lineCache.get(index)
    def linesFromFile = readFromFile(index, index + 1 + LINE_COUNT_EARLY_READ)
    def extendCache = lineCache ++= linesFromFile
    if (line.isEmpty) Try(extendCache)

    lineCache.get(index)
  }

  //FIXME only for sortable, because it hacks the lineOffsets ! cannot preread more lines efficiently.
  def getOneLine(index: Int) = {
    val line = lineCache.get(index)
    if (line.isEmpty) Try {
      lineOffsets.synchronized {
        lineCache ++= readFromFile(index, index)
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
    fileChannel = fileInputStream.getChannel
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
//    lineCache = Map()
    lineCache = clearLineCache
  }

  def getLongestLine = get(longestLineIndex)

  override def size = lineOffsets.synchronized { lineOffsets.size }
  /** For iterable trait */
  override def iterator = lines

  def lines = new Iterator[String] {
    private var index = 0
    def hasNext = index < CachedFileLineReader.this.size
    def next = {
      if (lineOffsets.nonEmpty) Log.debug("Line " + (index + 1) + " " + (100 * (index + 1) / lineOffsets.size) + "%")
      val line = get(index)
      index += 1
      line.get
    }
  }

  def findFirst(pattern: String) = {
    val patternMatcher = Pattern.compile(pattern).matcher("")
    def patternMatches(line: String) = { patternMatcher.reset(line.replaceAll(NL, "")); patternMatcher.matches }
    val first = this.lines.find(patternMatches)

    if (first.nonEmpty) {
      val m = Pattern.compile(pattern).matcher(first.get.replaceAll(NL, ""))
      m.matches
      m
    } else null
  }
}