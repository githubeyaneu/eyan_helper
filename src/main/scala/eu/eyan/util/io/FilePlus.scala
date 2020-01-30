package eu.eyan.util.io

import java.io.{File, FileInputStream, FileOutputStream}
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.security.MessageDigest

import eu.eyan.log.Log
import eu.eyan.util.scala.{TryCatchFinally, TryCatchFinallyClose}
import eu.eyan.util.string.StringPlus.StringPlusImplicit

import scala.io.{BufferedSource, Codec, Source}

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
      if (file.exists && file.isDirectory) Stream(file) ++ file.dirs.flatMap(_.dirsRecursively)
      else Stream()

    def filesWithExtension(extension: String) = file.listFiles.filter(_.getName.endsWith("." + extension))

    def fileTreeWithItself = FilePlus.treeWithItself(file)

    def subTree = fileTreeWithItself.tail

    def treeSub = subTree

    def subFiles = subTree.filter(_.isFile)

    def subDirs = subTree.filter(_.isDirectory)

    def getFile(subFilename: String): File = new File(file.getAbsolutePath + File.separator + subFilename)

    def linesList = TryCatchFinallyClose(Source.fromFile(file)(Codec.ISO8859), (bs: BufferedSource) => bs.getLines.toList, e => {
      Log.error(s"cannot read file", e)
      List()
    })

    //    def lines = Source.fromFile(file).getLines
    //TODO test it
    def lines(codec: Codec = Codec.UTF8) = new Iterator[String] {
      val bs = Source.fromFile(file)
      val lines = bs.getLines

      def hasNext: Boolean = {
        val hn = lines.hasNext
        if (!hn) CloseablePlus.closeQuietly(bs)
        hn
      }

      def next(): String = lines.next
    }

    def files = file.listFiles.toList

    def endsWith(extensions: String*) = extensions.exists(ext => file.getName.toLowerCase.endsWith(ext.toLowerCase))

    def notEndsWith(extensions: String*) = !extensions.exists(ext => file.getName.toLowerCase.endsWith(ext.toLowerCase))

    def contains(fileToContain: String) = file.exists && file.isDirectory && file.list.contains(fileToContain)

    def containsFileWithExtension(extension: String) = file.exists && file.isDirectory && file.listFiles.exists(_.endsWith(extension))

    def mkDirs = {
      if (file.notExists) file.mkdirs
      file
    }

    def hashSimple: String = {
      if (file.isFile) {
        if (file.length > 50 * 1000 * 1000) {
          TryCatchFinallyClose(
            new FileInputStream(file),
            (is: FileInputStream) => {
              val messageDigest = MessageDigest.getInstance("SHA")
              val buffer = Array.ofDim[Byte](8192)
              is.skip(file.length / 2)
              val bytesRead = is.read(buffer)
              if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead)
              val sha = messageDigest.digest

              sha.map("%02x".format(_)).mkString
            },
            t => {
              Log.error(t)
              throw t
            })
        } else "small"
      } else "d" //throw new IllegalArgumentException("Create hash not possible to directory! "+file.getAbsolutePath)
    }

    def hashFull(bytesReadCallback: Long => Unit = _ => {}): String = {
      if (!file.isFile) throw new IllegalArgumentException("Create hash not possible to directory! " + file.getAbsolutePath)

      TryCatchFinallyClose(
        new FileInputStream(file),
        (is: FileInputStream) => {
          val messageDigest = MessageDigest.getInstance("SHA")
          var bytesRead = 0
          val buffer = Array.ofDim[Byte](8192)

          while (bytesRead != -1) {
            bytesRead = is.read(buffer)
            if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead)
            bytesReadCallback(bytesRead)
          }

          messageDigest.digest.map("%02x".format(_)).mkString
        },
        t => {
          Log.error(t)
          throw t
        })
    }

    def hashFast(bytesReadCallback: Long => Unit = _ => {}, bufferSize: Long = 4096, sampleNumber: Long= 16): String = {
      if (!file.isFile) throw new IllegalArgumentException("Create hash not possible to directory! " + file.getAbsolutePath)

      val minLength = (2 * sampleNumber - 1) * bufferSize
      val fileLength = file.length

      if (fileLength <= minLength) hashFull(bytesReadCallback)
      else TryCatchFinallyClose(
        new FileInputStream(file),
        (is: FileInputStream) => {

          val samplesSize = sampleNumber * bufferSize
          val samplesNrWithoutLast = sampleNumber - 1
          val skipSize = ((fileLength - samplesSize) / samplesSize) * bufferSize

          val messageDigest = MessageDigest.getInstance("SHA")
          val buffer = Array.ofDim[Byte](bufferSize.toInt)

          var bytesRead = 0L
          var step = 0
          var actualFilePosition = 0L
          while (bytesRead != -1 && step < samplesNrWithoutLast) {
            bytesRead = is.read(buffer)
            if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead.toInt)
            val bytesSkipped = is.skip(skipSize)
            bytesReadCallback(bytesRead + bytesSkipped)
            actualFilePosition += bytesRead + bytesSkipped
            step += 1
          }

          val remainingCalculated = fileLength - samplesSize - samplesNrWithoutLast * skipSize
          val remainingCounted = fileLength - actualFilePosition - bufferSize
          assert(remainingCalculated == remainingCounted)

          val bytesSkipped = is.skip(remainingCalculated)
          bytesRead = is.read(buffer)
          if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead.toInt)
          bytesReadCallback(bytesRead + bytesSkipped)

          messageDigest.digest.map("%02x".format(_)).mkString
        },
        t => {
          Log.error(t)
          throw t
        })
    }

    def listFilesIfExists = if (file.isDirectory) file.listFiles else Array[File]()

    def empty = !file.isDirectory || file.list.isEmpty

    def notEmpty = !empty

    def attr = Files.readAttributes[BasicFileAttributes](file.toPath, classOf[BasicFileAttributes])

    def creationTime = attr.creationTime.toInstant

    def lastAccessTime = attr.lastAccessTime.toInstant

    def lastModifiedTime = attr.lastModifiedTime.toInstant

    def copyToDir(destination: File, progressCallback: Int => Unit = _ => {}) = {
      val destFile = (destination.getAbsolutePath + "\\" + file.getName).asFile
      val result = copyTo(destFile, progressCallback)
      if (result) Option(destFile) else None
    }

    def copyTo(destination: File, progressCallback: Int => Unit = _ => {}) = {
      val dest = new FileOutputStream(destination)
      val src = new FileInputStream(file)
      Log.debug(s"Copy $file to $destination")
      // TODO progress implementieren
      TryCatchFinally(
        {
          dest.getChannel.transferFrom(src.getChannel, 0, Long.MaxValue)
          true
        },
        { e => Log.error(e); false },
        {
          dest.close()
          src.close()
        })
    }

    def extendFileNameWith(plus: String):File = file.getAbsolutePath.extendFileNameWith(plus).asFile
		def prependFileNameWith(plus: String):File = {
      val dir = file.getParentFile
      if(dir==null) (plus+file.getAbsolutePath).asFile
      else (dir.getAbsolutePath+File.separator+plus+file.getName).asFile
    }
    def addSubDir(subdir: String):File = {
      val dir = file.getParentFile
      if(dir!=null) (dir.getAbsolutePath+File.separator+subdir).asFile.mkdirs
      prependFileNameWith(subdir+File.separator)
    }

    def generateNewNameIfExists(ct: Int = 0): File = {
      val plus = if (ct == 0) file else file.extendFileNameWith("_" + ct)
      if (plus.notExists) plus
      else generateNewNameIfExists(ct + 1)
    }

    def lift = if (file.exists) Some(file) else None

    def asResource = file.toString.toResourceFile.toOption

    def orElseResource = lift orElse asResource
  }

  private def treeWithItself(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) emptyArrayIfNull(f.listFiles).toStream.flatMap(treeWithItself)
      else Stream.empty)

  private def emptyArrayIfNull(list: Array[File]) = if (list == null) Array[File]() else list
}