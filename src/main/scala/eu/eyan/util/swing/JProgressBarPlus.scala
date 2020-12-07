package eu.eyan.util.swing

import java.util.concurrent.TimeUnit

import eu.eyan.log.Log
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.swing.JProgressBarPlus.ResetProgress
import eu.eyan.util.swing.SwingPlus.invokeLater
import javax.swing.JProgressBar
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

import scala.concurrent.duration.{Duration, FiniteDuration}

object JProgressBarPlus {

  implicit class JProgressBarImplicit[TYPE <: JProgressBar](jProgressBar: TYPE) extends JComponentImplicit(jProgressBar) {
    //    addChangeListener(ChangeListener)
    //    removeChangeListener(ChangeListener)
    //    setBorderPainted(boolean)
    //    setIndeterminate(boolean)
    //    setMaximum(int)
    //    setMinimum(int)
    //    setModel(BoundedRangeModel)
    //    setOrientation(int)
    //    setString(String)
    //    setStringPainted(boolean)
    //    setUI(ProgressBarUI)
    //    setValue(int)
    //    updateUI()
  }

  type ResetProgress = (Observable[String],  Int,  Observable[Int])=>Unit
}


class JProgressBarPlus(private val min: Int = 0, private val max: Int = 100, private val initialFormat: String = "%d%%", private val finishedText: String = "Ready") extends JProgressBar(min, max) {
  private val lock = new Object
  private val REFRESH_RATE: FiniteDuration = Duration(1000 / 30, TimeUnit.MILLISECONDS)

  private val format$$ = BehaviorSubject(initialFormat)
  private val value$$ = BehaviorSubject(min)
  private val formatAndValue$ = format$$.distinctUntilChanged.combineLatest(value$$.distinctUntilChanged)

  private val format$$$ = BehaviorSubject[Observable[String]]()
  private val value$$$ = BehaviorSubject[Observable[Int]]()


  format$$$.switchMap(x=> x).subscribe(onNext = newFormat => lock.synchronized { format$$.onNext(newFormat) }, onError = Log.error, onCompleted = () => {})
  value$$$.switchMap(x=> x).subscribe(onNext = newValue => lock.synchronized { value$$.onNext(newValue) }, onError = Log.error, onCompleted = () => {})

  formatAndValue$.sample(REFRESH_RATE).subscribe(onNext = updateFormatAndValue, onError = t=> t.printStackTrace(), onCompleted = () => {})

  private def updateFormatAndValue(formatAndValue: (String, Int)) = {
    lock.synchronized {
      invokeLater {
        setVisible(true)
        setString(String.format(formatAndValue._1, formatAndValue._2:Integer))
        setValue(formatAndValue._2)
      }
    }
  }

  // refactor it.
  def doneThenInvisible = {
    finished; invokeLater(setVisible(false)); this
  }

  def finished = {
    invokeLater({
      this.setString(finishedText); this.setValue(this.getMaximum)
    }); this
  }

  @deprecated("use setValue$ instead", "2020-06-21")
  def valueChanged(newValue: Int) = {
    lock.synchronized {
      value$$.onNext(newValue)
    }
    this
  }

  @deprecated("use setFormat$ instead", "2020-06-21")
  def setFormat(newFormat: String) = {
    lock.synchronized {
      format$$.onNext(newFormat)
    }
    this
  }

  def setValue$(value$: Observable[Int]) = {
    lock.synchronized{
      value$$$.onNext(value$.sample(REFRESH_RATE))
    }
    this
  }

  def setFormat$(format$: Observable[String]) = {
    lock.synchronized{
      format$$$.onNext(format$.sample(REFRESH_RATE))
    }
    this
  }
}