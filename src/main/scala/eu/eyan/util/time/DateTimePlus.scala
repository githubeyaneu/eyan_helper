package eu.eyan.util.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object DateTimePlus {
  implicit class InstantPlusImplicit(instant: Instant) {
    def inZone(zoneId: ZoneId) = ZonedDateTime.ofInstant(instant, zoneId)
  }
}