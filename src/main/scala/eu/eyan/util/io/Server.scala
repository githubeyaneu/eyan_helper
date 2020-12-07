package eu.eyan.util.io

import java.io._
import java.net.ServerSocket
import java.net.Socket
import eu.eyan.util.scala.TryCatch
import javax.net.SocketFactory

object Server extends App {
  TryCatch(test(), (e:Throwable) => e.printStackTrace()) 

  def test(): Unit = {
    val port = 11111
    val serverSocket = new ServerSocket(port)
    
    println("wait for connection on "+port)
    val client = blockUntilConnection(serverSocket)
    val message = readMessage(client)
    println(message)
    writeMessage(client, message)
  }

  def blockUntilConnection(serverSocket: ServerSocket) = serverSocket.accept()

  def readMessage(socket: Socket) = {
    val bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream))
    val buffer = Array.ofDim[Char](200)
    // blockiert bis Nachricht empfangen
    val charCt = bufferedReader.read(buffer, 0, 200)
    new String(buffer, 0, charCt)
  }

  def writeMessage(socket: Socket, message: String): Unit = {
    val printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream))
    printWriter.print(message)
    printWriter.flush()
    printWriter.close()
  }

}