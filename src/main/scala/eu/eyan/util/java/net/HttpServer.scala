package eu.eyan.util.java.net

import java.io.Closeable
import java.net.ServerSocket
import java.nio.file.Files
import java.nio.file.Paths

import eu.eyan.log.Log
import eu.eyan.util.io.CloseablePlus
import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit
import eu.eyan.util.java.lang.ThreadPlus
import eu.eyan.util.string.StringPlus.StringPlusImplicit

case class Get(url: String, param: String, value: String)

object HttpServer {
  private def noGetHandler(get: Get): String = {
    Log.debug(get)

    defaultGetResponse(get, "No get handler found for this GET")
  }

  private def noWebsocketHandler(websocket: WebSocket): Unit = {
    websocket.sendMessageObserver.onNext(defaultGetResponse(websocket.get, "No websocket handler found."))
    websocket.close
  }

  private def defaultGetResponse(get: Get, message: String) = {
    val defaultResponses = Map(
      "url" -> get.url,
      "param" -> get.param,
      "value" -> get.value,
      "result" -> message)

    val responses = defaultResponses
    val responsesJson = responses map { case (key, value) => s""" "$key" : "$value" """ }
    val response = s"{\r\n${responsesJson.mkString(", ")}\r\n}"
    response
  }

}

//TODO: make it possible to give more handlers.
class HttpServer(
  port: Int, var getHandler: Get => String = HttpServer.noGetHandler, var websocketHandler: WebSocket => Unit = HttpServer.noWebsocketHandler) extends Closeable {

  def start = { if (!active) { active = true; ThreadPlus run listenTheSocket }; this }
  def close = { active = false; serverSocket.close }
  def stop = { close; this }

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

        if (requestMap.contains("Sec-WebSocket-Key") && WebSocket.handshake(socketOut, requestMap("Sec-WebSocket-Key"))) {
          // WEBSOCKET
          websocketHandler(new WebSocket(get.getOrElse(Get("", "", "")), requestMap, socket)) //TODO: does it have always a get param?
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
            socketOut.write("Content-Type: text/javascript\r\n".getBytes("ASCII"))
            socketOut.write("\r\n".getBytes("ASCII"))
            socketOut.write(bytes)
          } else {
            // GET
            val response = get.map(getHandler).getOrElse("Not get found")
            socketOut.write("Content-Type: application/json\r\n".getBytes("ASCII"))
            socketOut.write(("Content-Length: " + response.length + "\r\n").getBytes("ASCII"))
            socketOut.write("\r\n".getBytes("ASCII"))
            socketOut.write(response.getBytes("ASCII"))
          }

          socketOut.flush
          CloseablePlus.closeQuietly(socketOut, socketIn, socket)
        }
      } catch {
        case t: Throwable => Log.error("listenTheSocket", t)
      }
    }
  }

  def findGet(lines: List[String]) = {
    val getLine = lines.find(_.startsWith("GET "))

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