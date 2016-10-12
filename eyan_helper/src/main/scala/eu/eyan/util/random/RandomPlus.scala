package eu.eyan.util.random

import scala.util.Random

object RandomPlus {
  val chars = "aábcdeéfghiíjklmnoóöőpqrstuúüűvwxyzAÁBCDEÉFGHIÍJKLMNOÓÖŐPQRSTUÚÜŰVWXYZ     "
}

class RandomPlus(salt: Int) extends Random(salt) {
  def randomChar = RandomPlus.chars(nextInt(RandomPlus.chars.length() - 1))

  def randomReadableString(lengthFrom: Int = 1, lengthTo: Int = 20) = (for (a <- 1 to lengthFrom + nextInt(lengthTo)) yield randomChar).mkString

  def nextReadableStrings(number: Int, lengthFrom: Int, lengthTo: Int) =
    for (i <- 1 to number) yield randomReadableString(lengthFrom, lengthTo)

  def nextInts(number: Int, n: Int) = for (i <- 1 to number) yield nextInt(n) 
}