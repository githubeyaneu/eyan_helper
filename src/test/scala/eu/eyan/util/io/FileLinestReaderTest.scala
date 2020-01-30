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
import java.util.Arrays

@RunWith(classOf[ScalaEclipseJunitRunner])
class FileLinestReaderTest() {

  val _folder = new TemporaryFolder
  @Rule def folder = _folder

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
    var flr: FileLinesReader = FileLinesReader(filepath, 8192)

    val strings = flr.readLines.toList
    println(">>>"+strings.mkString+"<<<")
    assertThat(strings).isEqualTo(
      List("line0 rn\r\n", 
          "line1 r\r", 
          "line2 n\n", 
          "line34nr\n", 
          "\r", 
          "line56 rrn\r", 
          "\r\n", 
          "line7J"))
    flr.close
    
    flr = FileLinesReader(filepath, 1)
    assertThat(flr.readLines.toList).isEqualTo(
      List("line0 rn\r\n", "line1 r\r", "line2 n\n", "line34nr\n", "\r", "line56 rrn\r", "\r\n", "line7J"))
    flr.close
  }
}