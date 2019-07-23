package eu.eyan.util.swing

import java.text.Normalizer

import eu.eyan.util.awt.AwtHelper.newRunnable
import eu.eyan.util.string.StringPlus.s1_containsSearch_s2_doesNot
import eu.eyan.util.string.StringPlus.s1_startsWithSearch_s2_doesNot
import eu.eyan.util.string.StringPlus.s2_startsWithSearch_s1_doesNot

class AutocompleteHints {
  val reg = "[\\p{InCombiningDiacriticalMarks}]".r
  private val normalizedautocompleteValues = new java.util.HashMap[String, String]()

  private var autocompleteValues = List[String]()
  def getAutocompleteValues = autocompleteValues
  def setAutocompleteValues(values: List[String]) = {
    autocompleteValues = values.filter { _ != null }
    new Thread(newRunnable { () => values.foreach { _.normalized } }).start()
  }

  implicit class AutocompleteHintsStringPlus(val s: String) {
    def normalized = {
      def normalize(text: String) = reg.replaceAllIn(Normalizer.normalize(text, Normalizer.Form.NFD), "").replaceAll("ß", "s").toLowerCase

      if (s == null) null
      else normalizedautocompleteValues.synchronized {
        if (!normalizedautocompleteValues.containsKey(s)) normalizedautocompleteValues.put(s, normalize(s))
        normalizedautocompleteValues.get(s)
      }
    }
  }

  def sortAlgo(searchString: String) = (s1: String, s2: String) => {

    val search_lc = searchString.toLowerCase
    lazy val s1_lc = s1.toLowerCase
    lazy val s2_lc = s2.toLowerCase

    if (s1_startsWithSearch_s2_doesNot(s1_lc, s2_lc, search_lc)) true
    else if (s2_startsWithSearch_s1_doesNot(s1_lc, s2_lc, search_lc)) false
    else if (s1_startsWithSearch_s2_doesNot(s1_lc.normalized, s2_lc.normalized, search_lc.normalized)) true
    else if (s2_startsWithSearch_s1_doesNot(s1_lc.normalized, s2_lc.normalized, search_lc.normalized)) false
    else s1_containsSearch_s2_doesNot(s1_lc, s2_lc, search_lc)
  }

  def findElementsToShow(searchString: String) = {
    val searchNormalized = searchString.normalized
    autocompleteValues
      .filter("".ne(_))
      .distinct
      .filter(_.normalized.contains(searchNormalized))
      .sortWith(sortAlgo(searchString))
  }
}