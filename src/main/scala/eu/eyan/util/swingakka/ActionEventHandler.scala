package eu.eyan.util.swingakka

import akka.actor.ActorRef

// FIXME deprecated dont use it...
object ActionEventHandler {
  def apply() = {new ActionEventHandler}
}
class ActionEventHandler {
  var actorRef: ActorRef = null
  def event[T](event: T) = if (actorRef != null) actorRef ! event
  def !(actorRef: ActorRef) = this.actorRef = actorRef
}