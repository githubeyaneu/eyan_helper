package eu.eyan.util.rx.lang.scala.subjects

import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableImplicit

object BehaviorSubjectPlus {
  implicit class BehaviorSubjectImplicit[BS <: BehaviorSubject[_]](behaviorSubject: BS) extends ObservableImplicit[BS](behaviorSubject)
}