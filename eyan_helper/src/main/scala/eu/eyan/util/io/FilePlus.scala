package eu.eyan.util.io

import java.text.Normalizer
import java.io.FileWriter
import java.io.BufferedWriter
import java.io.File
import scala.sys.process._

object FilePlus {
  
  implicit class FilePlus(val file: File){
    def deleteRecursively:Unit= {
    	if (file.isDirectory) file.listFiles.foreach(_.deleteRecursively)
    	if (file.exists && !file.delete) throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }
}