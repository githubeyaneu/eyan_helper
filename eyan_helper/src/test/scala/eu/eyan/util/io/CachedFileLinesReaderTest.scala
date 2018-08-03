package eu.eyan.util.io

import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.RandomAccessFile

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner

@RunWith(classOf[ScalaEclipseJunitRunner])
class CachedFileLinesReaderTest() {

  val _folder = new TemporaryFolder
  @Rule def folder = _folder

  private var file: File = null

  val cachedFileLineReader = CachedFileLineReader()

  @Before
  @throws(classOf[IOException])
  def setUp = {
  }

  @Test
  @throws(classOf[Exception])
  def test_load = {
    writeFileLines(100)
    var expectedPercent = 1
    cachedFileLineReader.load(file, percent => { assertThat(percent).isEqualTo(expectedPercent); expectedPercent += 1 })
  }

  @Test
  @throws(classOf[Exception])
  def test_load_NullPercentConsumer = {
    writeFileLines(100)
    cachedFileLineReader.load(file, null)
  }

  @Test
  @throws(classOf[Exception])
  def test_load_1Line = {
    writeFileLines(1000)
    var expectedPercent = 1
    cachedFileLineReader.load(file, percent => { assertThat(percent).isEqualTo(expectedPercent); expectedPercent += 1 })
  }

  @Test(expected = classOf[NullPointerException])
  @throws(classOf[Exception])
  def test_load_NullFile = {
    cachedFileLineReader.load(null, null)
  }

  @Test
  def test_load_IOException = {
    writeFileLines(10)
    val channel = new RandomAccessFile(file, "rw").getChannel()
    channel.lock()
    cachedFileLineReader.load(file, null)
    channel.close()
  }

  @Test
  def test_getLongestLine = {
    writeFileLines(11, false)
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.getLongestLine).isEqualTo("line10\r\n")
  }

  @Test
  def test_Size_empty = {
    writeFile("")
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.size).isEqualTo(0)
  }

  @Test
  def test_Size_1Line = {
    writeFile("1")
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.size).isEqualTo(1)
  }

  @Test
  def test_Size_1Line_load_twice = {
    writeFileLines(1, false)
    cachedFileLineReader.load(file, null)
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.size).isEqualTo(1)
    assertThat(cachedFileLineReader.get(0)).isEqualTo("line1\r\n")
  }

  @Test
  def test_Size_1M_Lines = {
    val SIZE = 10000000
    writeFileLines(SIZE, false)
    println("create complete")
    
    cachedFileLineReader.load(file, null)
    println("load complete")
    assertThat(cachedFileLineReader.size).isEqualTo(SIZE)
    
    for(i<-1 to SIZE) cachedFileLineReader.get(i)
  }

  @Test
  def test_Get_empty = {
    writeFile("")
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.get(0)).isNull
    assertThat(cachedFileLineReader.get(10)).isNull
  }

  @Test
  def test_Get = {
    writeFileLines(10000, false)
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.get(0)).isEqualTo("line1\r\n")
    assertThat(cachedFileLineReader.get(1)).isEqualTo("line2\r\n")
    assertThat(cachedFileLineReader.get(999)).isEqualTo("line1000\r\n")
    assertThat(cachedFileLineReader.get(1000000)).isNull
  }

  @Test
  def test_Get_reaIoException = {
    writeFileLines(10000, false)
    val channel = new RandomAccessFile(file, "rw").getChannel()
    cachedFileLineReader.load(file, null)
    assertThat(cachedFileLineReader.get(0)).isEqualTo("line1\r\n")

    channel.lock()
    assertThat(cachedFileLineReader.get(999)).isNull
    channel.close()
  }

  @Test
  def test_iterator_emptyFile = {
    writeFile("")
    cachedFileLineReader.load(file, null)
    val iterator = cachedFileLineReader.iterator
    assertThat(iterator.hasNext).isFalse
    assertThat(iterator.next).isNull
  }

  @Test
  def test_iterator = {
    writeFileLines(2, false)
    cachedFileLineReader.load(file, null)
    val iterator = cachedFileLineReader.iterator
    assertThat(iterator.hasNext).isTrue
    assertThat(iterator.next).isEqualTo("line1\r\n")
    assertThat(iterator.hasNext).isTrue
    assertThat(iterator.next).isEqualTo("line2\r\n")
    assertThat(iterator.hasNext).isFalse
    assertThat(iterator.next).isNull
  }

  @Test
  def test_findFirst = {
    writeFileLines(100, false)
    cachedFileLineReader.load(file, null)
    val firstMatcher = cachedFileLineReader.findFirst(".*(n.*\\d2)")
    assertThat(firstMatcher.group(1)).isEqualTo("ne12")

    val firstMatcher_NoMatch = cachedFileLineReader.findFirst("krixkrax")
    assertThat(firstMatcher_NoMatch).isNull
  }

  private def writeFileLines(numberOfLines: Int, sameLineLength: Boolean = true) = {
    file = folder.newFile
    val writer: PrintWriter = new PrintWriter(file)

    for {i <- 1 to numberOfLines} {
      val line = if (sameLineLength)
        "line" + "%09d".format(i) + "\r\n"
      else
        "line" + i + "\r\n"
      writer.write(line)
    }
    writer.flush
    writer.close
  }

  private def writeFile(content: String) = {
    file = folder.newFile
    val writer: PrintWriter = new PrintWriter(file)
    writer.write(content)
    writer.flush
    writer.close
  }
}