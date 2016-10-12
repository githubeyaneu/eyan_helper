package eu.eyan.util.swing

import scala.collection.mutable.MutableList
import java.text.Normalizer
import eu.eyan.util.awt.AwtHelper.newRunnable

class AutocompleteHints {
  val reg = "[\\p{InCombiningDiacriticalMarks}]".r
  private val normalizedautocompleteValues = new java.util.HashMap[String, String]()

  private var autocompleteValues = List[String]()
  def getAutocompleteValues = autocompleteValues.toList
  def setAutocompleteValues(values: List[String]) = {
    autocompleteValues = values.filter { _ != null }
    new Thread(newRunnable { () => values.foreach { getNormalized(_) } }).start()
  }

  def getNormalized(text: String) = {
    def normalize(text: String) = reg.replaceAllIn(Normalizer.normalize(text, Normalizer.Form.NFD), "").replaceAll("ß", "s").toLowerCase

    if (text == null) null
    else {
      normalizedautocompleteValues.synchronized {
        if (!normalizedautocompleteValues.containsKey(text)) {
          normalizedautocompleteValues.put(text, normalize(text))
        }
        normalizedautocompleteValues.get(text)
      }
    }
  }

  def sortAlgo(searchString: String) = (s1: String, s2: String) => {
    val l1 = s1.toLowerCase
    val l2 = s2.toLowerCase
    val lsearch = searchString.toLowerCase
    val s1Starts = l1.startsWith(lsearch)
    val s2Starts = l2.startsWith(lsearch)
    val s1Contains = l1.contains(lsearch)
    val s2Contains = l2.contains(lsearch)
    val s1NormalizedStarts = getNormalized(l1).startsWith(getNormalized(lsearch))
    val s2NormalizedStarts = getNormalized(l2).startsWith(getNormalized(lsearch))

    if (s1Starts && !s2Starts) true
    else if (s2Starts && !s1Starts) false
    else if (s1NormalizedStarts && !s2NormalizedStarts) true
    else if (s2NormalizedStarts && !s1NormalizedStarts) false
    else if (s1Contains && !s2Contains) true
    else false
  }

  def findElementsToShow(searchString: String) = {
    val searchNormalized = getNormalized(searchString)
    autocompleteValues
      .filter("".ne(_))
      .distinct
      .filter(getNormalized(_).contains(searchNormalized))
      .sortWith(sortAlgo(searchString))
  }
}