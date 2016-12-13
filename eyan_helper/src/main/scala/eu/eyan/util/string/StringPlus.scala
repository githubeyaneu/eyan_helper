package eu.eyan.util.string

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.Normalizer

import scala.sys.process.stringToProcess

import eu.eyan.util.io.FilePlus.FilePlus

object StringPlus {
  lazy val reg = "[\\p{InCombiningDiacriticalMarks}]".r

  def withoutAccents(s: String) = reg.replaceAllIn(Normalizer.normalize(s, Normalizer.Form.NFD), "").replaceAll("ß", "ss")

  implicit class StringPlus(val s: String) {
    def println = System.out.println(s)
    def printlnErr = System.err.println(s)

    def writeToFile(filename: String): Unit = writeToFile(new File(filename))

    def writeToFile(file: File): Unit = {
      val bw = new BufferedWriter(new FileWriter(file))
      bw.write(s)
      bw.close
    }

    def deleteAsFile = new File(s).delete
    def deleteAsDir = new File(s).deleteRecursively

    def executeAsProcess = s.!!
  }
}