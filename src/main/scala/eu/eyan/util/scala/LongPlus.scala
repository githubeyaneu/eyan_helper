package eu.eyan.util.scala

object LongPlus {
  implicit class LongImplicit(long: Long) {
    val K = 1000L
    val M = K * K
    val G = K * M
    val T = K * G
    val P = K * T

    def toSize =
      if (long < 10L * K) long + "B"
      else if (long < 10L * M) (long / K) + "KB"
      else if (long < 10L * G) (long / M) + "MB"
      else if (long < 10L * T) (long / G) + "GB"
      else if (long < 10L * P) (long / T) + "TB"
      else (long / P) + "PB"
  }
}