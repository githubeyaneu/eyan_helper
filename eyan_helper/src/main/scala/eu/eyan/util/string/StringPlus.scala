package eu.eyan.util.string

import java.text.Normalizer

object StringPlus {
  lazy val reg = "[\\p{InCombiningDiacriticalMarks}]".r
  
  def withoutAccents(s:String) = reg.replaceAllIn(Normalizer.normalize(s, Normalizer.Form.NFD), "").replaceAll("ß", "ss")
}