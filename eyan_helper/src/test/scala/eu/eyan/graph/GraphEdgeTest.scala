package eu.eyan.graph

import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.assertt.AssertPlus._
import eu.eyan.graph.impl.GraphEdgeUndirected
import eu.eyan.graph.impl.GraphImplSimple
import eu.eyan.graph.impl.GraphEdgeDirected
import eu.eyan.graph.impl.GraphEdgeSimple

class GraphEdgeTest {

  @Test def testVertices = {
    // vertices - vertices
    val ab = GraphEdgeUndirected("A", "B")
    ab.vertices shouldBe nonEmpty
    ab.vertices shouldHaveSize 2
    ab.vertices shouldContain "A"
    ab.vertices shouldContain "B"

    // equals
    ab shouldNotBeEqualTo GraphEdgeUndirected("C", "A")
    ab shouldNotBeEqualTo "A"

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

  @Test def testDirection = {
    // direction - vertices
    GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B") shouldBeEqualTo GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B")
    GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B") shouldBeEqualTo GraphEdgeSimple(GraphEdgeDirectionUndirected, "B", "A")

    GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B") shouldBeEqualTo GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B")
    GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B") shouldNotBeEqualTo GraphEdgeSimple(GraphEdgeDirectionDirected, "B", "A")

    // direction - direction
    GraphEdgeSimple(GraphEdgeDirectionDirected).direction shouldBeEqualTo GraphEdgeDirectionDirected
    GraphEdgeSimple(GraphEdgeDirectionUndirected).direction shouldBeEqualTo GraphEdgeDirectionUndirected

    // direction - toString
    GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B").toString shouldBeEqualTo "A-B"
    GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B").toString shouldBeEqualTo "A->B"
  }

  @Test def testUndirected = {
    // direction - undirected
    GraphEdgeSimple(GraphEdgeDirectionUndirected).undirected shouldBeTrue

    GraphEdgeSimple(GraphEdgeDirectionDirected).undirected shouldBeFalse
  }

  @Test def testDirected = {
    // direction - undirected
    GraphEdgeSimple(GraphEdgeDirectionUndirected).directed shouldBeFalse

    GraphEdgeSimple(GraphEdgeDirectionDirected).directed shouldBeTrue
  }

  @Test def testPredecessor = {
    //predecessor - vertices
    GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B").predecessor shouldBeEqualTo "A"
    GraphEdgeSimple(GraphEdgeDirectionDirected, "B", "A").predecessor shouldBeEqualTo "B"

    //predecessor - direction
    expectException(classOf[GraphUndirectedEdgeNotSupported], GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B").predecessor)
  }

  @Test def testSuccessor = {
    //successor - vertices
    GraphEdgeSimple(GraphEdgeDirectionDirected, "A", "B").successor shouldBeEqualTo "B"
    GraphEdgeSimple(GraphEdgeDirectionDirected, "B", "A").successor shouldBeEqualTo "A"

    //successor - direction
    expectException(classOf[GraphUndirectedEdgeNotSupported], GraphEdgeSimple(GraphEdgeDirectionUndirected, "A", "B").successor)
  }

}