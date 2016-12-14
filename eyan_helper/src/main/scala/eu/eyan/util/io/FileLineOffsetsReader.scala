package eu.eyan.util.io

import java.io.File
import java.io.Closeable
import java.io.FileReader
import java.io.FileInputStream

object FileLineStartOffsetReader {
  def apply(file: File) = new FileLineStartOffsetReader(file.getAbsolutePath, 64 * 1024)
  def apply(file: String, bufferSize: Int) = new FileLineStartOffsetReader(file, bufferSize)
}

class FileLineStartOffsetReader private (file: String, bufferSize: Int) extends Closeable {
  val EGAL = 'x'
  private var offset = 0L

  private val byteBuffer = new Array[Byte](bufferSize)
  private val fis = new FileInputStream(file)

  private var lastChar = EGAL
  private var bufferOffset = 0
  private var bufferLength = 0
  private var byteBufferLength = 0
  private var nextLineLength = 0L

  def getOffset = offset

  override def close() = { fis.close() }

  /**
   * @return start offset from the line. After calling this method the end
   *         offset of the line can be read out with the getOffset():long
   * @throws IOException
   */
  def readLine = {
    var ready = false
    val INVALID_RESULT = -11L
    var result = INVALID_RESULT
    while (result < -1L) {
      val nextCharInt = nextChar
      if (nextCharInt == -1)
        if (nextLineLength == 0) result = -1
        else {
          val ret = offset
          offset += nextLineLength
          nextLineLength = 0
          result = ret
        }
      else {
        val nextChar = nextCharInt.toChar
        if (nextChar == '\n') {
          nextLineLength += 1
          lastChar = EGAL
          val ret = offset
          offset += nextLineLength
          nextLineLength = 0
          result = ret
        }
        else if (lastChar == '\r') {
          val ret = offset
          offset += nextLineLength
          nextLineLength = 1
          lastChar = nextChar
          result = ret
        }
        else {
          nextLineLength += 1
          lastChar = nextChar
        }
      }
    }
    result
  }

  private def nextChar: Int = {
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