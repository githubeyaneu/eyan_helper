package eu.eyan.util.java.io

import java.io.BufferedReader

import scala.annotation.tailrec

import eu.eyan.log.Log

object BufferedReaderPlus {
  implicit class BufferedReaderImplicit(bufferedReader: BufferedReader) {
    // @tailrec FIXME: this must be tailrec!!!!
    final def readLinesUntilEmtpy: List[String] = {
      val line = bufferedReader.readLine
      Log.trace(line)
      if (line == null || line == "") List()
      else line :: bufferedReader.readLinesUntilEmtpy
    }
  }
}