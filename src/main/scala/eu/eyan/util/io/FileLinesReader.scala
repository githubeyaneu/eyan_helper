package eu.eyan.util.io

import java.io.File
import java.io.Closeable
import java.io.FileReader
import java.io.FileInputStream
import scala.collection.mutable.MutableList
import java.util.Arrays

object FileLinesReader {
  def apply(file: File) = new FileLinesReader(file.getAbsolutePath, 64 * 1024)
  def apply(file: String, bufferSize: Int) = new FileLinesReader(file, bufferSize)
}

class FileLinesReader private (file: String, bufferSize: Int) extends Closeable {
  val EGAL = 'x'
  private var offset = 0L

  private val byteBuffer = new Array[Byte](bufferSize)
  private val fis = new FileInputStream(file)

  private var lastChar = EGAL
  private var bufferOffset = 0
  private var bufferLength = 0
  private var byteBufferLength = 0
  private var nextLineLength = 0L
  private var lineBytes: MutableList[Byte] = MutableList()

  def readLines(): Stream[String] = {
    val nextLine = readLine
    if (nextLine == null) Stream.Empty
    else Stream.cons(new String(nextLine), readLines)
  }

  //  /**
  //   * TODO: this should be only used, the readline and get should not...
  //   * @return
  //   */
  //  def iterator(progress: (Int, Long, Long) => Unit) = new Iterator[Array[Long]] {
  //    var index = 0
  //    var startOffset = readLine
  //    def hasNext: Boolean = startOffset != -1
  //    def next(): Array[Long] = {
  //      val ret = Array(startOffset, offset)
  //      progress(index, startOffset, offset)
  //      index += 1
  //      startOffset = readLine
  //      ret
  //    }
  //  }

  override def close = fis.close

  /**
   * @return start offset from the line. After calling this method the end
   *         offset of the line can be read out with the getOffset():long
   * @throws IOException
   */
  def readLine: Array[Byte] = {
    var ready = false
    val INVALID_RESULT = -11L
    var result: Array[Byte] = null

    while (!ready) {
      val next = nextByte
      if (next == -1)
        if (nextLineLength == 0) ready = true
        else {
          val ret = offset
          offset += nextLineLength
          nextLineLength = 0
          result = lineBytes.toArray
          lineBytes.clear 
          ready = true
        }
      else {
        val nextChar = next.toChar
        if (nextChar == '\n') {
          nextLineLength += 1
          lastChar = EGAL
          val ret = offset
          offset += nextLineLength
          nextLineLength = 0
          lineBytes += next
          result = lineBytes.toArray
          lineBytes.clear 
          ready = true
        } else if (lastChar == '\r') {
          val ret = offset
          offset += nextLineLength
          nextLineLength = 1
          lastChar = nextChar
          lineBytes += next
          result = lineBytes.toArray
          lineBytes.clear 
          ready = true
        } else {
          nextLineLength += 1
          lastChar = nextChar
          lineBytes += next
        }
      }
    }
    result
  }

  private def nextByte: Byte = {
    readFromFileToBufferIfNecessary
    if (byteBufferLength != -1) nextCharFromBuffer
    else -1
  }

  private def readFromFileToBufferIfNecessary =
    if (bufferOffset == byteBufferLength) {
      byteBufferLength = fis.read(byteBuffer)
      bufferOffset = 0
    }

  private def nextCharFromBuffer = { val c = byteBuffer(bufferOffset); bufferOffset += 1; c }
}