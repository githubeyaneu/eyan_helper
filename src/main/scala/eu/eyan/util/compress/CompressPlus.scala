package eu.eyan.util.compress

import java.io.InputStream
import java.io.File

trait CompressPlus {
  def extractOneFileTo(filePathToExtract: String, targetFile: File):File
}