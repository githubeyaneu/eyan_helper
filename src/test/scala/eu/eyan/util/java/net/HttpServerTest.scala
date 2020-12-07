package eu.eyan.util.java.net

import java.net.{SocketTimeoutException, URI}
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

import eu.eyan.log.Log
import eu.eyan.testutil.{ScalaEclipseJunitRunnerTheories, TestPlus}
import eu.eyan.util.java.util.concurrent.NamedThreadFactory
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.junit.{After, Before, Rule, Test}

import scala.util.{Failure, Random, Success}

object HttpServerTest extends App {
  Log.activateDebugLevel
  new HttpServerTest()
}

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class HttpServerTest extends TestPlus {
  Log.activateDebugLevel

  val WEBSOCKET_OPEN = "onOpen"
  val WEBSOCKET_CLOSE_NORMAL = "(1000,,false)" //https://github.com/Luka967/websocket-close-codes
  val WEBSOCKET_CLOSE_ABNORMAL = "(1006,,true)"

  @Rule def timeout = Timeout.millis(TestPlus.DEFAULT_WAIT_TIME * 2 * 8)

  @After def stopServer(): Unit = server.close()

  var openedPort: Integer = -1

  val server: HttpServer = {
    val server = new HttpServer().start()
    server.port$.filter(_.nonEmpty).map(_.get).take(1).subscribe(p => openedPort = p)
    server
  }

  @Before def startTest = Log.debug("---------- Start Test ----------")

  @Test def http_404 =
    httpGet("") ==> Failure(new Exception(localhost + " not available, response code:404, response message: Not Found"))

  @Test def afterClose_NoResponse = {
    val portTest$ = server.port$.test
    waitFor(portTest$.assertValues(Some(openedPort)))
    server.close()
    httpGet("") ==> Failure(new SocketTimeoutException("connect timed out"))
    waitFor(portTest$.assertValues(Some(openedPort), None))
  }

  @Test def startTwiceNoError = server.start()

  @Test def noWebSocketHandler_no_response = {
    val webSocketClient = openWebsocket("")
    waitFor(webSocketClient.actions$$.test.assertValues(WEBSOCKET_OPEN, WEBSOCKET_CLOSE_ABNORMAL))
  }

  @Test def httpServerError_DoesNotBlock = {
    server.setWebSocketHandler(webSocket => {
      webSocket.get.url ==> "this causes an error"
    })
    val webSocketClient = openWebsocket("/ws")
    waitFor(webSocketClient.actions$$.test.assertValues(WEBSOCKET_OPEN))
    getHandler_handles
  }

  @Test def webSocketHandler_normal = {
    import java.util.concurrent.atomic.AtomicReference
    val testResult = new AtomicReference[String]("")
    server.setWebSocketHandler(webSocket => {
      testResult.set(webSocket.get.toString)
      webSocket.receivedMessages.subscribe(msg => {
        webSocket.sendMessageObserver.onNext("Ret" + msg)
        webSocket.close()
      })

    })
    val webSocketClient = openWebsocket("/ws")
    waitFor(webSocketClient.isOpen)
    webSocketClient.send("ABC")
    waitFor(testResult.get ==> "Get(ws,,)")
    waitFor(webSocketClient.actions$$.test.assertValues(WEBSOCKET_OPEN, "onMessage: RetABC", WEBSOCKET_CLOSE_ABNORMAL /* TODO this should be a normal close */))
    webSocketClient.close()
  }

  @Test def getHandler_handles = {
    server.setGetHandler(_ => Option("OK"))
    httpGet("/krixkrax").get ==> "OK"
  }

  @Test def getHandler_performance = {
    Log.activateDebugLevel
    val REQUEST_NUM = 10 * 1000
    val resultCounter = new AtomicInteger(0)
    server.setGetHandler(get => Option(get.url + "OK"))
    httpGet("/get" , 100 * 1000)


    val threadsBefore = Thread.getAllStackTraces.size()
    import scala.collection.JavaConverters._


    val threadsNamesBefore = Thread.getAllStackTraces.keySet().asScala.map(_.getName)
    val pool = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors(), new NamedThreadFactory(this.getClass.getName))


    for (ct <- 1 to REQUEST_NUM) {
      if(Random.nextInt(5)==0) pause(1) //FIXME TODO why is this necessary???
      pool.execute(() => {
      val res = httpGet("/get" + ct, 100 * 1000)
      res ==> Success("get" + ct + "OK")
      if(res.isFailure) Log.info(res)
      resultCounter.incrementAndGet()
    })
    }

    val threadsAfterTest = Thread.getAllStackTraces.size()
    waitFor(resultCounter.get() ==> REQUEST_NUM)
    (Thread.getAllStackTraces.keySet().asScala.map(_.getName) -- threadsNamesBefore).toList.sorted.zipWithIndex.foreach(println)


    val httpServerThreads = Runtime.getRuntime.availableProcessors -1 // TODO FIXME why -1???
    val testThreads = Runtime.getRuntime.availableProcessors
    println("threadsBefore", threadsBefore)
    // test 24 OK
    // server only 23 ???
    threadsAfterTest ==> ("expected thread number", threadsBefore + httpServerThreads + testThreads)
  }

  @Test def resource_exists = httpGet("/exists.txt").get ==> "exists"

  @Test def resource_NotExists = httpGet("/doesNotExists.txt") ==> Failure(new Exception(localhost + "/doesNotExists.txt not available, response code:404, response message: Not Found"))

  private def localhost = "http://localhost:" + openedPort

  private def openWebsocket(path: String) = new WebSocketClientForTest(new URI(localhost + path))

  private def httpGet(path: String, timeout: Int = 100) = (localhost + path).urlGet(timeout)

  val activeGetCount = new AtomicInteger()

}
