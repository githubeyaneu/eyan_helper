package eu.eyan.util.java.lang

import java.io.InputStream

import scala.io.Source

import eu.eyan.log.Log
import scala.io.Codec
import java.io.File

object RuntimePlus {
  case class ProcessResult(exitValue: Int, output: Option[String], errorOutput: Option[String])

  def exec(cmd: String, codec: Codec = Codec.ISO8859) = {
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

  def execWithStreamProcessors(cmd: String, codec: Codec = Codec.ISO8859, outputProcessor: Stream[String] => Unit, errorProcessor: Stream[String] => Unit) = {
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

  def exec(cmd: List[String], codec: Codec ):ProcessResult = {
    Log.info("Executing process: " + cmd)
    val process = startProcess(cmd)
    def readStreamInThread(stream: InputStream) = ThreadPlus.run(Source.fromInputStream(stream)(codec).mkString)
    val outRunner = readStreamInThread(process.getInputStream)
    val errRunner = readStreamInThread(process.getErrorStream)
    process.waitFor
    process.destroy()
    Log.info("Executing process finished: " + cmd)
    ProcessResult(process.exitValue, outRunner.result, errRunner.result)
  }

  def execAndProcessOutputs(cmd: List[String], callbackOut: String => Unit, callbackErr: String => Unit) = {
    //TODO params: copy out end arr to console and as a result...
    val process = startProcess(cmd)

    def readStreamInThread(stream: InputStream, callback: String => Unit) = ThreadPlus.run(Source.fromInputStream(stream).getLines.foreach(callback))
    val outRunner = readStreamInThread(process.getInputStream, callbackOut)
    val errRunner = readStreamInThread(process.getErrorStream, callbackErr)
    process.waitFor
    process.exitValue
  }

  private def startProcess(cmds: List[String]) = {
    val cmd = List("cmd", "/C") ++ cmds
    Log.info(cmd.mkString(" "))
    val processBuilder = new ProcessBuilder(cmd: _*)
    val process = processBuilder.start();

    process
  }

  private def startProcess(cmd: String) = {
    //val process = Runtime.getRuntime.exec(cmd)

    //FIXME the start /B has some side effects: it causes the stdin or out to be closed? has to be checked.
    val processBuilder = new ProcessBuilder("cmd", "/C start /B /LOW " + cmd)
    val process = processBuilder.start();

    process
  }
}