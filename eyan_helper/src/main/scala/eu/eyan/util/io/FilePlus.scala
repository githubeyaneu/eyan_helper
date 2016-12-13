package eu.eyan.util.io

import java.io.File

object FilePlus {
  
  implicit class FilePlus(val file: File){
    def deleteRecursively:Unit= {
      if (file.isDirectory) file.listFiles.foreach(_.deleteRecursively)
      if (file.exists && !file.delete) throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }
  }
}