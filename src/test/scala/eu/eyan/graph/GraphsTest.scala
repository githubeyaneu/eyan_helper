package eu.eyan.graph

import org.junit.Test
import org.junit.runner.RunWith

import eu.eyan.testutil.assertt.AssertPlus._
import eu.eyan.graph.impl.GraphEdgeUndirected
import eu.eyan.graph.impl.GraphImplSimple
import eu.eyan.graph.impl.GraphEdgeDirected

class GraphsTest {

  @Test def findDirectedForestVerticesInLevels = {
    val emptyGraph = GraphImplSimple[String]()

    expectException(classOf[GraphUndirectedNotSupported], { Graphs.findDirectedForestVerticesInLevels(emptyGraph) })

    val graph = GraphImplSimple[String](GraphDirectionDirected)
    Graphs.findDirectedForestVerticesInLevels(graph) shouldBeEmpty

    val ab = graph.addDirectedEdge("A", "B")
    var levels = Graphs.findDirectedForestVerticesInLevels(ab).toList
    levels shouldHaveSize 2
    levels(0) shouldBeEqualTo (0, Set("B"))
    levels(1) shouldBeEqualTo (1, Set("A"))

    val ab_cb = graph.addDirectedEdge("A", "B").addDirectedEdge("C", "B")
    levels = Graphs.findDirectedForestVerticesInLevels(ab_cb).toList
    levels shouldHaveSize 2
    levels(0) shouldBeEqualTo (0, Set("B"))
    levels(1) shouldBeEqualTo (1, Set("A", "C"))

    val ab_cb_ca = graph.addDirectedEdge("A", "B").addDirectedEdge("C", "B").addDirectedEdge("C", "A")
    levels = Graphs.findDirectedForestVerticesInLevels(ab_cb_ca).toList
    levels shouldHaveSize 3
    levels(0) shouldBeEqualTo (0, Set("B"))
    levels(1) shouldBeEqualTo (1, Set("A"))
    levels(2) shouldBeEqualTo (2, Set("C"))

    val cycle = graph.addDirectedEdge("A", "B").addDirectedEdge("B", "A")
    expectException(classOf[GraphCycledException], { Graphs.findDirectedForestVerticesInLevels(cycle) })

    val ab_cb_dc = graph.addDirectedEdge("A", "B").addDirectedEdge("C", "A").addDirectedEdge("D", "C")
    levels = Graphs.findDirectedForestVerticesInLevels(ab_cb_dc).toList
    levels shouldHaveSize 4
    levels(0) shouldBeEqualTo (0, Set("B"))
    levels(1) shouldBeEqualTo (1, Set("A"))
    levels(2) shouldBeEqualTo (2, Set("C"))
    levels(3) shouldBeEqualTo (3, Set("D"))
  }

  @Test def isConnected = {
    val emptyGraph = GraphImplSimple[String]()

    Graphs.isConnected(emptyGraph) shouldBeTrue // TODO ???

    //TODO what if undirected???
    //				  expectException(classOf[GraphUndirectedNotSupported], {Graphs.findDirectedForestVerticesInLevels(emptyGraph)})

    val a = emptyGraph.add("A")
    Graphs.isConnected(a) shouldBeTrue

    val a_b = a.add("B")
    Graphs.isConnected(a_b) shouldBeFalse
    
    val ab = emptyGraph.addUndirectedEdge("A", "B")
    Graphs.isConnected(ab) shouldBeTrue 

    val ab_c = emptyGraph.addUndirectedEdge("A", "B").add("C")
    Graphs.isConnected(ab_c) shouldBeFalse 
    
    val ab_bc = ab.addUndirectedEdge("B", "C")
    Graphs.isConnected(ab_bc) shouldBeTrue 

    val ab_cd = ab.addUndirectedEdge("C", "D")
    Graphs.isConnected(ab_cd) shouldBeFalse 
    
    val ab_cd_bd = ab_cd.addUndirectedEdge("B", "D")
    Graphs.isConnected(ab_cd_bd) shouldBeTrue 
  }
}