package eu.eyan.util.string

import org.junit.Test
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner
import org.fest.assertions.Assertions._
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.junit.JUnitSuite
import eu.eyan.testutil.ScalaEclipseJunitRunner
import StringsSearchTree.newTree
import eu.eyan.util.random.RandomPlus
import org.fest.assertions.Assertions

@RunWith(classOf[ScalaEclipseJunitRunner])
class StringsSearchTreeTest extends JUnitSuite {

  @Test def empty = assertThat(newTree[Int].get("").isEmpty).isTrue

  @Test def emptyString1_get_Empty_returns_1 = assertThat(newTree[Int].add("",1).get("")).isEqualTo(Set(1))
  
  @Test def emptyString12_get_Empty_returns_12 = assertThat(newTree[Int].add("",1).add("",2).get("")).isEqualTo(Set(1,2))
  
  @Test def emptyString112_get_Empty_returns_12 = assertThat(newTree[Int].add("",1).add("",2).add("",1).get("")).isEqualTo(Set(1,2))

  @Test def a1_get_Empty_returns_Empty = assertThat(newTree[Int].add("a",1).get("")).isEqualTo(Set())

  @Test def a1_get_a_returns_1 = assertThat(newTree[Int].add("a",1).get("a")).isEqualTo(Set(1))

  @Test def a12_get_a_returns_12 = assertThat(newTree[Int].add("a",1).add("a",2).get("a")).isEqualTo(Set(1,2))
  
  @Test def a12_b1_get_a_returns_12 = assertThat(newTree[Int].add("a",1).add("a",2).add("b",1).get("a")).isEqualTo(Set(1,2))

  @Test def a12_b1_get_b_returns_1 = assertThat(newTree[Int].add("a",1).add("a",2).add("b",1).get("b")).isEqualTo(Set(1))
  
  @Test def ab1_get_b_returns_Empty = assertThat(newTree[Int].add("ab",1).get("b")).isEqualTo(Set())
  
  @Test def ab1_get_ab_returns_1 = assertThat(newTree[Int].add("ab",1).get("ab")).isEqualTo(Set(1))
  
  @Test def ab1_ac2_get_ab_returns_1 = assertThat(newTree[Int].add("ab",1).add("ac",1).get("ab")).isEqualTo(Set(1))
  
  @Test def ab1_ac2_get_ac_returns_2 = assertThat(newTree[Int].add("ab",1).add("ac",2).get("ac")).isEqualTo(Set(2))

  @Test def addAllSubstrings_abc1_bc_2_a_returns_1 = assertThat(newTree[Int].addAll("abc",1).addAll("bc",2).get("a")).isEqualTo(Set(1))

  @Test def addAllSubstrings_Empty = assertThat(newTree[Int].addAll("",1).get("")).isEqualTo(Set(1))

  @Test def printer = assertThat(newTree[Int].add("ab",1).add("ab",2).toString).isEqualTo(": \r\na: \r\nab: 1, 2")
  
  @Test def printer2 = println(newTree[Int].addAll("abc",1).addAll("bc",2))
  
  @Test def tree5000 = {
    val rnd = new RandomPlus(13)
    val values = rnd.nextReadableStrings(5000, 0, 10) zip rnd.nextInts(5000, 1000)
    
    val start = System.currentTimeMillis()
    val tree = values.foldLeft(newTree[Int])((tree, value)=>tree.add(value._1, value._2))
    val end = System.currentTimeMillis()
    
    assertThat(end-start).isLessThan(300)
    println(end-start)
  }
}