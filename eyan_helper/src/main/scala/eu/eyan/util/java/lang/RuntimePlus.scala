package eu.eyan.util.java.lang

import java.io.InputStream

import scala.io.Source

import eu.eyan.log.Log

object RuntimePlus {
  case class ProcessResult(exitValue: Int, output: String, errorOutput: String)

  def exec(cmd: String) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info("Executing process: " + cmd)
    val process = Runtime.getRuntime.exec(cmd)
    def readStreamInThread(stream: InputStream) = ThreadPlus.run(Source.fromInputStream(stream).mkString)
    val outRunner = readStreamInThread(process.getInputStream)
    val errRunner = readStreamInThread(process.getErrorStream)
    process.waitFor
    ProcessResult(process.exitValue, outRunner.result, errRunner.result)
  }

  //TODO: 
  def execAndProcessOutputs(cmd: String, callbackOut: String => Unit, callbackErr: String => Unit) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info("Executing process: " + cmd)
    val process = Runtime.getRuntime.exec(cmd)
    def readStreamInThread(stream: InputStream, callback: String => Unit) = ThreadPlus.run(Source.fromInputStream(stream).getLines.foreach(callback))
    val outRunner = readStreamInThread(process.getInputStream, callbackOut)
    val errRunner = readStreamInThread(process.getErrorStream, callbackErr)
    process.waitFor
    process.exitValue
  }
}