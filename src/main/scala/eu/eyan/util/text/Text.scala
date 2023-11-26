package eu.eyan.util.text

import eu.eyan.log.Log
import eu.eyan.util.scala.Try
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit
import eu.eyan.util.rx.lang.scala.ObservablePlus
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableVarargsImplicit
import eu.eyan.util.rx.lang.scala.ObservablePlus.ObservableListImplicit
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object Text {
  def combineMoreTextsWithNamesAndExecute[T](texts: (String, Text)*)(action: Map[String, String] => T) = {
    val textsList = texts.toList.unzip
    val names = textsList._1
    val observables = textsList._2
    val textsCombined = observables.combineLatest
    var result: T = null.asInstanceOf[T]
    textsCombined.take(1).subscribe(list => result = action(names.zip(list).toMap))
    result
  }
}
/**
 * An observable text that can be formatted with the also observable parameters
 * The text translation should not happen here, the template should be updated in this case.
 */
class Text(private val initialTemplate: String, private val args: Observable[Any]*) extends Observable[String] {
	def get = text.take1Synchronous[String]
	
	def setTemplate(nextTemplate: String) = templateSubject onNext nextTemplate
	
  private  val templateSubject = BehaviorSubject[String](initialTemplate)
  Log.debug("Text created " + templateSubject.take1Synchronous)
  Log.debug("Text created args:" + args.size)
  
  private lazy val paramsCombined = args.toList.combineLatest
  
  private lazy val templateObservable = templateSubject.distinctUntilChanged

  private lazy val onlyValidTemplates = templateObservable filter templateHasLessParamsAsArgsGiven

  private lazy val templateAndParams = onlyValidTemplates combineLatest paramsCombined

  private lazy val textWithParamsApplied = templateAndParams map formatTextWithParams

  private lazy val formattedText = if (args.nonEmpty) textWithParamsApplied else templateObservable

  // This three line is neccesary to be an Observable...
  private lazy val text = BehaviorSubject("")
  formattedText.distinctUntilChanged subscribe text
  lazy val asJavaObservable: rx.Observable[_ <: String] = text.asJavaObservable
  
  private def templateHasLessParamsAsArgsGiven(template: String) = {
    val ok = template.countOccurrences("%s") <= args.size
    if (!ok) Log.error(s"Wrong template. Template contains more params as given params. Template=$template, params=${args.size}")
    ok
  }

  private def formatTextWithParams(templateAndParams: (String, List[Any])) = templateAndParams match {
    case (template, params) =>
      Log.debug(s"Format text. Template=$template, params=${params.mkString}")
      val text = Try(String.format(template, params.map(_.asInstanceOf[Object]): _*)).getOrElse("")
      Log.debug(s"Formatted text=$text")
      text
  }
}