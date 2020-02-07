package eu.eyan.util.java.lang

import java.io.InputStream

import scala.io.Source

import eu.eyan.log.Log
import scala.io.Codec
import java.io.File

object RuntimePlus {
  case class ProcessResult(exitValue: Int, output: Option[String], errorOutput: Option[String])

  def exec(cmd: String, codec: Codec = Codec.UTF8) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info("Executing process: " + cmd)
    val process = startProcess(cmd)
    def readStreamInThread(stream: InputStream) = ThreadPlus.run(Source.fromInputStream(stream)(codec).mkString)
    val outRunner = readStreamInThread(process.getInputStream)
    val errRunner = readStreamInThread(process.getErrorStream)
    process.waitFor
    Log.info("Executing process finished: " + cmd)
    ProcessResult(process.exitValue, outRunner.result, errRunner.result)
  }

  //TODO:
  def execAndProcessOutputs(cmd: String, callbackOut: String => Unit, callbackErr: String => Unit) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info(s"Executing process: $cmd, File(.)=${new File(".").getAbsolutePath}")
    val process = startProcess(cmd)

    def readStreamInThread(stream: InputStream, callback: String => Unit) = ThreadPlus.run(Source.fromInputStream(stream).getLines.foreach(callback))
    val outRunner = readStreamInThread(process.getInputStream, callbackOut)
    val errRunner = readStreamInThread(process.getErrorStream, callbackErr)
    process.waitFor
    process.exitValue
  }

  def execWithStreamProcessors(cmd: String, codec: Codec = Codec.UTF8, outputProcessor: Stream[String] => Unit, errorProcessor: Stream[String] => Unit) = {
    //TODO params: copy out end arr to console and as a result...
    Log.info("Executing process: " + cmd)
    val process = startProcess(cmd)
    def readStreamInThread(stream: InputStream, processor: Stream[String] => Unit) = ThreadPlus.run(processor(Source.fromInputStream(stream)(codec).getLines.toStream))
    val outRunner = readStreamInThread(process.getInputStream, outputProcessor)
    val errRunner = readStreamInThread(process.getErrorStream, errorProcessor)
    process.waitFor
    process.exitValue
  }

  def availableProcessors = Runtime.getRuntime.availableProcessors

  private def startProcess(cmd: String) = {
    //val process = Runtime.getRuntime.exec(cmd)

<<<<<<< Upstream, based on branch 'JPanelBuilder' of https://github.com/githubeyaneu/eyan_helper.git
    val process = new ProcessBuilder("cmd", "/C start /B /LOW " + cmd).start();
    process
=======
    val pb = new ProcessBuilder("cmd", "/C start /B /LOW " + cmd);
    pb.start()
>>>>>>> 942e12c low prio execution jpanelbuilder obse+
  }
}