package eu.eyan.util.awt.clipboard

import java.awt.datatransfer.DataFlavor
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
//import org.apache.commons.io.FileUtils
import java.io.IOException
import java.nio.file.Files
import java.io.PrintWriter

object ClipboardPlus {
  def getTextFromClipboard(): String = {
    try {
      Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).asInstanceOf[String]
    } catch {
      case e: Exception => e.printStackTrace()
      ""
    }
  }

  def copyToClipboard(text: String): Unit = Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null)

  def toTempFile(): File = {
    try {
      val tempFile = File.createTempFile("clipboard", ".txt")
      println("Write clipboard contents to temp file: " + tempFile)
      new PrintWriter(tempFile) { write(getTextFromClipboard()); close }
      //      FileUtils.writeStringToFile(tempFile, getTextFromClipboard())
      tempFile
    } catch {
      case e: IOException => e.printStackTrace()
    }
    null
  }
}