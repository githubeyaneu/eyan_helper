package eu.eyan.util.swingakka

import java.awt.Component
import java.awt.event.ActionEvent
import java.util.concurrent.Executor

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.pattern.pipe
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.JFrame

object TestIt extends App {
  val system: ActorSystem = ActorSystem("SwingActorMarriage")
  val printer: ActorRef = system.actorOf(Props[Printer], "printerActor")
  printer ! "startWindow"
}

class Printer extends Actor with ActorLogging {
  def receiver(ct: Int): Receive = {

    case "startWindow" =>
      val panel = new JPanelWithFrameLayout().newRow
      panel.newColumn.addButton("föl").name("föl").onAction_Actor(self)
      panel.newColumn.addButton("le").name("le").onAction_Actor(self)
      panel.newColumn.addButton("check").name("check").onAction_Actor(self)
      panel.newColumn.addButton("future").onAction(self ! "future")
      panel.newColumn.addButton("parent").onAction(self ! "parent")
      panel.newColumn.addButton("log").onAction(self ! "log")
      panel.newColumn.addButton("stop").onAction(self ! "stop")
      panel.newColumn.addButton("child").onAction(self ! "child")
      panel.newColumn.addButton("ReceiveTimeout").onAction(self ! "ReceiveTimeout")
      panel.newColumn.addButton("scheduleOnce").onAction(self ! "scheduleOnce")
      val frame = new JFrame().name("test").onCloseExit.component(panel).packAndSetVisible

    case e: ActionEvent =>
      val buttonName = e.getSource.asInstanceOf[Component].getName
      if (buttonName == "check") println("ActionEvent: " + ct)
      if (buttonName == "föl") context.become(receiver(ct + 1), false)
      if (buttonName == "le") context.unbecome

    case "future" =>
      implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
      val future = Future[String] { println("future"); "fut" }
      future pipeTo self

    case "stop"   => context.stop(self)

    case "parent" => context.parent

    case "log" =>
      // Actor with ActorLogging
      log.info("xyz {} zyx", "paramm")

    case "child" =>
      context.actorOf(Props[Printer]) ! "startWindow"

      var cache = Set.empty[String] // ,  += , -=
      cache += "abc"
      var list = List.empty[String] // ,  += , -=
      list = ("" :: list) :+ "abc"

    case "ReceiveTimeout" =>
      import scala.concurrent.duration._
      context.setReceiveTimeout(3.seconds)
      println("set Receive Timeout")

    case "scheduleOnce" =>
      import scala.concurrent.duration._
      import context.dispatcher
      context.system.scheduler.scheduleOnce(3.seconds, self, "Timeout")

    case sg => println("unhandled message (" + sg.getClass()+"):"+sg)
  }

  def receive = receiver(1)
}