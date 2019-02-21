package eu.eyan.util.progress

import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import eu.eyan.log.Log

object Progress {
  def percent() = new Progress(0, Some(100))
  def counter() = new Progress(0, None)
}

case class ProgressUpdate(val state: ProgressState, val counter: Long, val max: Option[Long]) {
  def notStarted = state == NotStarted
  def ended = state == Ended
}

trait ProgressState
case object NotStarted extends ProgressState
case object InProgress extends ProgressState
case object Ended extends ProgressState

class Progress(val counter: Long, val max: Option[Long]) {
  
  def addSubTask = {
    val newProgress = new Progress(0, max)
  }
  def progress: Observable[ProgressUpdate] = progressSubject.distinctUntilChanged
  def set(value: Long) = {
    Log.info(value)
    progressSubject.onNext(ProgressUpdate(InProgress, value, max))
  }

  def stop = progressSubject.onNext(ProgressUpdate(Ended, progressSubject.get[ProgressUpdate].counter, max)) 

  private val progressSubject = BehaviorSubject[ProgressUpdate](ProgressUpdate(NotStarted, 0, max))
}

class Percent {}