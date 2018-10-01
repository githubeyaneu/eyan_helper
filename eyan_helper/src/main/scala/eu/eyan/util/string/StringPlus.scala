package eu.eyan.util.string

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.OutputStreamWriter
import java.net.URLDecoder
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

import scala.io.Source

import eu.eyan.log.Log
import eu.eyan.util.http.HttpPlus
import eu.eyan.util.io.FilePlus
import eu.eyan.util.io.FilePlus.FilePlusImplicit
import eu.eyan.util.scala.TryCatchFinally
import eu.eyan.util.java.lang.RuntimePlus
import eu.eyan.util.scala.collection.StreamPlus.StreamImplicit
import java.awt.Desktop
import java.net.URL
import java.net.URI
import scala.util.matching.Regex
import scala.io.Codec
import eu.eyan.util.java.lang.RuntimePlus.ProcessResult
import java.io.InputStream
import eu.eyan.util.java.lang.ThreadPlus
import eu.eyan.util.scala.Try
import javax.swing.ImageIcon

object StringPlus {
  lazy val reg = "[\\p{InCombiningDiacriticalMarks}]".r

  def withoutAccents(s: String) = reg.replaceAllIn(Normalizer.normalize(s, Normalizer.Form.NFD), "").replaceAll("ß", "ss")

  def s1_startsWithSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.startsWith(search) && !s2.startsWith(search)
  def s2_startsWithSearch_s1_doesNot(s1: String, s2: String, search: String) = !s1.startsWith(search) && s2.startsWith(search)
  def s1_containsSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.contains(search) && !s2.contains(search)

  implicit class StringPlusImplicit(val s: String) {
    def print = { System.out.print(s); s }
    def printErr = { System.err.print(s); s }
    def println = { System.out.println(s); s }
    def printlnErr = { System.err.println(s); s }

    def writeToFile(filename: String): String = { writeToFile(filename.asFile); filename }

    def writeToFile(file: File): File = {
      val bw = new BufferedWriter(new FileWriter(file))
      TryCatchFinally(
        bw.write(s),
        e => Log.error(e),
        bw.close)
      file
    }

    def writeToFileAnsi(filename: String): String = {
      val writer = new OutputStreamWriter(new FileOutputStream(filename, true), "windows-1252")
      TryCatchFinally(
        writer.append(s),
        e => Log.error(e),
        writer.close)
      filename
    }

    def appendToFile(file: File): Unit = {
      val bw = new BufferedWriter(new FileWriter(file, true))
      bw.write(s)
      bw.close
    }

    def deleteAsFile = asFile.delete
    def deleteAsDir = asFile.deleteRecursively

    def extendFileNameWith(plus: String) = {
      val dotIndex = s.lastIndexOf(".")
      if (dotIndex > -1) s.substring(0, dotIndex) + plus + s.substring(dotIndex)
      else s + plus
    }

    def asFile = new File(s)
    def asDir = asFile
    def file = asFile
    def dir = asFile
    def asFileOrResource = asFile.orElseResource

    def asUrl = new URL(s)

    def execInDir(
      commandAndArgs:  Array[String],
      codec:           Codec,
      outputProcessor: Stream[String] => Unit = s => {},
      errorProcessor:  Stream[String] => Unit = s => {}) = {

      Log.info(s"Executing process in workDir $s cmd:${commandAndArgs.mkString(" ")}")

      //TODO refactor move to ProcessPlus etc... together with RuntimePlus
      val process = new ProcessBuilder(commandAndArgs: _*).directory(s.asFile).start
      def readStreamInThread(stream: InputStream, processor: Stream[String] => Unit) = ThreadPlus.run(processor(Source.fromInputStream(stream)(codec).getLines.toStream))
      val outRunner = readStreamInThread(process.getInputStream, outputProcessor)
      val errRunner = readStreamInThread(process.getErrorStream, errorProcessor)
      process.waitFor
      process.exitValue
    }

    //    import scala.sys.process.stringToProcess
    def executeAsProcess = {
      ("Executing a process:\r\n  " + s).println
      scala.sys.process.stringToProcess(s).!!
    }

    def executeAsBatchFile(batName: String = "temp_bat_can_deleted.bat", deleteBatAfterwards: Boolean = true) = {
      // TODO use try
      s.writeToFile(batName)
      Log.info("Executing a batch file: " + batName)
      val result = batName.executeAsProcessWithResultAndOutputLineCallback(s => {})
      Log.info("Result: " + result)
      //      val res = bat.executeAsProcess.println
      if (deleteBatAfterwards) batName.deleteAsFile
    }

    def executeAsProcessWithResult: ProcessResult = executeAsProcessWithResult(Codec.UTF8)

    def executeAsProcessWithResult(codec: Codec) = RuntimePlus.exec(s, codec)

    def executeAsProcessWithStreamProcessors(
      codec:           Codec,
      outputProcessor: Stream[String] => Unit = s => {},
      errorProcessor:  Stream[String] => Unit = s => {}) =
      RuntimePlus.execWithStreamProcessors(s, codec, outputProcessor, errorProcessor)

    def executeAsProcessWithResultAndOutputLineCallback(callback: String => Unit) = RuntimePlus.execAndProcessOutputs(s, callback, callback)

    def asUrlPost(postParams: String = "") = HttpPlus.sendPost(s, postParams)

    def asUrlGet_responseAsStream() = HttpPlus.sendGet_responseAsStream(s)

    def asUrlGet = Source.fromInputStream(asUrlGet_responseAsStream()).mkString

    def linesFromFile = Source.fromFile(s).getLines

    def toSafeFileName = s.replaceAll(":", "").replaceAll("\\\\", "_")

    def asDateTime(format: DateTimeFormatter, zoneId: ZoneId = ZoneOffset.UTC) = LocalDateTime.parse(s, format).atZone(zoneId).toInstant

    def toIntOr(orElse: Int) = try { s.toInt } catch { case nfe: NumberFormatException => orElse }

    def withoutAccents = StringPlus.withoutAccents(s)

    // with \ for regex
    val SLASH_R = """\/"""
    val BACKSLASH_R = """\\"""
    val COLON_R = """\:"""
    val ASTERISK_R = """\*"""
    val QUESTION_MARK_R = """\?"""
    val QUOTATION_MARK_R = """\""""
    val VERTICAL_BAR_R = """\|"""
    val FULL_STOP_R = """\."""
    val SEMICOLON_R = """\;"""
    val EQUALS_R = """\="""
    val COMMA_R = """\,"""
    val GRAVE_ACCENT_R = """\´"""
    val APOSTROPHE_R = """\'"""

    // without \
    val LESS_THAN = """<"""
    val GREATER_THAN = """>"""

    private val CHARS_TO_REPLACE = SLASH_R + BACKSLASH_R + COLON_R + ASTERISK_R + QUESTION_MARK_R + QUOTATION_MARK_R + VERTICAL_BAR_R + FULL_STOP_R + SEMICOLON_R + EQUALS_R + COMMA_R + GRAVE_ACCENT_R + APOSTROPHE_R + LESS_THAN + GREATER_THAN

    def toSafeFilename = s.replaceAll(s"[$CHARS_TO_REPLACE]", "_").withoutAccents

    def toUrlDecoded = URLDecoder.decode(s, "utf-8")

    //    def containsAny(strings: String*) = strings.exists(s.contains(_)) //TODO
    def containsAny(strings: Seq[String]) = strings.exists(s.contains(_))
    def matchesAny(strings: String*) = strings.exists(s.matches(_))

    def containsAnyIgnoreCase(strings: Seq[String]) = s.toLowerCase.containsAny(strings.map(_.toLowerCase))

    def toHexEncode = s.getBytes.map(_.toHexString).mkString(",")
    def toHexDecode = new String(s.split(",").map(Integer.parseUnsignedInt(_, 16).toByte).toArray)

    def openAsFile = if (s.asFile.exists()) Desktop.getDesktop.open(asFile)
    def openAsURL = Desktop.getDesktop.browse(new URI(s))

    //    def findGroup(regex: String, group: Int = 1):Option[String] = findGroup(regex.r, group)
    def findGroup(regex: Regex, group: Int = 1) = regex.findAllIn(s).matchData.toList.lift(0).map(_.group(group))

    def toIntOrElse(default: Int) = try s.toInt catch { case _: Throwable => default }

    def splitLinesFromFile(splitCondition: String => Boolean): Stream[String] = s.linesFromFile.toStream.splitToStreams(splitCondition).map(_.mkString("\r\n"))

    def toResourceFile = Try {
      val url = this.getClass.getClassLoader().getResource(s)
      if (url == null) throw new IllegalArgumentException(s"resource $s not found.")
      url.getFile.asFile
    }
    
    def isNull = s == null
    
    def isNullOrEmpty = isNull || s.isEmpty  
    
    def toIconAsResource = {
      //TODO refactor...
      val is = ClassLoader.getSystemResourceAsStream(s)
      val bytes = Stream.continually(is.read).takeWhile(_ != -1).map(_.toByte).toArray
      new ImageIcon(bytes)
    }
  }
}