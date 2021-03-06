package eu.eyan.util.random

import scala.util.Random
import java.text.Normalizer

object RandomPlus {
  val chars = "aábcdeéfghiíjklmnoóöőpqrstuúüűvwxyzAÁBCDEÉFGHIÍJKLMNOÓÖŐPQRSTUÚÜŰVWXYZß     "

  def main(args: Array[String]): Unit = {
    val reg = "[\\p{InCombiningDiacriticalMarks}]".r
    def norm(c: Char) = reg.replaceAllIn(Normalizer.normalize(c + "", Normalizer.Form.NFD), "")
    for {i <- 0x0 to 0xFFFF} {
      if (norm(i.toChar).length() > 0 && i != norm(i.toChar).charAt(0).toInt)
        println((i, i.toChar + "", norm(i.toChar), norm(i.toChar).charAt(0).toInt))
    }
    println('ß'.toInt)
  }
}

class RandomPlus(salt: Int) extends Random(salt) {
  def randomChar = RandomPlus.chars(nextInt(RandomPlus.chars.length() - 1))

  val DEFAUL_RANDOM_STRING_LENGTH = 20
  def randomReadableString(lengthFrom: Int = 1, lengthTo: Int = DEFAUL_RANDOM_STRING_LENGTH) =
    (for {a <- 1 to lengthFrom + nextInt(lengthTo)} yield randomChar).mkString

  def nextReadableStrings(number: Int, lengthFrom: Int, lengthTo: Int) =
    for {i <- 1 to number} yield randomReadableString(lengthFrom, lengthTo)

  def nextInts(number: Int, n: Int) = for {i <- 1 to number} yield nextInt(n)
}