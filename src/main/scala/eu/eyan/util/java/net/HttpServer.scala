package eu.eyan.util.java.net

import java.io.Closeable
import java.net.{BindException, ServerSocket, Socket, SocketException}
import java.nio.file.{Files, Paths}
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

import eu.eyan.log.Log
import eu.eyan.util.io.CloseablePlus
import eu.eyan.util.io.CloseablePlus.CloseablePlusImplicit
import eu.eyan.util.io.FilePlus.FilePlusImplicit
import eu.eyan.util.io.InputStreamPlus.InputStreamPlusImplicit
import eu.eyan.util.java.util.concurrent.NamedThreadFactory
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.time.Timer.timerStart
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.subjects.BehaviorSubject

import scala.language.postfixOps
import scala.util.{Failure, Success}

case class Get(url: String, param: String, value: String)

class HttpServer() extends Closeable {

  var getHandler: Get => Option[String] = (_: Get) => None

  def setGetHandler(getHandler: Get => Option[String]) = {
    this.getHandler = getHandler
    this
  }

  var webSocketHandler: WebSocket => Unit = ws => ws.close()

  def setWebSocketHandler(webSocketHandler: WebSocket => Unit) = {
    this.webSocketHandler = webSocketHandler
    this
  }

  private val port$$ = BehaviorSubject[Option[Integer]](None)
  private val serverStarted = new AtomicBoolean(false)
  private var serverSocket: ServerSocket = _

  private val executorPool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors(), new NamedThreadFactory(this.getClass.getName))
  port$$.subscribe(port => Log.info("HttpServer.port$$.onNext", port))

  def port$ = port$$.distinctUntilChanged.subscribeOn(IOScheduler())

  @throws(classOf[BindException])
  def start(portToOpen: Integer = null): HttpServer = {
    if (!serverStarted.get()) {
      val SOCKET_BACKLOG_SIZE = Runtime.getRuntime.availableProcessors()*2
      serverSocket = if (portToOpen != null) new ServerSocket(portToOpen, SOCKET_BACKLOG_SIZE) else new ServerSocket(0, SOCKET_BACKLOG_SIZE)
      val openedPort = serverSocket.getLocalPort
      port$$.onNext(Some(openedPort))
      new Thread(() => listenTheSocket(), this.getClass.getName + ":" + openedPort).start()
    } else {
      Log.warn("already serverStarted...")
    }
    this
  }

  def close(): Unit = {
    serverSocket.closeQuietly
    serverStarted.set(false)
    port$$.onNext(None)
  }

  private def listenTheSocket(): Unit = {
    serverStarted.set(true)
    while (serverStarted.get) {
      try {
        val socket = serverSocket.accept // BLOCKS
        executorPool.execute(() => {
          try {
            handleNewSocket(socket)
          } catch {
            case t:Throwable => Log.error(t.getMessage)
          }
        })
      } catch {
        case se: SocketException => Log.info("OK SocketException (?closed by client?) " + se.getMessage)
        case t: Throwable => Log.error("listenTheSocket", t)
      }
    }
  }

  private def handleNewSocket(socket: Socket) = {
    Log.info(Thread.currentThread().getName,"Start")
    val timer = timerStart()

    Log.debug("----------------------------------------------------")

    val socketIn = socket.getInputStream
    val socketOut = socket.getOutputStream

    val requestLines = socketIn.readLinesUntilEmtpyLine
    Log.debug("Lines processed", timer.timerElapsed)
    Log.debug("Request lines >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    Log.debug(requestLines.head)
    Log.debug(requestLines.tail)
    Log.debug("Request lines <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
    val requestLinesSplitted = requestLines.map(_.split(": ").lift).map(array => (array(0).getOrElse(""), array(1).getOrElse("")))
    val requestMap = requestLinesSplitted.toMap
    Log.debug(requestMap)
    val get = findGetInRequest(requestLines)
    Log.debug("findGet", timer.timerElapsed)

    val isWebSocket = requestMap.contains("Sec-WebSocket-Key") && WebSocket.handshake(socketOut, requestMap("Sec-WebSocket-Key"))
    Log.debug("WebSocket evaluated", timer.timerElapsed)

    if (isWebSocket) {
      Log.debug("Handling as a WebSocket")
      webSocketHandler(new WebSocket(get.getOrElse(Get("", "", "")), requestMap, socket)) //TODO: does it have always a get param?
      Log.debug("WebSocket handled", timer.timerElapsed)
    } else if (get.nonEmpty) {
      Log.debug("Try to handle as get")
      Log.debug("Get: " + get.get)
      Log.info(Thread.currentThread().getName,get.get.url)
      val responseOption = getHandler(get.get)
      Log.debug("get handled", timer.timerElapsed)
      if (responseOption.nonEmpty) {
        Log.debug("Handling as a Get")
        val response = responseOption.get
        socketOut.write("HTTP/1.1 200 OK\r\n".getBytes("ASCII"))
        //socketOut.write("Content-Type: application/json\r\n".getBytes("ASCII"))
        socketOut.write(("Content-Length: " + response.length + "\r\n").getBytes("ASCII"))
        socketOut.write("\r\n".getBytes("ASCII"))
        socketOut.write(response.getBytes("ASCII"))
        Log.debug("Get response wrote", timer.timerElapsed)
      } else {

        def toResourceFile(path: String) = {
          val url = this.getClass.getClassLoader.getResource(path)
          if (url != null) Success(url.getFile.asFile)
          else Failure(new IllegalArgumentException(s"resource $path not found."))
        }

        val selectedResource = toResourceFile(get.map(_.url).getOrElse(""))
        val resourceExists = selectedResource.map(_.existsAndFile).getOrElse(false)

        Log.debug("Resource checked", timer.timerElapsed)

        if (resourceExists) {
          Log.debug("Handling as a Resource")
          Log.debug("Resource " + selectedResource)
          val bytes = Files.readAllBytes(Paths.get(selectedResource.get.toURI))
          Log.debug("Resource size: " + bytes.size)
          socketOut.write("HTTP/1.1 200 OK\r\n".getBytes("ASCII"))
          //socketOut.write("Access-Control-Allow-Origin: *\r\n".getBytes("ASCII")) //TODO make parameterizable
          socketOut.write(("Content-Length: " + bytes.length + "\r\n").getBytes("ASCII"))
          socketOut.write("Content-Type: text/javascript\r\n".getBytes("ASCII"))
          socketOut.write("\r\n".getBytes("ASCII"))
          socketOut.write(bytes)
          Log.debug("Resource written", timer.timerElapsed)
        } else {
          Log.debug("NO Response")
          // https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
          socketOut.write("HTTP/1.0 404 Not Found\r\n\r\n".getBytes("ASCII"))
          Log.debug("404 written", timer.timerElapsed)
        }
      }

      Log.debug("Before flush", timer.timerElapsed)
      socketOut.flush()
      Log.debug("After flush", timer.timerElapsed)
      CloseablePlus.closeQuietly(socketOut, socketIn, socket)
      Log.debug("socket closed", timer.timerElapsed)
    }

    Log.info(Thread.currentThread().getName,"Ended")
  }

  def findGetInRequest(lines: List[String]): Option[Get] = {
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