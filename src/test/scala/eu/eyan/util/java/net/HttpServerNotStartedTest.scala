package eu.eyan.util.java.net

import java.net.BindException

import eu.eyan.log.Log
import eu.eyan.testutil.{ScalaEclipseJunitRunnerTheories, TestPlus}
import eu.eyan.util.io.CloseablePlus.closeQuietly
import org.junit.rules.Timeout
import org.junit.runner.RunWith
import org.junit.{Rule, Test}

import scala.util.Failure

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class HttpServerNotStartedTest extends TestPlus {
  Log.activateDebugLevel

  @Rule def globalTimeout: Timeout = Timeout.seconds(TestPlus.DEFAULT_WAIT_TIME*2)

  private def createHttpServer = new HttpServer()

  @Test def serverNotBlocks = createHttpServer

  @Test def notStarted_NotWorks = {
    val server = createHttpServer
    val portTest$ = server.port$.test
    waitFor(portTest$.assertValues(None))
  }

  @Test def two_servers_same_port = {
    var server1: HttpServer = null
    var server2: HttpServer = null
    try {
      server1 = createHttpServer.start(12345)
      "http://localhost:12345".urlGet() ==> Failure(new Exception("http://localhost:12345 not available, response code:404, response message: Not Found"))

      server2 = createHttpServer.start(12345) ==> new BindException("Address already in use: JVM_Bind")
    } finally {
      closeQuietly(server1, server2)
    }
  }
}
