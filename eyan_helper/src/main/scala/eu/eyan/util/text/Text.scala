package eu.eyan.util.text

import eu.eyan.log.Log
import eu.eyan.util.scala.Try
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.util.rx.lang.scala.subjects.BehaviorSubjectPlus.BehaviorSubjectImplicit

object Text {
  def emptySingularPlural(nr: Observable[Int], emptyText: Text, singularText: Text, pluralText: Text): Observable[String] = {
    val textsCombined = Observable.combineLatest(Array(emptyText, singularText, pluralText).toIterable)(_.toList)

    val nrAndTexts = nr combineLatest textsCombined

    def selectTitleText(nrAndTexts: (Int, List[String])) = {
      val nr = nrAndTexts._1
      val texts = nrAndTexts._2
      val idx = if (nr < 2) nr else 2
      texts(idx)
    }
    nrAndTexts map selectTitleText
  }

  def combineAndExecute[T](texts: (String, Text)*)(action: Map[String, String] => T) = {
    val textsList = texts.toList.unzip
    val names = textsList._1
    val observables = textsList._2
    val textsCombined = Observable.combineLatest(observables.toIterable)(_.toList)
    var result: T = null.asInstanceOf[T]
    textsCombined.take(1).subscribe(list => result = action(names.zip(list).toMap))
    result
  }
}
/**
 * An observable text that can be formatted with the also observable parameters
 *  The text translation should not happen here, the template should be updated in this case.
 */
class Text(protected val template: String, private val args: Observable[Any]*) extends Observable[String] {
  protected val templateObservable = BehaviorSubject[String](template)
  templateObservable.take(1).subscribe(string => Log.info("Text created " + string))
  Log.info("Text created args:" + args.size)

  // TODO check string and param numbers
  lazy val paramsCombined = Observable.combineLatest(args.toIterable)(_.toList)

  def countOccurrences(src: String, tgt: String): Int = src.sliding(tgt.length).count(window => window == tgt)

  def noMoreParams(template: String) = {
    val ok = countOccurrences(template, "%s") <= args.size
    if (!ok) Log.error(s"Wrong template. Template contains more params as given params. Template=$template, params=${args.size}")
    ok
  }

  lazy val onlyValidTemplates = templateObservable filter noMoreParams

  lazy val templateAndParams = onlyValidTemplates combineLatest paramsCombined

  lazy val textFormatter = templateAndParams map formatTextWithParams

  lazy val formattedText = if (args.nonEmpty) textFormatter else templateObservable

  lazy val text = BehaviorSubject("")
  formattedText subscribe text

  lazy val asJavaObservable: rx.Observable[_ <: String] = text.asJavaObservable

  def get = text.get[String]

  private def formatTextWithParams(templateAndParams: (String, List[Any])) = {
    val template = templateAndParams._1
    val params = templateAndParams._2
    Log.debug(s"Format text. Template=$template, params=${params.mkString}")
    val text = Try(String.format(template, params.map(_.asInstanceOf[Object]): _*)).getOrElse("")
    Log.debug(s"Formatted text=$text")
    text
  }
  

}