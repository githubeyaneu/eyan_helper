package eu.eyan.util.java.net

import java.io._
import java.net.Socket
import java.security.MessageDigest
import java.util.UUID

import eu.eyan.log.Log
import eu.eyan.util.io.CloseablePlus
import eu.eyan.util.java.lang.ThreadPlus
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import eu.eyan.util.scala.Try
import rx.lang.scala.Observer
import rx.lang.scala.schedulers.IOScheduler
import rx.lang.scala.subjects.BehaviorSubject
import sun.misc.BASE64Encoder

object WebSocket {

  def handshake(outputStream: OutputStream, secWebsocketKey: String): Boolean = {
    Log.info("")
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

    Log.info("ResponseLines: " + responseLines.mkString(", "))
    if (hash.isSuccess) socketOutPw.write(response)
    socketOutPw.write(NEW_LINE + NEW_LINE)
    socketOutPw.flush()

    hash.isSuccess
  }
}
case class WebSocket(get: Get, parameters: Map[String, String], private val socket: Socket) extends Closeable {
  def receivedMessages = receivedMessages_.distinctUntilChanged.subscribeOn(IOScheduler())
  def socketClosed = socketClosed_.distinctUntilChanged.subscribeOn(IOScheduler())
  def sendMessageObserver = Observer[String](onSendMessageNext: String => Unit, onSendMessageError: Throwable => Unit, () => onSendMessageCompleted())
  def close() = closeWebSocket

  private val socketOut = new BufferedOutputStream(socket.getOutputStream)
  private val socketClosed_ = BehaviorSubject[String]()
  private val receivedMessages_ = BehaviorSubject[String]()

  private val socketId = parameters.getOrElse("Sec-WebSocket-Key", UUID.randomUUID.toString)

  //FIXME do somehow better threading.
  val receiving = ThreadPlus.runObservable(
    while ("closed" != socketClosed_.take1Synchronous) {
      val receivedMessage = reiceveMessage(socket.getInputStream) // BLOCKS
      if (receivedMessage.nonEmpty) {
        Log.debug(s"$socketId Recieved from client: " + receivedMessage.get)
        receivedMessages_.onNext(receivedMessage.get)
      } else {
        Log.debug(s"$socketId socket ended")
        close()
        receivedMessages_ onCompleted ()
        Log.debug(s"$socketId socket receiver completed")
      }
    })

  receiving.subscribe(res => { }, t => {
    Log.error(s"recieving: $socketId socket problem", t)
    close()
    receivedMessages_ onError t
  }, () => Log.debug("recieving: ended"))

  private def onSendMessageNext(message: String) = sendMessage(socketOut, message)
  private def onSendMessageError(throwable: Throwable) = sendMessage(socketOut, "Error " + throwable.getMessage + "<br/>\r\n" + throwable.getStackTrace.mkString("<br/>\r\n"))
  private def onSendMessageCompleted(): Unit = Log.trace("Nothing to do.")

  private def closeWebSocket = {
    CloseablePlus closeQuietly socket
    Log info "socket closed"
    socketClosed_ onNext "closed"
    socketClosed_ onCompleted ()
    Log info "socket closed sent"
  }

  private def logBytes(title: String, bytes: Array[Byte]): Unit = Log.trace(title + bytes.map(b => String.format("%02X ", b.asInstanceOf[Object])).mkString)

  private def reiceveMessage(inputStream: InputStream): Option[String] = {
    Log.info("")
    val messageHeader = Array.ofDim[Byte](2)
    inputStream.read(messageHeader) // BLOCKS
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
      for (i <- data.indices) data(i) = (data(i) ^ mask(i % mask.length)).toByte
      val result = new String(data)
      Log.info(result)
      Some(result)
    }
  }

  private def sendMessage(os: BufferedOutputStream, msg: String): Unit = {
    Log.info(msg)
    val SINGLE_FRAME_UNMASKED = 0x81

    val msgBytes = msg.getBytes
    logBytes("Sending to client", msgBytes)

    os.write(SINGLE_FRAME_UNMASKED)
    os.write(msgBytes.length)
    os.write(msgBytes)
    os.flush()
  }
}