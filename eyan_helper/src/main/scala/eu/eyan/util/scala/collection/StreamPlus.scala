package eu.eyan.util.scala.collection

object StreamPlus {
//FIXME...
  implicit class StreamPlusImplicit[TYPE, STREAM <: Stream[TYPE]](stream: STREAM) {
    def splitter(splitCondition: TYPE => Boolean) = StreamPlus.split(stream)(splitCondition)
  }

  def split[TYPE](stream: Stream[TYPE])(splitCondition: TYPE => Boolean, aggregate: Stream[TYPE] = Stream.Empty): Stream[Stream[TYPE]] = {
    if (stream.isEmpty)
      Stream(aggregate)
    else if (splitCondition(stream.head))
      Stream.cons(aggregate, split(stream.tail)(splitCondition))
    else
      split(stream.tail)(splitCondition, aggregate :+ stream.head)
  }
}