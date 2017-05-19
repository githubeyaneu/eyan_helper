package eu.eyan.util.io

import java.io.InputStream
import eu.eyan.util.compress.ZipPlus
import eu.eyan.util.compress.SevenZipPlus
import eu.eyan.util.compress.CompressPlus

object InputStreamPlus {
  implicit class InputStreamPlusImplicit(inputStream: InputStream) {
    def asCompressedStream(extension: String): Option[CompressPlus] =
      if (extension == "zip")
        Some(new ZipPlus(inputStream))
      else if (extension == "7z")
        Some(new SevenZipPlus(inputStream))
      else None
  }
}