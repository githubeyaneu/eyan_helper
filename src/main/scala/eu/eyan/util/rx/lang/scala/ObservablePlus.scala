package eu.eyan.util.rx.lang.scala

import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable
import rx.lang.scala.Subscription
import eu.eyan.util.scala.BooleanPlus.BooleanImplicit
import scala.reflect.ClassTag

object ObservablePlus {
  // FIXME: why cannot implicits have more types???
  // https://stackoverflow.com/questions/41486024/scala-multiple-type-parameters-for-implicit-class
  // https://stackoverflow.com/questions/5598085/where-does-scala-look-for-implicits/5598107#5598107
  implicit class ObservableImplicit[O <: Observable[_]](observable: O) {
    @deprecated("Using this is not reactive...", "2020.02.04")
    def get[T]: T = {
      var result = null.asInstanceOf[T]
      observable.take(1).subscribe(s => result = s.asInstanceOf[T])
      result
    }
  }
  implicit class ObservableImplicitT[T, O <: Observable[T]](observable: O) {
    def combineLatestFirst[T2](observable2: Observable[T2]): Observable[T] = observable.combineLatest(observable2).map(_._1)
    def takeLatestOf[T2](observable2: Observable[T2]): Observable[T2] = observable.withLatestFrom(observable2)((t, t2) => t2)
    def withLatestOf[T2](observable2: Observable[T2]): Observable[(T,T2)] = observable.withLatestFrom(observable2)((t, t2) => (t,t2))
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

  def toArray[T](observables: Seq[Observable[T]])(implicit ev: ClassTag[T]): Observable[Array[T]] = {
    val array$ = toSeq(observables).map(_.toArray)
    array$
  }

  def toSeq[T](observables: Seq[Observable[T]])(implicit ev: ClassTag[T]): Observable[Seq[T]] = {
    //noinspection ScalaStyle
    val list$ = Observable.combineLatest(observables.toIterable)(x => x)
    list$
  }

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
        val idx = if (nr < 1) 0 else (nr == 1) ? 1 | 2
        texts(idx)
      }
      nrAndTexts map selectTitleText
    }
  }
  
  def combineLatest[T1, T2](o1: Observable[T1], o2: Observable[T2]) =
		  List(o1, o2)
		  .combineLatest
		  .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2]))
  
  def combineLatest[T1, T2, T3, T4](o1: Observable[T1], o2: Observable[T2], o3: Observable[T3], o4: Observable[T4]) =
		  List(o1, o2, o3, o4)
		  .combineLatest
		  .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2], list(2).asInstanceOf[T3], list(3).asInstanceOf[T4]))
  
  def combineLatest[T1, T2, T3, T4, T5, T6, T7, T8, T9](o1: Observable[T1], o2: Observable[T2], o3: Observable[T3], o4: Observable[T4], o5: Observable[T5], o6: Observable[T6], o7: Observable[T7], o8: Observable[T8], o9: Observable[T9]) =
		  List(o1, o2, o3, o4, o5, o6, o7, o8, o9)
		  .combineLatest
		  .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2], list(2).asInstanceOf[T3], list(3).asInstanceOf[T4], list(4).asInstanceOf[T5], list(5).asInstanceOf[T6], list(6).asInstanceOf[T7], list(7).asInstanceOf[T8], list(8).asInstanceOf[T9]))
  
  def combineLatest[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10](o1: Observable[T1], o2: Observable[T2], o3: Observable[T3], o4: Observable[T4], o5: Observable[T5], o6: Observable[T6], o7: Observable[T7], o8: Observable[T8], o9: Observable[T9], o10: Observable[T10]) =
		  List(o1, o2, o3, o4, o5, o6, o7, o8, o9, o10)
		  .combineLatest
		  .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2], list(2).asInstanceOf[T3], list(3).asInstanceOf[T4], list(4).asInstanceOf[T5], list(5).asInstanceOf[T6], list(6).asInstanceOf[T7], list(7).asInstanceOf[T8], list(8).asInstanceOf[T9], list(9).asInstanceOf[T10]))

  def combineLatest[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11](o1: Observable[T1], o2: Observable[T2], o3: Observable[T3], o4: Observable[T4], o5: Observable[T5], o6: Observable[T6], o7: Observable[T7], o8: Observable[T8], o9: Observable[T9], o10: Observable[T10], o11: Observable[T11]) =
    List(o1, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11)
      .combineLatest
      .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2], list(2).asInstanceOf[T3], list(3).asInstanceOf[T4], list(4).asInstanceOf[T5], list(5).asInstanceOf[T6], list(6).asInstanceOf[T7], list(7).asInstanceOf[T8], list(8).asInstanceOf[T9], list(9).asInstanceOf[T10], list(10).asInstanceOf[T11]))

  def combineLatest[T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12](o1: Observable[T1], o2: Observable[T2], o3: Observable[T3], o4: Observable[T4], o5: Observable[T5], o6: Observable[T6], o7: Observable[T7], o8: Observable[T8], o9: Observable[T9], o10: Observable[T10], o11: Observable[T11], o12: Observable[T12]) =
    List(o1, o2, o3, o4, o5, o6, o7, o8, o9, o10, o11, o12)
      .combineLatest
      .map(list => (list(0).asInstanceOf[T1], list(1).asInstanceOf[T2], list(2).asInstanceOf[T3], list(3).asInstanceOf[T4], list(4).asInstanceOf[T5], list(5).asInstanceOf[T6], list(6).asInstanceOf[T7], list(7).asInstanceOf[T8], list(8).asInstanceOf[T9], list(9).asInstanceOf[T10], list(10).asInstanceOf[T11], list(11).asInstanceOf[T12]))

}