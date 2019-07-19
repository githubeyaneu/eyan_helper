package eu.eyan.util.scala.collection

import eu.eyan.log.Log

object StreamPlus {
  implicit class StreamImplicit[TYPE](stream: Stream[TYPE]) {

    def splitToStreams(splitCondition: TYPE => Boolean, next: Stream[TYPE] = Stream.Empty): Stream[Stream[TYPE]] = {
      if (stream.isEmpty && next.isEmpty) {
        Log.trace("stream is empty, next is empty")
        Stream.Empty
      } else if (stream.isEmpty) {
        Log.trace("only stream is empty")
        Stream(next)
      } else if (splitCondition(stream.head) && next.isEmpty) {
        Log.trace("split, next is empty" + stream.head)
        stream.tail.splitToStreams(splitCondition, Stream(stream.head))
      } else if (splitCondition(stream.head)) {
        Log.trace("split, next not empty" + stream.head)
        val ret: Stream[Stream[TYPE]] = Stream(next) #::: stream.tail.splitToStreams(splitCondition, Stream(stream.head))
        ret
      } else {
        Log.trace("no split" + stream.head)
        stream.tail.splitToStreams(splitCondition, next :+ stream.head)
      }
    }
    
  }
}