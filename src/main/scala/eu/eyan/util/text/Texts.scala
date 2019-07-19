package eu.eyan.util.text

import rx.lang.scala.subjects.BehaviorSubject
import eu.eyan.log.Log
import rx.lang.scala.Observable

abstract class Texts() {
  def getLanguages: Array[String]
	def hasNoInitialLanguage = initialLanguage.isEmpty
	def onLanguageSelected(selectedLanguage: String) = {
			Log.info(selectedLanguage)
			language.onNext(Some(selectedLanguage))
			this
	}
	
  protected def initialLanguage: Option[String]
  protected def getTextTranslation(technicalName: String, language: Option[String]): Option[String]

  protected val language = BehaviorSubject[Option[String]](initialLanguage)

  protected def noTranslation(technicalName: String) = s"**$technicalName**"
  protected def translate(technicalName: String)(language: Option[String]) = getTextTranslation(technicalName, language) // this.getClass.getSimpleName.replace("$", "")
}

class TextsDialogYes(val text: Text, val title: Text, val yes: Text)
class TextsDialogYesNo(val text: Text, val title: Text, val yes: Text, val no: Text)
class TextsDialogYesNoCancel(val text: Text, val title: Text, val yes: Text, val no: Text, val cancel: Text)
class TextsDialogFileChooser(val title: Text, val approve: Text, val cancel: Text, val fileFilterText: Text)
class TextsButton(val text: Text, val tooltip: Text, val icon: Text)