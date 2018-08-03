package eu.eyan.graph

import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.assertt.AssertPlus._
import eu.eyan.graph.impl.GraphEdgeUndirected
import eu.eyan.graph.impl.GraphImplSimple
import eu.eyan.graph.impl.GraphEdgeDirected

class GraphTest {

  @Test def testVertices = {
    val emptyGraph = GraphImplSimple[String]()
    emptyGraph.vertices shouldBe empty
  }

  @Test def testEdges = {
    val emptyGraph = GraphImplSimple[String]()
    emptyGraph.edges shouldBe empty
  }

  @Test def testAddVertex = {
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

  @Test def testDirection = {
    GraphImplSimple[String](GraphDirectionUndirected).direction shouldBeEqualTo GraphDirectionUndirected

    GraphImplSimple[String](GraphDirectionDirected).direction shouldBeEqualTo GraphDirectionDirected

    GraphImplSimple[String](GraphDirectionMixed).direction shouldBeEqualTo GraphDirectionMixed
  }

  @Test def testUndirected = {
    GraphImplSimple[String](GraphDirectionUndirected).undirected shouldBeTrue

    GraphImplSimple[String](GraphDirectionDirected).undirected shouldBeFalse

    GraphImplSimple[String](GraphDirectionMixed).undirected shouldBeTrue
  }

  @Test def testDirected = {
    GraphImplSimple[String](GraphDirectionUndirected).directed shouldBeFalse

    GraphImplSimple[String](GraphDirectionDirected).directed shouldBeTrue

    GraphImplSimple[String](GraphDirectionMixed).directed shouldBeTrue
  }

  @Test def testMixed = {
    GraphImplSimple[String](GraphDirectionUndirected).mixed shouldBeFalse

    GraphImplSimple[String](GraphDirectionDirected).mixed shouldBeFalse

    GraphImplSimple[String](GraphDirectionMixed).mixed shouldBeTrue
  }

  @Test def testAddEdge() = {
    val e = GraphImplSimple[String]()
    val AB = GraphEdgeUndirected("A", "B")
    val ab = e.add(AB)

    ab.vertices shouldHaveSize 2
    ab.vertices shouldContain "A"
    ab.vertices shouldContain "B"

    ab.edges shouldBe nonEmpty
    ab.edges shouldHaveSize 1

    val edge = ab.edges.toList(0)
    edge shouldBeSameInstanceTo AB

    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectNoException(undirected.add(GraphEdgeUndirected("A", "B")))
    expectException(classOf[GraphDirectedEdgeNotSupported], undirected.add(GraphEdgeDirected("A", "B")))

    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectException(classOf[GraphUndirectedEdgeNotSupported], directed.add(GraphEdgeUndirected("A", "B")))
    expectNoException(directed.add(GraphEdgeDirected("A", "B")))

    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.add(GraphEdgeUndirected("A", "B")))
    expectNoException(mixed.add(GraphEdgeDirected("A", "B")))
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
    empty_AB.edges shouldContain GraphEdgeUndirected("A", "B")
    empty_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    empty_AB shouldNotBeEqualTo ab

    val a_AB = a.addUndirectedEdge("A", "B")
    a_AB.vertices shouldBe nonEmpty
    a_AB.vertices shouldHaveSize 2
    a_AB.vertices shouldContain "A"
    a_AB.vertices shouldContain "B"
    a_AB.edges shouldBe nonEmpty
    a_AB.edges shouldHaveSize 1
    a_AB.edges shouldContain GraphEdgeUndirected("A", "B") 
    a_AB.edges shouldContain GraphEdgeUndirected("B", "A")
    a_AB shouldNotBeEqualTo ab

    val ab_AB = ab.addUndirectedEdge("A", "B")
    ab_AB shouldBeEqualTo empty_AB

    val ab_AB_BA = ab_AB.addUndirectedEdge("B", "A")
    ab_AB_BA shouldBeEqualTo ab_AB

    val aab = ab.add("A")
    aab shouldBeEqualTo ab

    // directed
    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectNoException(undirected.addUndirectedEdge("A", "B"))

    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectException(classOf[GraphUndirectedEdgeNotSupported], directed.addUndirectedEdge("A", "B"))

    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.addUndirectedEdge("A", "B"))

    // add(edge)
    emptyGraph.addUndirectedEdge("A", "B") shouldBeEqualTo emptyGraph.add(GraphEdgeUndirected("A", "B"))
    emptyGraph.addUndirectedEdge("B", "A") shouldBeEqualTo emptyGraph.add(GraphEdgeUndirected("A", "B"))
  }

  @Test def testAddDirectedEdge() = {

    val emptyGraph = GraphImplSimple[String](GraphDirectionDirected)
    val a = emptyGraph.add("A")
    val ab = a.add("B")

    val empty_AB = emptyGraph.addDirectedEdge("A", "B")
    empty_AB.vertices shouldBe nonEmpty
    empty_AB.vertices shouldHaveSize 2
    empty_AB.vertices shouldContain "A"
    empty_AB.vertices shouldContain "B"
    empty_AB.edges shouldBe nonEmpty
    empty_AB.edges shouldHaveSize 1
    empty_AB.edges shouldContain GraphEdgeDirected("A", "B") 
    empty_AB.edges shouldNotContain GraphEdgeDirected("B", "A")
    empty_AB shouldNotBeEqualTo ab

    val a_AB = a.addDirectedEdge("A", "B")
    a_AB.vertices shouldBe nonEmpty
    a_AB.vertices shouldHaveSize 2
    a_AB.vertices shouldContain "A"
    a_AB.vertices shouldContain "B"
    a_AB.edges shouldBe nonEmpty
    a_AB.edges shouldHaveSize 1
    a_AB.edges shouldContain GraphEdgeDirected("A", "B")
    a_AB shouldNotBeEqualTo ab

    val ab_AB = ab.addDirectedEdge("A", "B")
    ab_AB shouldBeEqualTo empty_AB

    val ab_BA = ab.addDirectedEdge("B", "A")
    ab_BA shouldNotBeEqualTo ab_AB

    val ab_AB_BA = ab_AB.addDirectedEdge("B", "A")
    ab_AB_BA shouldNotBeEqualTo ab_AB

    // directed
    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectException(classOf[GraphDirectedEdgeNotSupported], undirected.addDirectedEdge("A", "B"))

    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectNoException(directed.addDirectedEdge("A", "B"))

    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.addDirectedEdge("A", "B"))

    // add(edge)
    emptyGraph.addDirectedEdge("A", "B") shouldBeEqualTo emptyGraph.add(GraphEdgeDirected("A", "B"))
    emptyGraph.addDirectedEdge("B", "A") shouldNotBeEqualTo emptyGraph.add(GraphEdgeDirected("A", "B"))

    // addUndirectedEdge(edge)
    mixed.addDirectedEdge("A", "B") shouldNotBeEqualTo mixed.addUndirectedEdge("A", "B")
    mixed.addDirectedEdge("B", "A") shouldNotBeEqualTo mixed.addUndirectedEdge("A", "B")
  }

  @Test def testEdgesOfVertex() = {
    val emptyGraph = GraphImplSimple[String](GraphDirectionMixed)
    emptyGraph.edges("A") shouldBeEmpty

    val AB = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B")
    AB.edges("C") shouldBeEmpty

    AB.edges("B") shouldHaveSize 1
    AB.edges("B") shouldContain (GraphEdgeUndirected("A", "B"))

    val graph = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("C", "D").addDirectedEdge("A", "C").addDirectedEdge("C", "A")
    graph.edges("A") shouldHaveSize 3
    graph.edges("A") shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges("A") shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges("A") shouldContain (GraphEdgeDirected("C", "A"))

    graph.edges("B") shouldHaveSize 1
    graph.edges("B") shouldContain (GraphEdgeUndirected("A", "B"))

    graph.edges("C") shouldHaveSize 3
    graph.edges("C") shouldContain (GraphEdgeUndirected("C", "D"))
    graph.edges("C") shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges("C") shouldContain (GraphEdgeDirected("C", "A"))

    graph.edges("D") shouldHaveSize 1
    graph.edges("D") shouldContain (GraphEdgeUndirected("C", "D"))
  }

  @Test def testEdgesOfVertices() = {
    val emptyGraph = GraphImplSimple[String](GraphDirectionMixed)
    emptyGraph.edges(List("A", "B")) shouldBeEmpty

    val AB = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B")
    AB.edges(List("C", "D")) shouldBeEmpty

    AB.edges(List("A", "B")) shouldHaveSize 1
    AB.edges(List("A", "B")) shouldContain (GraphEdgeUndirected("A", "B"))

    val graph = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("C", "D").addDirectedEdge("A", "C").addDirectedEdge("C", "A")
    graph.edges(List("A", "B")) shouldHaveSize 3
    graph.edges(List("A", "B")) shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges(List("A", "B")) shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges(List("A", "B")) shouldContain (GraphEdgeDirected("C", "A"))
    graph.edges(List("A", "B")) shouldBeEqualTo graph.edges(List("B", "A"))

    graph.edges(List("A", "C")) shouldHaveSize 4
    graph.edges(List("A", "C")) shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges(List("A", "C")) shouldContain (GraphEdgeUndirected("C", "D"))
    graph.edges(List("A", "C")) shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges(List("A", "C")) shouldContain (GraphEdgeDirected("C", "A"))

    graph.edges(List("A", "D")) shouldHaveSize 4
    graph.edges(List("A", "D")) shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges(List("A", "D")) shouldContain (GraphEdgeUndirected("C", "D"))
    graph.edges(List("A", "D")) shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges(List("A", "D")) shouldContain (GraphEdgeDirected("C", "A"))

    graph.edges(List("B", "C")) shouldHaveSize 4
    graph.edges(List("B", "C")) shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges(List("B", "C")) shouldContain (GraphEdgeUndirected("C", "D"))
    graph.edges(List("B", "C")) shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges(List("B", "C")) shouldContain (GraphEdgeDirected("C", "A"))

    graph.edges(List("B", "D")) shouldHaveSize 2
    graph.edges(List("B", "D")) shouldContain (GraphEdgeUndirected("A", "B"))
    graph.edges(List("B", "D")) shouldContain (GraphEdgeUndirected("C", "D"))

    graph.edges(List("C", "D")) shouldHaveSize 3
    graph.edges(List("C", "D")) shouldContain (GraphEdgeUndirected("C", "D"))
    graph.edges(List("C", "D")) shouldContain (GraphEdgeDirected("A", "C"))
    graph.edges(List("C", "D")) shouldContain (GraphEdgeDirected("C", "A"))

  }
  
  @Test def testAddAllEdges() = {
    val e = GraphImplSimple[String]()
    val AB = GraphEdgeUndirected("A", "B")
    val ab_all = e.addAll(List(AB, AB))
    val ab = e.add(AB)
    ab_all shouldBeEqualTo ab
    
    val CD = GraphEdgeUndirected("C", "D")
    e.addAll(List(AB, CD)) shouldBeEqualTo e.add(AB).add(CD)

    val undirected = GraphImplSimple[String](GraphDirectionUndirected)
    expectNoException(undirected.addAll(List(AB)))
    expectException(classOf[GraphDirectedEdgeNotSupported], undirected.addAll(List(GraphEdgeDirected("A", "B"))))

    val directed = GraphImplSimple[String](GraphDirectionDirected)
    expectException(classOf[GraphUndirectedEdgeNotSupported], directed.addAll(List(GraphEdgeUndirected("A", "B"))))
    expectNoException(directed.addAll(List(GraphEdgeDirected("A", "B"))))

    val mixed = GraphImplSimple[String](GraphDirectionMixed)
    expectNoException(mixed.addAll(List(GraphEdgeUndirected("A", "B"))))
    expectNoException(mixed.addAll(List(GraphEdgeDirected("A", "B"))))
  }

  @Test def testOrder() = {
    val emptyGraph = GraphImplSimple[String](GraphDirectionMixed)
    emptyGraph.order shouldBeEqualTo 0

    val AB = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B")
    AB.order shouldBeEqualTo 2

    val graph = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("C", "D").addDirectedEdge("A", "C").addDirectedEdge("C", "A")
    graph.order shouldBeEqualTo 4
  }

  @Test def testSize() = {
    val emptyGraph = GraphImplSimple[String](GraphDirectionMixed)
    emptyGraph.size shouldBeEqualTo 0

    val AB = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B")
    AB.size shouldBeEqualTo 1

    val graph = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("C", "D").addDirectedEdge("A", "C").addDirectedEdge("C", "A")
    graph.size shouldBeEqualTo 4

    val graph2 = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("A", "B")
    graph2.size shouldBeEqualTo 1
  }

  @Test def testDegree() = {
    val emptyGraph = GraphImplSimple[String](GraphDirectionMixed)
    expectException(classOf[GraphVertexNotFound], emptyGraph.degree("A"))

    val AB = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B")
    AB.degree("A") shouldBeEqualTo 1
    AB.degree("B") shouldBeEqualTo 1

    val graph = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("C", "D").addDirectedEdge("A", "C").addDirectedEdge("C", "A")
    graph.degree("A") shouldBeEqualTo 3
    graph.degree("B") shouldBeEqualTo 1
    graph.degree("C") shouldBeEqualTo 3
    graph.degree("D") shouldBeEqualTo 1

    val graph2 = GraphImplSimple[String](GraphDirectionMixed).addUndirectedEdge("A", "B").addUndirectedEdge("A", "B").addUndirectedEdge("A", "A")
    graph2.degree("A") shouldBeEqualTo 3
    graph2.degree("B") shouldBeEqualTo 1
  }
}