package eu.eyan.graph

import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.assertt.AssertPlus._
import eu.eyan.graph.impl.GraphEdgeUndirected
import eu.eyan.graph.impl.GraphImplSimple
import eu.eyan.graph.impl.GraphEdgeDirected

class GraphTest {

  @Test def testVertices() = {
    val emptyGraph = GraphImplSimple[String]()
    emptyGraph.vertices shouldBe empty
  }

  @Test def testEdges() = {
    val emptyGraph = GraphImplSimple[String]()
    emptyGraph.edges shouldBe empty
  }

  @Test def testAddVertex() = {
    val emptyGraph = GraphImplSimple[String]()
    val a = emptyGraph.add("A")
    a.vertices shouldHaveSize 1
    a.vertices shouldBe nonEmpty
    a.vertices shouldContain "A"
    a.edges shouldBe empty

    val ab = a.add("B")
    ab.vertices shouldBe nonEmpty
    ab.vertices shouldHaveSize 2
    ab.vertices shouldContain "A"
    ab.vertices shouldContain "B"
    ab.edges shouldBe empty

    val aab = ab.add("A")
    aab.vertices shouldBe nonEmpty
    aab.vertices shouldHaveSize 2
    aab.vertices shouldContain "A"
    aab.vertices shouldContain "B"
    aab.edges shouldBe empty
  }

  @Test def testAddEdge() = {
    val e = GraphImplSimple[String]()
		val AB = GraphEdgeUndirected("A","B")
    val ab = e.add(AB)
    
    ab.vertices shouldHaveSize 2
    ab.vertices shouldContain "A"
    ab.vertices shouldContain "B"

    ab.edges shouldBe nonEmpty
    ab.edges shouldHaveSize 1
    
    val edge = ab.edges.toList(0)
    edge shouldBeSameInstanceTo AB 
    
    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectNoException(undirected.add(GraphEdgeUndirected("A","B")))
    expectException(classOf[GraphDirectedEdgeNotSupported], undirected.add(GraphEdgeDirected("A","B")))
    
    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectException(classOf[GraphUndirectedEdgeNotSupported], directed.add(GraphEdgeUndirected("A","B")))
    expectNoException(directed.add(GraphEdgeDirected("A","B")))
    
    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.add(GraphEdgeUndirected("A","B")))
    expectNoException(mixed.add(GraphEdgeDirected("A","B")))
  }

  @Test def testAddUndirectedEdge() = {

    val emptyGraph = GraphImplSimple[String]()
    val a = emptyGraph.add("A")
    val ab = a.add("B")

    val empty_AB = emptyGraph.addUndirectedEdge("A", "B")
    empty_AB.vertices shouldBe nonEmpty
    empty_AB.vertices shouldHaveSize 2
    empty_AB.vertices shouldContain "A"
    empty_AB.vertices shouldContain "B"
    empty_AB.edges shouldBe nonEmpty
    empty_AB.edges shouldHaveSize 1
    empty_AB.edges shouldContain GraphEdgeUndirected("A", "B") // FIXME: GraphEdgeUndirected is implementation, use trait functions instead if possible(?)
    empty_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    empty_AB shouldNotBeEqualTo a

    val a_AB = a.addUndirectedEdge("A", "B")
    a_AB.vertices shouldBe nonEmpty
    a_AB.vertices shouldHaveSize 2
    a_AB.vertices shouldContain "A"
    a_AB.vertices shouldContain "B"
    a_AB.edges shouldBe nonEmpty
    a_AB.edges shouldHaveSize 1
    a_AB.edges shouldContain GraphEdgeUndirected("A", "B") // FIXME: GraphEdgeUndirected is implementation, use trait functions instead if possible(?)
    a_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    a_AB shouldNotBeEqualTo ab

    val ab_AB = ab.addUndirectedEdge("A", "B")
    ab_AB shouldBeEqualTo ab_AB

    val ab_AB_BA = ab_AB.addUndirectedEdge("B", "A")
    ab_AB_BA shouldBeEqualTo ab_AB

    val aab = ab.add("A")
    aab shouldBeEqualTo ab
    
    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectNoException(undirected.addUndirectedEdge("A","B"))
    
    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectException(classOf[GraphUndirectedEdgeNotSupported], directed.addUndirectedEdge("A","B"))
    
    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.addUndirectedEdge("A","B"))
  }

  @Test def testAddDirectedEdge() = {
  }

  @Test def testOrder() = {
  }

  @Test def testSize() = {
  }

  @Test def testDegree() = {
  }

  @Test
  def EdgeSimpleTest = {
    val AB = GraphEdgeUndirected("A", "B")
    AB shouldNotBeEqualTo GraphEdgeUndirected("C", "A")
    AB shouldNotBeEqualTo "A"
    AB.toString shouldBeEqualTo "A-B"
    AB.canEqual(AB).shouldBeTrue
    AB.canEqual(1).shouldBeFalse
    AB.toString shouldBeEqualTo "A-B"

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