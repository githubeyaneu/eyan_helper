package eu.eyan.util.text

import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.log.Log
import rx.lang.scala.Observable

abstract class Texts() {
  def initialLanguage: Option[String]
  protected def getTextTranslation(technicalName: String, language: Option[String]): Option[String]

  protected val language = BehaviorSubject[Option[String]](initialLanguage)

  def onLanguageSelected(selectedLanguage: String) = {
    Log.info(selectedLanguage)
    language.onNext(Some(selectedLanguage))
    this
  }

  protected def noTranslation(technicalName: String) = s"**$technicalName**"
  protected def translate(technicalName: String)(language: Option[String]) = getTextTranslation(technicalName, language) // this.getClass.getSimpleName.replace("$", "")
}