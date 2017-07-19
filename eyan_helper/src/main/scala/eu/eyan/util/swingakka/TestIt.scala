package eu.eyan.util.swingakka

import eu.eyan.util.swing.SwingPlus
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import javax.swing.JFrame
import akka.actor.ActorRef
import javax.swing.JButton
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorLogging
import java.awt.event.ActionEvent
import java.awt.Component
import eu.eyan.util.swing.JPanelWithFrameLayout

object TestIt extends App {
  
  val system: ActorSystem = ActorSystem("SwingActorMarriage")
  
  val printer: ActorRef = system.actorOf(Props[Printer], "printerActor")

  val panel = new JPanelWithFrameLayout
  panel.newRow
  panel.newColumn
  panel.addButton("föl").name("föl").onAction_Actor(printer) 
  panel.newColumn
  panel.addButton("le").name("le").onAction_Actor(printer)
  panel.newColumn
  panel.addButton("check").name("check").onAction_Actor(printer) 
  
  val frame = new JFrame().name("test").onCloseExit.component(panel).packAndSetVisible
}


class Printer extends Actor with ActorLogging {
  def receiver(ct: Int):Receive = {
    case e:ActionEvent => 
      val buttonName = e.getSource.asInstanceOf[Component].getName
      
      if(buttonName == "check") println("ActionEvent: "+ct) 
      if(buttonName == "föl") context.become(receiver(ct+1), false) 
      if(buttonName == "le") context.unbecome
      
      // future pipeTo self
      // implicit val exec = context.dispatcher.asInstanceOf[Executor with ExecutionContext]
      
      // context.parent
      // context.stop(self - ???)
      
      // Actor with ActorLogging
      // log.debug("xyz {}", param)
      
      // context.actorOf() -> why not system?
      
      // var cache = Set.empty[String] ,  += , -= 
      
      // import scala.concurrent.duration._
      // context.setReceiveTimeout(10.seconds)
      
      // import context.dispatcher
      // context.system.scheduler.scheduleOnce(10.seconds)

      // context.system.scheduler.scheduleOnce(10.seconds, self, Timeout)
      // receive case Timeout => ...
  }
  
  def receive = receiver(1)
}