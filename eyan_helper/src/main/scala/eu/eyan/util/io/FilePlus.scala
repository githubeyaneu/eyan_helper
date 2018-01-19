package eu.eyan.util.io

import java.io.File
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import java.security.MessageDigest
import java.io.FileInputStream
import scala.io.Source
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.Path
import eu.eyan.util.scala.TryCatchFinally
import eu.eyan.log.Log
import java.io.FileOutputStream
import eu.eyan.util.scala.TryCatchFinallyClose
import scala.io.BufferedSource

object FilePlus {

  implicit class FilePlusImplicit(val file: File) {
    def deleteRecursively: Unit = {
      if (file.isDirectory) file.listFiles.foreach(_.deleteRecursively)
      if (file.exists && !file.delete) throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
    }

    def notExists = !file.exists
    def existsAndFile = file != null && file.exists && file.isFile
    def existsAndDir = file != null && file.exists && file.isDirectory

    def extension = if (file.getName.lastIndexOf(".") > 0) file.getName.substring(file.getName.lastIndexOf(".")) else ""

    def withoutExtension = if (extension.isEmpty) file else new File(file.getPath.substring(0, file.getPath.lastIndexOf(".")))

    def dirs = if (file.exists) file.listFiles.filter(_.isDirectory).toStream else Stream()

    def dirsRecursively: Stream[File] =
      if (file.exists && file.isDirectory) Stream(file) ++ file.dirs.map(_.dirsRecursively).flatten
      else Stream()

    def filesWithExtension(extension: String) = file.listFiles.filter(_.getName.endsWith("." + extension))

    def fileTreeWithItself = FilePlus.treeWithItself(file)
    def subTree = fileTreeWithItself.tail
    def treeSub = subTree
    def subFiles = subTree.filter(_.isFile)
    def subDirs = subTree.filter(_.isDirectory)

    def getFile(subFilename: String): File = new File(file.getAbsolutePath + File.separator + subFilename)

    def linesList = TryCatchFinallyClose(Source.fromFile(file), (bs: BufferedSource) => bs.getLines.toList, e => { Log.error(s"cannot read file", e); List() })

    //    def lines = Source.fromFile(file).getLines
    //TODO test it
    def lines = new Iterator[String] {
      val bs = Source.fromFile(file)
      val lines = bs.getLines
      def hasNext: Boolean = { val hn = lines.hasNext; if (!hn) CloseablePlus.closeQuietly(bs); hn }
      def next(): String = lines.next
    }

    def files = file.listFiles.toList

    def endsWith(extensions: String*) = extensions.map(ext => file.getName.toLowerCase.endsWith(ext.toLowerCase)).contains(true)
    def notEndsWith(extensions: String*) = !(extensions.map(ext => file.getName.toLowerCase.endsWith(ext.toLowerCase)).contains(true))

    def contains(fileToContain: String) = file.exists && file.isDirectory && file.list.contains(fileToContain)

    def containsFileWithExtension(extension: String) = file.exists && file.isDirectory && file.listFiles.exists(_.endsWith(extension))

    def mkDirs = { file.mkdirs; file }

    def hash = {
      if (file.isFile()) {
        if (file.length > 50 * 1000 * 1000) {
          val messageDigest = MessageDigest.getInstance("SHA")
          TryCatchFinallyClose(
            new FileInputStream(file),
            (is: FileInputStream) => {
              val buffer = new Array[Byte](8192)
              is.skip(file.length / 2)
              val bytesRead = is.read(buffer)
              if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead)
              val sha = messageDigest.digest

              sha.map("%02x".format(_)).mkString
            },
            t => t.printStackTrace)
        } else "small"
      } else "d" //throw new IllegalArgumentException("Create hash not possible to directory! "+file.getAbsolutePath)
    }

    def listFilesIfExists = if (file.isDirectory) file.listFiles else Array[File]()

    def empty = !file.isDirectory || file.list.isEmpty
    def notEmpty = !empty

    def attr = Files.readAttributes[BasicFileAttributes](file.toPath, classOf[BasicFileAttributes])
    def creationTime = attr.creationTime.toInstant
    def lastAccessTime = attr.lastAccessTime.toInstant
    def lastModifiedTime = attr.lastModifiedTime.toInstant

    def copyToDir(destination: File, progressCallback: Int => Unit = dontcare => {}) = {
      val destFile = (destination.getAbsolutePath+"\\"+file.getName).asFile
      val result = copyTo(destFile, progressCallback)
      if (result) Option(destFile) else None
    }
    
    def copyTo(destination: File, progressCallback: Int => Unit = dontcare => {}) = {
      val dest = new FileOutputStream(destination)
      val src = new FileInputStream(file)
      Log.debug(s"Copy $file to $destination")
      // TODO progress implementieren
      TryCatchFinally(
        { dest.getChannel().transferFrom(src.getChannel, 0, Long.MaxValue); true },
        { e => Log.error(e); false },
        { dest.close; src.close })
    }

    def extendFileNameWith(plus: String) = file.getAbsolutePath.extendFileNameWith(plus).asFile

    def generateNewNameIfExists(ct: Int = 0): File = {
      val plus = if (ct == 0) file else file.extendFileNameWith("_" + ct)
      if (plus.notExists) plus
      else generateNewNameIfExists(ct + 1)
    }
  }

  private def treeWithItself(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) emptyArrayIfNull(f.listFiles).toStream.flatMap(treeWithItself)
      else Stream.empty)

  private def emptyArrayIfNull(list: Array[File]) = if (list == null) Array[File]() else list

  private def fileTreesWithItselfs(paths: String*): Stream[File] = (paths map { _.asFile } map treeWithItself).toStream.flatten
}