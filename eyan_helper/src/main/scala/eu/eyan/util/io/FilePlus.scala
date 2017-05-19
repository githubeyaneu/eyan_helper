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

    def dirs = if (file.exists) file.listFiles.filter(_.isDirectory).toStream else Stream()

    def dirsRecursively: Stream[File] =
      if (file.exists && file.isDirectory) Stream(file) ++ file.dirs.map(_.dirsRecursively).flatten
      else Stream()

    def filesWithExtension(extension: String) = file.listFiles.filter(_.getName.endsWith("." + extension))

    def getFileTree: Stream[File] = FilePlus.getFileTree(file)
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

  def fileTrees(paths: String*): Stream[File] = (paths map { _.asFile } map getFileTree ).toStream.flatten
}
