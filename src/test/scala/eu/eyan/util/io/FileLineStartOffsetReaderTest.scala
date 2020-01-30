package eu.eyan.util.io

import java.io.File
import java.io.IOException
import java.io.PrintWriter

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner

@RunWith(classOf[ScalaEclipseJunitRunner])
class FileLineStartOffsetReaderTest() {

  val _folder = new TemporaryFolder
  @Rule def folder = _folder

  // @Rule var folder: TemporaryFolder = new TemporaryFolder

  private var filepath: String = _

  @Before
  @throws(classOf[IOException])
  def setUp = {
    val file: File = folder.newFile
    filepath = file.getAbsolutePath
    val writer: PrintWriter = new PrintWriter(new File(filepath))
    writer.write("line0 rn\r\n")
    writer.write("line1 r\r")
    writer.write("line2 n\n")
    writer.write("line34nr\n")
    writer.write("\r")
    writer.write("line56 rrn\r")
    writer.write("\r\n")
    writer.write("line7J")
    writer.flush
    writer.close
  }

  @Test
  @throws(classOf[Exception])
  def testStartOffsets = {
    var flr: FileLineStartOffsetReader = FileLineStartOffsetReader(filepath, 8192)
    assertThat(flr.readLine).isEqualTo(0)
    assertThat(flr.readLine).isEqualTo(10)
    assertThat(flr.readLine).isEqualTo(18)
    assertThat(flr.readLine).isEqualTo(26)
    assertThat(flr.readLine).isEqualTo(35)
    assertThat(flr.readLine).isEqualTo(36)
    assertThat(flr.readLine).isEqualTo(47)
    assertThat(flr.readLine).isEqualTo(49)
    assertThat(flr.readLine).isEqualTo(-1)
    assertThat(flr.readLine).isEqualTo(-1)
    flr.close
    flr = FileLineStartOffsetReader(filepath, 1)
    assertThat(flr.getOffset).isEqualTo(0)
    assertThat(flr.readLine).isEqualTo(0)
    assertThat(flr.readLine).isEqualTo(10)
    assertThat(flr.readLine).isEqualTo(18)
    assertThat(flr.readLine).isEqualTo(26)
    assertThat(flr.readLine).isEqualTo(35)
    assertThat(flr.readLine).isEqualTo(36)
    assertThat(flr.readLine).isEqualTo(47)
    assertThat(flr.readLine).isEqualTo(49)
    assertThat(flr.readLine).isEqualTo(-1)
    assertThat(flr.readLine).isEqualTo(-1)
    flr.close
  }

  //  @Test
  //  @throws(classOf[Exception])
  //  def testOffsets {
  //    var flr: FileLineOffsetsReader = new FileLineOffsetsReader(filepath, 8192)
  //    assertThat(flr.getOffset).isEqualTo(0)
  //    assertNextLineOffsets(flr, 0, 9)
  //    assertNextLineOffsets(flr, 10, 17)
  //    assertNextLineOffsets(flr, 18, 25)
  //    assertNextLineOffsets(flr, 26, 34)
  //    assertNextLineOffsets(flr, 35, 35)
  //    assertNextLineOffsets(flr, 36, 46)
  //    assertNextLineOffsets(flr, 47, 48)
  //    assertNextLineOffsets(flr, 49, 54)
  //    assertThat(flr.readLine).isNull
  //    assertThat(flr.readLine).isNull
  //    flr.close
  //    flr = new FileLineOffsetsReader(filepath, 1)
  //    assertThat(flr.getOffset).isEqualTo(0)
  //    assertNextLineOffsets(flr, 0, 9)
  //    assertNextLineOffsets(flr, 10, 17)
  //    assertNextLineOffsets(flr, 18, 25)
  //    assertNextLineOffsets(flr, 26, 34)
  //    assertNextLineOffsets(flr, 35, 35)
  //    assertNextLineOffsets(flr, 36, 46)
  //    assertNextLineOffsets(flr, 47, 48)
  //    assertNextLineOffsets(flr, 49, 54)
  //    assertThat(flr.readLine).isNull
  //    assertThat(flr.readLine).isNull
  //    flr.close
  //  }
  //
  //  @throws(classOf[IOException])
  //  private def assertNextLineOffsets(flr: FileLineOffsetsReader, startOffset: Int, endOffset: Int) {
  //    val offsets: Tuple2[java.lang.Long, java.lang.Long] = flr.readLine
  //    assertThat(offsets.getStart).isEqualTo(startOffset)
  //    assertThat(offsets.getEnd).isEqualTo(endOffset)
  //  }

  //  @Test
  //  @throws(classOf[Exception])
  //  def testGetLine {
  //    var flr: FileLinesReader = new FileLinesReader(filepath, 8192)
  //    assertThat(flr.getOffset).isEqualTo(0)
  //    assertNextLine(flr, "line0 rn\r\n", 10)
  //    assertNextLine(flr, "line1 r\r", 18)
  //    assertNextLine(flr, "line2 n\n", 26)
  //    assertNextLine(flr, "line34nr\n", 35)
  //    assertNextLine(flr, "\r", 36)
  //    assertNextLine(flr, "line56 rrn\r", 47)
  //    assertNextLine(flr, "\r\n", 49)
  //    assertNextLine(flr, "line7J", 55)
  //    assertNextLine(flr, null, 55)
  //    assertNextLine(flr, null, 55)
  //    flr.close
  //    flr = new FileLinesReader(filepath, 1)
  //    assertThat(flr.getOffset).isEqualTo(0)
  //    assertNextLine(flr, "line0 rn\r\n", 10)
  //    assertNextLine(flr, "line1 r\r", 18)
  //    assertNextLine(flr, "line2 n\n", 26)
  //    assertNextLine(flr, "line34nr\n", 35)
  //    assertNextLine(flr, "\r", 36)
  //    assertNextLine(flr, "line56 rrn\r", 47)
  //    assertNextLine(flr, "\r\n", 49)
  //    assertNextLine(flr, "line7J", 55)
  //    assertNextLine(flr, null, 55)
  //    assertNextLine(flr, null, 55)
  //    flr.close
  //  }
  //
  //  @throws(classOf[IOException])
  //  private def assertNextLine(flr: FileLinesReader, nextLine: String, expectedOffset: Int) {
  //    assertThat(flr.readLine).isEqualTo(nextLine)
  //    assertThat(flr.getOffset).isEqualTo(expectedOffset)
  //  }
}