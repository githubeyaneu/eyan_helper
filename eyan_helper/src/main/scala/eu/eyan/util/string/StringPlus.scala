package eu.eyan.util.string

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.Normalizer
import scala.sys.process.stringToProcess

import eu.eyan.util.io.FilePlus.FilePlusImplicit
import eu.eyan.util.http.HttpPlus
import eu.eyan.util.io.FilePlus
import scala.io.Source
import eu.eyan.util.io.FilePlus.FilePlusImplicit
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object StringPlus {
  lazy val reg = "[\\p{InCombiningDiacriticalMarks}]".r

  def withoutAccents(s: String) = reg.replaceAllIn(Normalizer.normalize(s, Normalizer.Form.NFD), "").replaceAll("ß", "ss")

  def s1_startsWithSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.startsWith(search) && !s2.startsWith(search)
  def s2_startsWithSearch_s1_doesNot(s1: String, s2: String, search: String) = !s1.startsWith(search) && s2.startsWith(search)
  def s1_containsSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.contains(search) && !s2.contains(search)

  implicit class StringPlusImplicit(val s: String) {
    def println = { System.out.println(s); s }
    def printlnErr = { System.err.println(s); s }

    def writeToFile(filename: String): String = { writeToFile(new File(filename)); filename }

    def writeToFile(file: File): File = {
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(s)
      bw.close
      file
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

    def executeAsProcess = s.!!
    def executeAsBatchFile(batName: String = "temp_bat_can_deleted.bat", deleteBatAfterwards: Boolean = true) = {
      s.writeToFile(batName).executeAsProcess.println
      if (deleteBatAfterwards) batName.deleteAsFile
    }

    def asUrlPost(postParams: String = "") = HttpPlus.sendPost(s, postParams)
    def asUrlGet_responseAsStream() = HttpPlus.sendGet_responseAsStream(s)

    def linesFromFile = Source.fromFile(s).getLines
    
    def toSafeFileName = s.replaceAll(":", "").replaceAll("\\\\", "_")

    def asDateTime(format: DateTimeFormatter, zoneId: ZoneId = ZoneOffset.UTC) = LocalDateTime.parse(s, format).atZone(zoneId).toInstant
  }
}