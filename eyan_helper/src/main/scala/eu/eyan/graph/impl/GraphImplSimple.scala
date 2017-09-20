package eu.eyan.graph.impl

import eu.eyan.graph.Graph
import eu.eyan.graph.GraphDirectedEdgeNotSupported
import eu.eyan.graph.GraphDirectionUndirected
import eu.eyan.graph.GraphEdge
import eu.eyan.graph.GraphEdgeDirection
import eu.eyan.graph.GraphUndirectedEdgeNotSupported
import eu.eyan.graph.GraphDirectionDirected

object GraphImplSimple {
	def apply[VERTEX](direction: GraphEdgeDirection): Graph[VERTEX, Any] = new GraphImplSimple[VERTEX](Set(), Set(), direction)
	
  def apply[VERTEX](
      nodes: Set[VERTEX] = Set[VERTEX](), 
      edges: Set[GraphEdge[VERTEX]] = Set[GraphEdge[VERTEX]](),
      direction: GraphEdgeDirection = GraphDirectionUndirected): Graph[VERTEX, Any] =
    new GraphImplSimple[VERTEX](nodes, edges, direction)
}

/** Simple Implementation of graph */
class GraphImplSimple[VERTEX] protected (val vertices: Set[VERTEX], val edges: Set[GraphEdge[VERTEX]], val direction: GraphEdgeDirection) extends Graph[VERTEX, Any] {

  override def equals(that: Any) = that.isInstanceOf[GraphImplSimple[VERTEX]] && that.asInstanceOf[GraphImplSimple[VERTEX]].vertices == vertices && that.asInstanceOf[GraphImplSimple[VERTEX]].edges == edges
  override def hashCode = 31 * (31 + edges.hashCode) + vertices.hashCode
  override def toString = s"GraphUndirectedSets $vertices, $edges"

  def add(vertex: VERTEX): GRAPH = GraphImplSimple(vertices + vertex, edges, direction)
  def add(edge: GraphEdge[VERTEX]): GRAPH = {
    if (edge.undirected && direction == GraphDirectionDirected) throw new GraphUndirectedEdgeNotSupported
    if (edge.directed && direction == GraphDirectionUndirected) throw new GraphDirectedEdgeNotSupported
    GraphImplSimple(vertices + edge._1 + edge._2, edges + edge, direction)
  }
  
  def addDirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH = ???
  def addUndirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH = add(GraphEdgeUndirected(vertex1, vertex2))

  def size = edges.size
  def order = vertices.size
  def degree(node: VERTEX) = edges.count(edge => edge._1 == node || edge._2 == node)
}