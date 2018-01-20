package eu.eyan.util.scala.collection.immutable

import eu.eyan.util.scala.collection.TraversableOncePlus.TraversableOnceImplicit

object ListPlus {
  implicit class ListImplicit[TYPE<: List[_]](list:TYPE) extends TraversableOnceImplicit(list){
  }
}