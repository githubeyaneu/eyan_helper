package eu.eyan.util.rx.lang.scala

import rx.lang.scala.Observer

object ObserverPlus {
  implicit class ObserverImplicit[T](observer: Observer[T]) {
    def pam[T2](mapper: => T): Observer[T2] = mapBack(mapper)
    def mapBack[T2](mapper: => T): Observer[T2] =
      Observer.apply[T2](
        (t2: T2) => observer.onNext(mapper),
        throwable => observer.onError(throwable),
        () => observer.onCompleted())

    def pam[T2](mapper: T2 => T): Observer[T2] = mapBack(mapper)
    def mapBack[T2](mapper: T2 => T): Observer[T2] =
      Observer.apply[T2](
        (t2: T2) => observer.onNext(mapper(t2)),
        throwable => observer.onError(throwable),
        () => observer.onCompleted())
  }
}