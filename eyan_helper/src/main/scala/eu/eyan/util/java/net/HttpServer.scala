package eu.eyan.util.java.net

import java.io.Closeable
import eu.eyan.util.java.lang.ThreadPlus
import java.net.ServerSocket
import java.io.BufferedReader
import java.io.InputStreamReader
import eu.eyan.log.Log
import scala.collection.mutable.ListBuffer
import java.nio.file.Paths
import java.nio.file.Files
import java.io.InputStream
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit
import java.io.PrintWriter
import java.security.MessageDigest
import java.io.BufferedOutputStream
import java.io.OutputStream
import eu.eyan.util.scala.Try
import sun.misc.BASE64Encoder
import eu.eyan.util.io.CloseablePlus
import java.net.Socket

case class Get(url: String, param: String, value: String)

class HttpServer(
  port: Int, getHandler: Get => String, websocketHandler: WebSocket => Unit) extends Closeable {
  def start = if (!active) { active = true; ThreadPlus run listenTheSocket }
  def close = { active = false; serverSocket.close }
  def stop = close

  private var active = false
  private val serverSocket = new ServerSocket(port)

  private def listenTheSocket = {

    while (active) {

      try {
        val socket = serverSocket.accept // BLOCKS

        Log.debug("----------------------------------------------------")

        val socketIn = socket.getInputStream
        val socketOut = socket.getOutputStream

        val requestLines = socketIn.readLinesUntilEmtpyLine
        val requestLinesSplitted = requestLines.map(_.split(": ").lift).map(array => (array(0).getOrElse(""), array(1).getOrElse("")))
        val requestMap = requestLinesSplitted.toMap
        val get = findGet(requestLines)

        // WEBSOCKET TODO
        if (requestMap.contains("Sec-WebSocket-Key") && WebSocket.handshake(socketOut, requestMap("Sec-WebSocket-Key"))) {
          websocketHandler(new WebSocket(get.getOrElse(Get("","","")), requestMap, socket))//FIXME: does it have always a get param?
        } else {

          val selectedResource = get.map(_.url).getOrElse("").toResourceFile
          val resourceExists = selectedResource.map(_.exists).getOrElse(false)

          socketOut.write("HTTP/1.1 200 OK\r\n".getBytes("ASCII"))
          socketOut.write("Access-Control-Allow-Origin: *\r\n".getBytes("ASCII"))

          if (resourceExists) {
            // RESOURCE
            Log.debug("Resource " + selectedResource)
            val bytes = Files.readAllBytes(Paths.get(selectedResource.get.toURI))
            Log.debug(bytes.size)
            socketOut.write(("Content-Length: " + bytes.length + "\r\n").getBytes("ASCII"))
            socketOut.write(("Content-Type: text/javascript\r\n").getBytes("ASCII"))
            socketOut.write("\r\n".getBytes("ASCII"))
            socketOut.write(bytes)
          } else {
            // GET
            val response = get.map(getHandler).getOrElse("Not get found")
            socketOut.write(("Content-Type: application/json\r\n").getBytes("ASCII"))
            socketOut.write(("Content-Length: " + response.length + "\r\n").getBytes("ASCII"))
            socketOut.write("\r\n".getBytes("ASCII"))
            socketOut.write(response.getBytes("ASCII"))
          }

          socketOut.flush
          CloseablePlus.closeQuietly(socketOut, socketIn, socket)
        }
      } catch {
        case t: Throwable => Log.error(t)
      }
    }
  }

  def findGet(lines: List[String]) = {
    val getLine = lines.filter(_.startsWith("GET ")).lift(0)

    getLine.map(line => {
      Log.debug("Get line:" + line)
      val headerGet = line.substring(5, line.lastIndexOf("HTTP")).trim
      Log.debug("Get: " + headerGet)
      val urlParams = headerGet.split("\\?")
      val url = urlParams.lift(0).getOrElse("")
      Log.debug("url: " + url)
      val params = urlParams.lift(1).map(_.split("=")).getOrElse(Array())
      // FIXME: lets support more params separated with &
      Log.debug("params: " + params.mkString(","))
      val paramName = params.lift(0).getOrElse("")
      val paramValue = params.lift(1).getOrElse("")
      Get(url, paramName, paramValue)
    })
  }
}