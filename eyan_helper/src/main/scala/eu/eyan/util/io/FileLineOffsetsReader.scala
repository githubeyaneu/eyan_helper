package eu.eyan.util.io

import java.io.File
import java.io.Closeable
import java.io.FileReader
import java.io.FileInputStream

object FileLineStartOffsetReader {
  def apply(file: File) = new FileLineStartOffsetReader(file.getAbsolutePath, 8192)
  def apply(file: String, bufferSize: Int) = new FileLineStartOffsetReader(file, bufferSize)
}

class FileLineStartOffsetReader private (file: String, bufferSize: Int) extends  Closeable {
	val EGAL = 'x'
	
	private var finished = false
	private var offset = 0L
	
	private val buffer = new Array[Char](bufferSize)
	private val byteBuffer = new Array[Byte](bufferSize)
	private val reader = new FileReader(file)
	private val fis = new FileInputStream(file)
	
	private var lastChar = EGAL
	private var bufferOffset = 0
	private var bufferLength = 0
	private var byteBufferLength = 0
	private var nextLineLength = 0L
	
	def getOffset = offset

  override def close() = { reader.close(); fis.close() }

  /**
   * @return start offset from the line. After calling this method the end
   *         offset of the line can be read out with the getOffset():long
   * @throws IOException
   */
  def readLine = {
    var ready = false
    var result = -11L
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
    if (finished)
      -1
    else if (bufferOffset == byteBufferLength) {
      bufferLength = reader.read(buffer)
      byteBufferLength = fis.read(byteBuffer)
      bufferOffset = 0
      if (byteBufferLength == -1) { finished = true; -1 }
      else byteBuffer(getAndIncBufferOffset)
    }
    else byteBuffer(getAndIncBufferOffset)
  }

  private def getAndIncBufferOffset = { val ret = bufferOffset; bufferOffset += 1; ret }
}