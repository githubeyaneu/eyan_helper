package eu.eyan.util.rx.lang.scala

import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable

object ObservablePlus {
  // FIXME: why cannot implicits have more types???
  // https://stackoverflow.com/questions/41486024/scala-multiple-type-parameters-for-implicit-class
  // https://stackoverflow.com/questions/5598085/where-does-scala-look-for-implicits/5598107#5598107
  implicit class ObservableImplicit[O <: Observable[_]](observable: O) {
    def get[T] = {
      var result = null.asInstanceOf[T]
      observable.take(1).subscribe(s => result = s.asInstanceOf[T])
      result
    }
  }
}