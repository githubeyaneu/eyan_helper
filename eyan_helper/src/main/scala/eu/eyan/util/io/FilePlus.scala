package eu.eyan.util.io

import java.io.File
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import java.security.MessageDigest
import java.io.FileInputStream
import scala.io.Source

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
    
    def getFile(subFilename: String): File = new File(file.getAbsolutePath + File.separator + subFilename)
    
    def lines = Source.fromFile(file).getLines 

    def hash = {
			if (file.isFile()) {
			  if(file.length > 50*1000*1000){
				  val messageDigest = MessageDigest.getInstance("SHA")
						  val is = new FileInputStream(file)
				  val buffer = new Array[Byte](8192)
				  is.skip(file.length/2)
				  val bytesRead = is.read(buffer)
				  if (bytesRead > 0) messageDigest.update(buffer, 0, bytesRead)
				  val sha = messageDigest.digest
				  
				  sha.map("%02x".format(_)).mkString
			  }
			  else "small"
			}
			else "d"//throw new IllegalArgumentException("Create hash not possible to directory! "+file.getAbsolutePath)
    }
  }

  def getFileTree(f: File): Stream[File] =
    f #:: (
      if (f.isDirectory) f.listFiles().toStream.flatMap(getFileTree)
      else Stream.empty)

  def fileTrees(paths: String*): Stream[File] = (paths map { _.asFile } map getFileTree).toStream.flatten
}
