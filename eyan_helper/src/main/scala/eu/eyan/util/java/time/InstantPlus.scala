package eu.eyan.util.java.time

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId

object InstantPlus {
  implicit class InstantImplicit[TYPE <: Instant](instant: TYPE) {
    def toString(pattern: String, locale: Locale = Locale.GERMANY, zone: ZoneId = ZoneId.systemDefault) = {
      val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale).withZone(zone)
      formatter.format(instant)
    }
  }
}