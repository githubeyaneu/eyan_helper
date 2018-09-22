package eu.eyan.util.rx.lang.scala.subjects

import rx.lang.scala.subjects.BehaviorSubject

object BehaviorSubjectPlus {
  // FIXME: why cannot implicits have more types???
  // https://stackoverflow.com/questions/41486024/scala-multiple-type-parameters-for-implicit-class
  // https://stackoverflow.com/questions/5598085/where-does-scala-look-for-implicits/5598107#5598107
  implicit class BehaviorSubjectImplicit[BS <: BehaviorSubject[_]](behaviorSubject: BS) {
    def get[T] = {
      var result = null.asInstanceOf[T]
      behaviorSubject.take(1).subscribe(s => result = s.asInstanceOf[T])
      result
    }
  }
}