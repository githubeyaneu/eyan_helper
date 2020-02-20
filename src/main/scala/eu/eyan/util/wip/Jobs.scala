package eu.eyan.util.wip

import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.log.Log

object Jobs {
  def apply() = new Jobs
}
class Jobs {
  private var counter = 0
  def run(work: => Unit) = createAndExecuteJob(work)
  def workInProgress = jobs.map(_.nonEmpty).distinctUntilChanged

  private val jobs = BehaviorSubject[List[Job]](List())
  jobs.subscribe(list => Log.info(list))

  private case class Job(id: Int)

  private def createAndExecuteJob(work: => Unit) = {
    counter += 1
    val job = new Job(counter)
    jobs.synchronized { jobs.first.map(_.+:(job)).subscribe(jobs.onNext(_)) }
    Log.info("job start", job)
    work
    Log.info("job done ", job)
    jobs.synchronized { jobs.first.map(_.filter(_ != job)).subscribe(jobs.onNext(_)) }
    this
  }
}