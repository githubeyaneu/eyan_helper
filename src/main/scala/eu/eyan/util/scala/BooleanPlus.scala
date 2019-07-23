package eu.eyan.util.scala

object BooleanPlus {
  implicit class BooleanImplicit[B <: Boolean](bool: => B) {
    def ?[T](trueCase: => T) = new Ternary(bool, trueCase)
  }

  class Ternary[B <: Boolean, T](condition: => B, trueCase: => T) {
    def |(falseCase: => T) = if (condition) trueCase else falseCase
  }

  def main(args: Array[String]): Unit = {
    (1 < 2) ? "a"
    (1 > 2) ? "b"

    println((1 > 2) ? "b")
    println((1 > 2) ? "b" | "c")
  }
}