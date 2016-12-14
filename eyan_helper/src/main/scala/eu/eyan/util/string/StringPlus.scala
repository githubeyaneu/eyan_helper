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

  def s1_startsWithSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.startsWith(search) && !s2.startsWith(search)
  def s2_startsWithSearch_s1_doesNot(s1: String, s2: String, search: String) = !s1.startsWith(search) && s2.startsWith(search)
  def s1_containsSearch_s2_doesNot(s1: String, s2: String, search: String) = s1.contains(search) && !s2.contains(search)

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