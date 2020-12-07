package eu.eyan.util.java.net

import java.net.URI
import java.nio.ByteBuffer

import eu.eyan.log.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import rx.lang.scala.subjects.ReplaySubject

class WebSocketClientForTest(serverURI: URI) extends WebSocketClient(serverURI) {
  println("WSC",isOpen)
  connect()

  val onOpen$$ = ReplaySubject[String]()
  override def onOpen(handshakedata: ServerHandshake): Unit = {
    Log.debug("onOpen")
    onOpen$$.onNext("onOpen")
  }

  val onClose$$ = ReplaySubject[(Int, String, Boolean)]()
  override def onClose(code: Int, reason: String, remote: Boolean): Unit = {
    Log.debug("onClose", (code, reason, remote))
    onClose$$.onNext((code, reason, remote))
  }

  val onMessage$$ = ReplaySubject[String]()
  override def onMessage(message: String): Unit = {
    Log.debug("onMessage", message)
    onMessage$$.onNext(message)
  }

  override def onMessage(message: ByteBuffer): Unit = new NotImplementedError()

  val onError$$ = ReplaySubject[Exception]()
  override def onError(ex: Exception): Unit = {
    Log.error("onError",ex)
    onError$$.onNext(ex)
  }

  val actions$$ = ReplaySubject[String]()
  onOpen$$.subscribe(actions$$)
  onClose$$.map(_.toString()).subscribe(actions$$)
  onMessage$$.map("onMessage: "+_).subscribe(actions$$)
  onError$$.map(_.toString()).subscribe(actions$$)
}
