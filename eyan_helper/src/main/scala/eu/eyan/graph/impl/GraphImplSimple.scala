package eu.eyan.graph.impl

import eu.eyan.graph.Graph
import eu.eyan.graph.GraphDirectedEdgeNotSupported
import eu.eyan.graph.GraphDirectionUndirected
import eu.eyan.graph.GraphEdge
import eu.eyan.graph.GraphDirection
import eu.eyan.graph.GraphEdgeDirection
import eu.eyan.graph.GraphUndirectedEdgeNotSupported
import eu.eyan.graph.GraphDirectionDirected
import eu.eyan.graph.GraphVertexNotFound

object GraphImplSimple {
	def apply[VERTEX](): Graph[VERTEX, Any] = apply(GraphDirectionUndirected)
	
	def apply[VERTEX](direction: GraphDirection): Graph[VERTEX, Any] = new GraphImplSimple[VERTEX](Set(), Set(), direction)
	
	//TODO: it is possible to create graph with edges but without vertices...
  private def apply[VERTEX](
      vertices: Set[VERTEX] = Set[VERTEX](), 
      edges: Set[GraphEdge[VERTEX]] = Set[GraphEdge[VERTEX]](),
      direction: GraphDirection = GraphDirectionUndirected): Graph[VERTEX, Any] =
    new GraphImplSimple[VERTEX](vertices, edges, direction)
}

/** Simple Implementation of graph */
class GraphImplSimple[VERTEX] protected (val vertices: Set[VERTEX], val edges: Set[GraphEdge[VERTEX]], val direction: GraphDirection) extends Graph[VERTEX, Any] {

  override def equals(that: Any) = that.isInstanceOf[GraphImplSimple[VERTEX]] && that.asInstanceOf[GraphImplSimple[VERTEX]].vertices == vertices && that.asInstanceOf[GraphImplSimple[VERTEX]].edges == edges
  override def hashCode = 31 * (31 + edges.hashCode) + vertices.hashCode
  override def toString = {
    //TODO implement sort???
    val vs = vertices
    val es=edges
    s"GraphImplSimple[$direction, $vs, $es]"
  }

  def add(vertex: VERTEX): GRAPH = GraphImplSimple(vertices + vertex, edges, direction)
  def add(edge: GraphEdge[VERTEX]): GRAPH = {
    if (edge.undirected && direction == GraphDirectionDirected) throw new GraphUndirectedEdgeNotSupported
    if (edge.directed && direction == GraphDirectionUndirected) throw new GraphDirectedEdgeNotSupported
    GraphImplSimple(vertices ++ edge.vertices, edges + edge, direction)
  }
  
  def addUndirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH = add(GraphEdgeUndirected(vertex1, vertex2))
  
  def addDirectedEdge(vertex1: VERTEX, vertex2: VERTEX): GRAPH = add(GraphEdgeDirected(vertex1, vertex2))

  def size = edges.size
  def order = vertices.size
  def degree(vertex: VERTEX) = {
    if (vertices.contains(vertex))  edges.toList.map(edge => edge.vertices.count(_ == vertex)).sum
    else throw new GraphVertexNotFound
  }
  
  def edges(vertex: VERTEX) = edges.filter(edge => edge.vertices.exists(_ == vertex))

  def edges(vertices: VERTICES) = edges.filter(edge => edge.vertices.toSet.intersect(vertices.toSet).nonEmpty)
  
  def addAll(edges: EDGES):GRAPH = edges.foldLeft(this.asInstanceOf[GRAPH])((graph, edge) => graph.add(edge))
}