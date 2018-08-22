package eu.eyan.util.text

import eu.eyan.log.Log
import eu.eyan.util.scala.Try
import rx.lang.scala.Observable
import rx.lang.scala.subjects.BehaviorSubject

/** An observable text that can be formatted with the also observable parameters 
 *  The text translation should not happen here, the template should be updated in this case.*/
abstract class Text(private val template: BehaviorSubject[String], private val args: Observable[Any]*) extends Observable[String] {

  template.take(1).subscribe(string => Log.info("Text created "+ string))
  
  if (args.nonEmpty) {
	  Log.info("Text created args:"+args.size)

    // TODO check string and param numbers
    def toText(params: List[Any]) = Try(String.format(get, params.map(_.asInstanceOf[Object]): _*)).getOrElse("")
    val observablesCombined = Observable.combineLatest(args.toIterable)(_.toList).map(toText)
    observablesCombined.subscribe(template)
  }
  val asJavaObservable: rx.Observable[_ <: String] = template.asJavaObservable

  def get:String
}