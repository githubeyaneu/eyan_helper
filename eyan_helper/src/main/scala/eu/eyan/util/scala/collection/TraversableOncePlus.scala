package eu.eyan.util.scala.collection

object TraversableOncePlus {
  implicit class TraversableOnceImplicit[TYPE <: TraversableOnce[_]](traversableOnce: TYPE){
    def mkStringNL = traversableOnce.mkString("\r\n")
		def mkStringListInNewLine(indent: String = "  ") = traversableOnce.mkString("\r\n"+indent, "\r\n"+indent, "\r\n")
  }
}