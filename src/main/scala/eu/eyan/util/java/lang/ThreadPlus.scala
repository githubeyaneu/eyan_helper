package eu.eyan.util.java.lang

import java.util.concurrent.Executors

import eu.eyan.log.Log
import eu.eyan.util.scala.TryCatch
import rx.lang.scala.Observable

object ThreadPlus {

  def run[T](action: => T) = new ThreadRunner(action)

  //  def runSafe[T](action: => T) = new ThreadRunnerSafe(action).result.distinctUntilChanged
  //  class ThreadRunnerSafe[T](action: => T) {
  //    val result = PublishSubject[T]()
  //    val thread = new Thread(RunnablePlus.runnable(TryCatch({ result.onNext(action); result.onCompleted }, result.onError _))).start
  //  }

  def runObservable[T](action: => T) = Observable[T](emitter => {
    new Thread(RunnablePlus.runnable(TryCatch({
      emitter.onNext(action); emitter.onCompleted
    }, emitter.onError _))).start()
  })

  def runBlockingWithTimeout[T](ms: Long, action: => T, cancel: => Unit) = {
    def now = System.currentTimeMillis
    val threadAction = ThreadPlus.run(action)
    def waitForDone(plusCond: => Boolean = true) = while (!threadAction.done && plusCond) Thread.sleep(1)
    val start = now
    waitForDone(now < start + ms)
    if (threadAction.done) threadAction.result
    else { cancel; waitForDone(); None }
  }

  lazy val executor = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime.availableProcessors - 2))
}

class ThreadRunner[T](action: => T) {
  var done = false
  private var ret: Option[T] = None

  ThreadPlus.executor.execute(RunnablePlus.runnable({
    ret = try { val ret = Option(action); Log.info("ok"); ret } catch { case e: Throwable => Log.error(e); None}
    done = true
    Log.info("done")
  }))
  def result: Option[T] = ret
}