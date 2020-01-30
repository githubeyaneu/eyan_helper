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
import eu.eyan.util.awt.clipboard.ClipboardPlus
import scala.annotation.tailrec
import eu.eyan.util.awt.ImagePlus
import java.awt.Color
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.util.Base64
import rx.lang.scala.Observable

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
    def mkDirs = { asDir.mkdirs; s }

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
      Log.debug("Executing a process:\r\n  " + s)
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

    def linesFromFile = Source.fromFile(s).getLines // FIXME

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
    def toHexDecode = new String(s.split(",").map(Integer.parseUnsignedInt(_, 16).toByte))

    private def encryptDecryptIvParameterSpec = new IvParameterSpec(new Array[Byte](16))
    private def encryptDecryptKey(salt:String) = new SecretKeySpec(Base64.getDecoder.decode(salt), "AES")
    private def encryptDecryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    
    def encrypt(salt: String): String = {
      val cipher = encryptDecryptCipher
      cipher.init(Cipher.ENCRYPT_MODE, encryptDecryptKey(salt), encryptDecryptIvParameterSpec)
      new String(Base64.getEncoder.encode(cipher.doFinal(s.getBytes("utf-8"))), "utf-8")
    }

    def decrypt(salt: String): String = {
  		val cipher = encryptDecryptCipher
      cipher.init(Cipher.DECRYPT_MODE, encryptDecryptKey(salt), encryptDecryptIvParameterSpec)
      new String(cipher.doFinal(Base64.getDecoder.decode(s.getBytes("utf-8"))), "utf-8")
    }

    def openAsFile = if (s.asFile.exists()) Desktop.getDesktop.open(asFile)
    def openAsURL = Desktop.getDesktop.browse(new URI(s))

    //    def findGroup(regex: String, group: Int = 1):Option[String] = findGroup(regex.r, group)
    def findGroup(regex: Regex, group: Int = 1) = {
      Log.debug("string: "+s)
      Log.debug("regex: "+regex)
      Log.debug("group: "+group)
      
      val findAll = regex.findAllIn(s)
      Log.debug("findAll: "+findAll)
      val matchData = findAll.matchData
      Log.debug("matchData: "+matchData)
      val matchDataList = matchData.toList
      Log.debug("matchDataList: "+matchDataList)
      val lift = matchDataList.headOption
      Log.debug("lift: "+lift)
      val groupFilter = lift.filter(mach => mach.groupCount >0)
      Log.debug("groupFilter: "+groupFilter)
      val res = groupFilter.map(mach => mach.group(group))
      Log.debug("Res: "+res)
      res
    }

    def toIntOrElse(default: Int) = try s.toInt catch { case _: Throwable => default }

    def splitLinesFromFile(splitCondition: String => Boolean): Stream[String] = s.linesFromFile.toStream.splitToStreams(splitCondition).map(_.mkString("\r\n"))

    def toResourceFile = Try {
      val url = this.getClass.getClassLoader.getResource(s)
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

    def toCamelCase = {
      @tailrec def underScoreToCamel(s: String, output: String, first: Boolean, lastUnderScore: Boolean): String =
        if (s.isEmpty) output
        else {
          val underScore = s.head == '_'
          val plus = if (underScore) "" else if (lastUnderScore) s.head.toUpper else if (first) s.head else s.head.toLower
          underScoreToCamel(s.tail, output + plus, first = false, lastUnderScore = underScore)
        }

      underScoreToCamel(s, "", first = true, lastUnderScore = false)
    }

    def toUnderScoreCase = {
      @tailrec def camel2Underscore(s: String, output: String, first: Boolean): String =
        if (s.isEmpty) output
        else {
          val plus = if (s.head.isUpper && !first) "_" else ""
          camel2Underscore(s.tail, output + plus + s.head, first = false)
        }

      camel2Underscore(s, "", first = true)
    }

    def copyToClipboard = ClipboardPlus.copyToClipboard(s)

    def countOccurrences(tgt: String): Int = if (s != null) s.sliding(tgt.length).count(window => window == tgt) else 0

    def toSmallIcon(color: Color = Color.gray, width: Int = s.length * 16, height: Int = 16) = new ImageIcon(ImagePlus.imageFromString(s, color, width, height))
    
    def asSingle = Observable[String](emitter => {emitter.onNext(s);emitter.onCompleted})

    def findAll(regex:String) = {
      val re = regex.r
      val matches = for(m <- re.findAllIn(s)) yield(m)
      matches.toList
    }
  }
}