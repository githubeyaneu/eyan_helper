package eu.eyan.util.rx.lang.scala.subjects

import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableImplicit
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableImplicitT

object BehaviorSubjectPlus {
  implicit class BehaviorSubjectImplicit[BS <: BehaviorSubject[_]](behaviorSubject: BS) extends ObservableImplicit[BS](behaviorSubject)
  
  implicit class BehaviorSubjectImplicitT[T](behaviorSubject: BehaviorSubject[T]) extends ObservableImplicitT[T, BehaviorSubject[T]](behaviorSubject){
    @deprecated("Using this is not reactive...", "2020.02.04")
    def gettt: T = {
      var result = null.asInstanceOf[T]
      behaviorSubject.take(1).subscribe(s => result = s)
      result
    }
  }
}