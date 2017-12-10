package eu.eyan.util.java.lang

import java.io.InputStream

import scala.io.Source

import eu.eyan.log.Log

object RuntimePlus {
  def exec(cmd: String) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info("Executing process: " + cmd)
    val process = Runtime.getRuntime.exec(cmd)
    def readStreamInThread(stream: InputStream) = ThreadPlus.run(Source.fromInputStream(stream).mkString)
    val outRunner = readStreamInThread(process.getInputStream)
    val errRunner = readStreamInThread(process.getErrorStream)
    process.waitFor
    case class ProcessResult(exitValue: Int, output: String, errorOutput: String)
    ProcessResult(process.exitValue, outRunner.result, errRunner.result)
  }
}