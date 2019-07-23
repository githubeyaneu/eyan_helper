package eu.eyan.karnaugh

import eu.eyan.testutil.assertt.AssertPlus

import scala.collection.mutable.HashMap
import eu.eyan.karnaugh.Karnaugh.crossJoin

import scala.collection.mutable

object Karnaugh extends App {
  /*
   *  A B ->  &  |  ^  A  B !A !B
   *  0 0 ->  0  0  0  0  0  1  1
   *  0 1 ->  0  1  1  0  1  1  0
   *  1 0 ->  0  1  1  1  0  0  1
   *  1 1 ->  1  1  0  1  1  0  0
   *
   *
   *
    A 0 0 1 1
    B 0 1 0 1

      0 0 0 0  ->  0  (contradiction)
      0 0 0 1  ->  &  (conjuction)
      0 0 1 0  ->     (material nonimplication) -  A & !B
      0 0 1 1  ->  A  (proposition A)
      0 1 0 0  ->     (converse nonimplication) - !A & B
      0 1 0 1  ->  B  (proposition B)
      0 1 1 0  ->  ^  (exclusive disjunction) - XOR - (A | B) & !(A & B) - (A & !B) | (!A & B)
      0 1 1 1  ->  |  (disjunction)
      1 0 0 0  ->     (joint denial) - NOR - !(A|B) - !A & !B
      1 0 0 1  ->     (biconditional) - NXOR - ? - (!A & !B) | (A & B)
      1 0 1 0  ->  !B (negation B)
      1 0 1 1  ->     (converse implication) - A | !B
      1 1 0 0  ->  !A (negation A)
      1 1 0 1  ->     (material implication) - !A | B
      1 1 1 0  ->     (alternative denial) - NAND - !(A&B) - !A | !B
      1 1 1 1  ->  1  (tautology)
   */

  def crossJoin[T](list: Set[Set[T]]): Set[Set[T]] =
    list.toList match {
      case set :: Nil          => set map (Set(_))
      case headSet :: tailSets => for { i <- headSet; j <- crossJoin(tailSets.toSet) } yield j + i
      case Nil                 => Set(Set())
    }
}

case class KarnaughCellsMissing() extends Exception

abstract class KarVar(name: String, negate: Boolean)
case class Var(name: String) extends KarVar(name, false) { override def toString = name }
case class VarNot(name: String) extends KarVar(name, true) { override def toString = "!" + name }

abstract class Operator1(name: String, operator: Boolean => Boolean)
case class False() extends Operator1("false", a => false)
case class True() extends Operator1("true", a => true)
case class Same() extends Operator1("", a => a)
case class Negate() extends Operator1("not", a => !a)

abstract class Operator2(name: String, operator: (Boolean, Boolean) => Boolean)
case class AND() extends Operator2("and", (a, b) => a && b)
case class OR() extends Operator2("or", (a, b) => a || b)

class Karnaugh(variables: Set[String]) {
  private val cells = mutable.HashMap[Set[_ <: KarVar], Boolean]()
  def addCell(variables: Set[_ <: KarVar], value: Boolean) = { cells += (variables -> value); this }

  private def variableDoubles = variables.map(variable => Set[KarVar](Var(variable), VarNot(variable)))
  def solve: String = {
    if (crossJoin(variableDoubles) != cells.keySet) throw KarnaughCellsMissing()
    ""
  }

}
