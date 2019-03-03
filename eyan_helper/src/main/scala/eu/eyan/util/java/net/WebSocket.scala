package eu.eyan.util.java.net

import eu.eyan.log.Log
import java.net.Socket
import eu.eyan.util.java.lang.ThreadPlus
import eu.eyan.util.scala.Try
import java.io.PrintWriter
import java.security.MessageDigest
import java.io.BufferedOutputStream
import java.io.OutputStream
import java.io.InputStream
import sun.misc.BASE64Encoder
import rx.lang.scala.Observable
import eu.eyan.util.scala.TryCatch
import rx.Completable
import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observer
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import java.io.Closeable
import java.util.UUID
import rx.lang.scala.Scheduler
import rx.lang.scala.Scheduler
import rx.lang.scala.schedulers.IOScheduler
import eu.eyan.util.io.CloseablePlus

object WebSocket {
  private def logBytes(title: String, bytes: Array[Byte]): Unit = Log.trace(title + bytes.map(b => String.format("%02X ", b.asInstanceOf[Object])).mkString)

  def reiceveMessage(inputStream: InputStream): Option[String] = {
    val messageHeader = Array.ofDim[Byte](2)
    inputStream.read(messageHeader)
    logBytes("Headers:", messageHeader)
    val operationCode = messageHeader(0) & 0x0F
    if (operationCode == 8) {
      Log.debug("Client closed the websocket.")
      None
    } else {
      val MASK_SIZE = 4
      val payloadSize = (messageHeader(1) & 0xFF) - 0x80
      val payloadBuffer = Array.ofDim[Byte](MASK_SIZE + payloadSize)
      inputStream.read(payloadBuffer)
      logBytes(s"Payload($payloadSize):", payloadBuffer)
      val mask = payloadBuffer.slice(0, 4)
      val data = payloadBuffer.slice(4, payloadBuffer.length)
      for (i <- 0 until data.length) data(i) = (data(i) ^ mask(i % mask.length)).toByte
      Some(new String(data))
    }
  }

  private def sendMessage(outputStream: OutputStream, msg: String): Unit = {
    val SINGLE_FRAME_UNMASKED = 0x81

    val msgBytes = msg.getBytes
    logBytes("Sending to client", msgBytes)

    val os = new BufferedOutputStream(outputStream)
    os.write(SINGLE_FRAME_UNMASKED)
    os.write(msgBytes.length)
    os.write(msgBytes)
    os.flush
  }

  def handshake(outputStream: OutputStream, secWebsocketKey: String): Boolean = {
    val NEW_LINE = "\r\n"
    val sha1 = MessageDigest.getInstance("SHA-1")
    val base64Encoder = new BASE64Encoder()
    val socketOutPw = new PrintWriter(outputStream)

    val secWebsocketKeyWithGlobal = secWebsocketKey + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11"
    val hash = Try(base64Encoder.encode(sha1.digest(secWebsocketKeyWithGlobal.getBytes)))

    Log.info("Hash: " + hash)

    val responseHttp = "HTTP/1.1 101 Switching Protocols"
    val responseUpgradeWebsocket = "Upgrade: websocket"
    val responseConnectionUpgrade = "Connection: Upgrade"
    val responseWebSocketAccept = s"Sec-WebSocket-Accept: ${hash.getOrElse("")}"
    val responseLines = List(responseHttp, responseUpgradeWebsocket, responseConnectionUpgrade, responseWebSocketAccept)
    val response = responseLines.mkString(NEW_LINE)

    Log.info("Response: " + response)
    if (hash.isSuccess) socketOutPw.write(response)
    socketOutPw.write(NEW_LINE + NEW_LINE)
    socketOutPw.flush

    hash.isSuccess
  }
}
case class WebSocket(val get: Get, val parameters: Map[String, String], private val socket: Socket) extends Closeable {
  def receivedMessages = receivedMessages_.distinctUntilChanged.subscribeOn(IOScheduler())
  def socketClosed = socketClosed_.distinctUntilChanged.subscribeOn(IOScheduler())
  def sendMessageObserver = Observer[String](onSendMessageNext: String => Unit, onSendMessageError: Throwable => Unit, onSendMessageCompleted: () => Unit)
  def close = closeWebSocket
  
  private val socketOut = socket.getOutputStream
  private val socketClosed_ = BehaviorSubject[String]()
  private val receivedMessages_ = BehaviorSubject[String]()
  
  private val socketId = parameters.get("Sec-WebSocket-Key").getOrElse(UUID.randomUUID.toString)

  //FIXME do somehow better threading.
  ThreadPlus run
    TryCatch(
      while ("closed" != socketClosed_.get) {
        val receivedMessage = WebSocket.reiceveMessage(socket.getInputStream)
        if (receivedMessage.nonEmpty) {
          Log.debug(s"$socketId Recieved from client: " + receivedMessage.get)
          receivedMessages_.onNext(receivedMessage.get)
        } else {
          Log.debug(s"$socketId socket ended")
          close
          receivedMessages_ onCompleted ()
          Log.debug(s"$socketId socket receiver completed")
        }
      },
      (t: Throwable) => {
        close
        Log.error(s"$socketId socket problem", t)
        receivedMessages_ onError t
      })

  private def onSendMessageNext(message: String) = WebSocket.sendMessage(socketOut, message)
  private def onSendMessageError(throwable: Throwable) = WebSocket.sendMessage(socketOut, "Error "+throwable.getMessage+"<br/>\r\n"+throwable.getStackTrace.mkString("<br/>\r\n"))
  private def onSendMessageCompleted(): Unit = Log.trace("Nothing to do.")
  
  private def closeWebSocket = {
    CloseablePlus closeQuietly socket
    Log info "socket closed"
    socketClosed_ onNext "closed"
    socketClosed_ onCompleted ()
    Log info "socket closed sent"
  }
}