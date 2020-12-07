package eu.eyan.util.java.net

import eu.eyan.util.string.StringPlus.StringPlusImplicit
import rx.lang.scala.Observable
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import eu.eyan.log.Log
import rx.lang.scala.Observer
import rx.lang.scala.schedulers.IOScheduler

object HttpServerManualTest extends App {
  Log.activateInfoLevel
  var wsCt = 0;
  val server = new HttpServer().setGetHandler(get => None).setWebSocketHandler(wsHandler)
  server.start(12345)
  
  def wsHandler(ws: WebSocket) = {
	  Log.info(ws.get.url)
	  Log.info(ws.parameters)
		
    wsCt += 1
    val ct = wsCt
    
    s"new ws$ct started".asSingle subscribe ws.sendMessageObserver
    
    ws.receivedMessages.map(s"ws$ct: " + _).subscribe(msg => Log.info(msg))
    
    val interval = Observable.interval(Duration.apply(5, TimeUnit.SECONDS)).share.subscribeOn(IOScheduler())
    val intervalSubscription = interval.map(s"ws$ct: msg" + _) subscribe ws.sendMessageObserver
    ws.socketClosed.subscribe(s=> intervalSubscription.unsubscribe)
  }
}