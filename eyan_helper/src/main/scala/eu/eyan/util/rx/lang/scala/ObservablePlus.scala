package eu.eyan.util.rx.lang.scala

import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable
import rx.lang.scala.Subscription
import eu.eyan.util.scala.BooleanPlus.BooleanImplicit

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

  implicit class ObservableVarargsImplicit[T](observables: Observable[T]*) {
    def combineLatest = ObservablePlus.toList(observables: _*)
  }

  implicit class ObservableAnyVarargsImplicit(observables: Observable[Any]*) {
    def combineLatest = ObservablePlus.toList(observables: _*)
  }

  implicit class ObservableListImplicit[T](observables: List[Observable[T]]) {
    def combineLatest = ObservablePlus.toList(observables: _*)
  }

  def toList[T](observables: Observable[T]*): Observable[List[T]] = Observable.combineLatest(observables.toArray.toIterable)(_.toList)

  implicit class ObservableImplicitBoolean[O <: Observable[Boolean]](observableBoolean: O) {
    def ifElse[T](trueObs: Observable[T], falseObs: Observable[T]): Observable[T] = {
      val textsCombined = ObservablePlus.toList(trueObs, falseObs)
      val conditionAndTexts = observableBoolean combineLatest textsCombined
      def selectText(conditionAndTexts: (Boolean, List[T])) = conditionAndTexts._2(conditionAndTexts._1 ? 0 | 1)
      conditionAndTexts map selectText
    }

    def negate = not
    def not = observableBoolean.map(!_)

    def and[O2 <: Observable[Boolean]](observableBoolean2: O2) = observableBoolean.combineLatest(observableBoolean2).map({ case (b1, b2) => b1 && b2 })
    def or[O2 <: Observable[Boolean]](observableBoolean2: O2) = observableBoolean.combineLatest(observableBoolean2).map({ case (b1, b2) => b1 || b2 })
  }

  implicit class ObservableImplicitInt[O <: Observable[Int]](observableInt: O) {
    def emptySingularPlural[T](empty: Observable[T], singular: Observable[T], plural: Observable[T]): Observable[T] = {
      val textsCombined = ObservablePlus.toList(empty, singular, plural)

      val nrAndTexts = observableInt combineLatest textsCombined

      def selectTitleText(nrAndTexts: (Int, List[T])) = {
        val nr = nrAndTexts._1
        val texts = nrAndTexts._2
        val idx = if (nr < 1) 0 else ((nr == 1) ? 1 | 2)
        texts(idx)
      }
      nrAndTexts map selectTitleText
    }
  }
}