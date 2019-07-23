package eu.eyan.util.regex

import scala.util.matching.Regex

object RegexPlus {
  def toRegexGroups(regex: Regex)(line: String) = regex.findAllIn(line).matchData.flatMap(_.subgroups)

  implicit class RegexPlusImplicit(val regex: Regex) {
    def toRegexGroups = RegexPlus.toRegexGroups(regex)(_)
  }
}