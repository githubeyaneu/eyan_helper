package eu.eyan.util.java

import java.util.function.Consumer

object Java8ScalaHelper {
  def newConsumer[TYPE](mthod: TYPE => Unit):Consumer[TYPE] = (v1: TYPE) => mthod.apply(v1)
}