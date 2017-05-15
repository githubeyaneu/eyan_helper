package eu.eyan.util.io

import java.io.File

object FilePlus {

  implicit class FilePlusImplicit(val file: File) {
    def deleteRecursively: Unit = {
      if (file.isDirectory) file.listFiles.foreach(_.deleteRecursively)
      if (file.exists && !file.delete) throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }

    def filesWithExtension(extension: String) = file.listFiles.filter(_.getName.endsWith("." + extension))
    
    def getFileTree: Stream[File] = FilePlus.getFileTree(file)
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

}