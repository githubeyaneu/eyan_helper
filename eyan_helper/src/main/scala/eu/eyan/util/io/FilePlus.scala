package eu.eyan.util.io

import java.io.File
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object FilePlus {

  implicit class FilePlusImplicit(val file: File) {
    def deleteRecursively: Unit = {
      if (file.isDirectory) file.listFiles.foreach(_.deleteRecursively)
      if (file.exists && !file.delete) throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }

    def extension = if (file.getName.lastIndexOf(".") > 0) file.getName.substring(file.getName.lastIndexOf(".")) else ""
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

  def fileTrees(paths: String*): Stream[File] = (paths map { _.asFile } map getFileTree ).toStream.flatten
}