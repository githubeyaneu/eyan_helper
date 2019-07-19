package eu.eyan.util.config

import eu.eyan.util.string.StringPlus.StringPlusImplicit

object Config {
  def apply(path: String) = path.linesFromFile.toList.filter(hasEquals).map(toConfigEntry).toMap
  
  def hasEquals(s:String) = s.contains("=")
  def toConfigEntry(s:String) = {val first = s.indexOf("="); (s.substring(0, first), s.substring(first+1))}
}