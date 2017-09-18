package eu.eyan.graph

import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.assertt.AssertPlus._

class GraphSimpleTest {

	@Test def graphUndirectedSetsTest = {
		val emptyGraph =GraphUndirectedSets[String]() 	
	  graphTest(emptyGraph)
			
	  val a = emptyGraph.addNode("A")
    val ab = a.addNode("B")
	   
    val empty_AB = emptyGraph.addEdge("A", "B")
    empty_AB.nodes shouldBe nonEmpty
    empty_AB.nodes shouldHaveSize 2
    empty_AB.nodes shouldContain "A"
    empty_AB.nodes shouldContain "B"
    empty_AB.edges shouldBe nonEmpty
    empty_AB.edges shouldHaveSize 1
    empty_AB.edges shouldContain GraphEdgeUndirected("A", "B")
    empty_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    empty_AB sholdBeNotEqualTo a

    val a_AB = a.addEdge("A", "B")
    a_AB.nodes shouldBe nonEmpty
    a_AB.nodes shouldHaveSize 2
    a_AB.nodes shouldContain "A"
    a_AB.nodes shouldContain "B"
    a_AB.edges shouldBe nonEmpty
    a_AB.edges shouldHaveSize 1
    a_AB.edges shouldContain GraphEdgeUndirected("A", "B")
    a_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    a_AB sholdBeNotEqualTo ab

    val ab_AB = ab.addEdge("A", "B")
    ab_AB sholdBeEqualTo ab_AB

    val ab_AB_BA = ab_AB.addEdge("B", "A")
    ab_AB_BA sholdBeEqualTo ab_AB
	}

	@Test def graphDirectedSetsTest = {
			graphTest(GraphDirectedSets[String]())
	}
	
  def graphTest(emptyGraph: Graph[String]) = {
    emptyGraph.nodes shouldBe empty
    emptyGraph.edges shouldBe empty

    val a = emptyGraph.addNode("A")
    a.nodes shouldHaveSize 1
    a.nodes shouldBe nonEmpty
    a.nodes shouldContain "A"
    a.edges shouldBe empty

    val ab = a.addNode("B")
    ab.nodes shouldBe nonEmpty
    ab.nodes shouldHaveSize 2
    ab.nodes shouldContain "A"
    ab.nodes shouldContain "B"
    ab.edges shouldBe empty

    val aab = ab.addNode("A")
    aab.nodes shouldBe nonEmpty
    aab.nodes shouldHaveSize 2
    aab.nodes shouldContain "A"
    aab.nodes shouldContain "B"
    aab.edges shouldBe empty
    aab sholdBeEqualTo ab
  }

  @Test
  def EdgeSimpleTest = {
    val AB = GraphEdgeUndirected("A", "B")
    AB sholdBeNotEqualTo GraphEdgeUndirected("C", "A")
    AB sholdBeNotEqualTo "A"
    AB.toString sholdBeEqualTo "A-B"
    AB.canEqual(AB).shouldBeTrue
    AB.canEqual(1).shouldBeFalse
    AB.toString sholdBeEqualTo "A-B"

    val l = List(1, 2, 1, -1000, -17, -13, -11, -7, -5, -1, -2, -4, -8, -16, 0, 1000, 17, 13, 11, 7, 5, 1, 2, 4, 8, 16, 30, -31)
    for (combination <- (l ++ l ++ l ++ l).combinations(4)) {
      for (permutation <- combination.permutations) {
        permutation match {
          case List(a, b, c, d) =>
            val ab = GraphEdgeUndirected(a, b)
            val cd = GraphEdgeUndirected(c, d)
            val equals = ab == cd
            val hashEquals = ab.hashCode == cd.hashCode
            if (equals) hashEquals.shouldBeTrue
        }
      }
    }
  }
}