package eu.eyan.util.java

import java.util.function.Consumer

object Java8ScalaHelper {
  def newConsumer[TYPE](mthod: TYPE => Unit):Consumer[TYPE] = new Consumer[TYPE]() { override def accept(v1: TYPE): Unit = mthod.apply(v1) }
}