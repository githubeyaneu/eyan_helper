package eu.eyan.graph.impl

import eu.eyan.graph.GraphEdge
import eu.eyan.graph.GraphEdgeDirection
import eu.eyan.graph.GraphEdgeDirectionDirected
import eu.eyan.graph.GraphEdgeDirectionUndirected
import eu.eyan.graph.GraphUndirectedEdgeNotSupported

object GraphEdgeSimple {
  def apply[VERTEX](direction: GraphEdgeDirection, vertices: VERTEX*) = new GraphEdgeSimple(direction, vertices.toList)
}

class GraphEdgeSimple[VERTEX](val direction: GraphEdgeDirection, val vertices: List[VERTEX]) extends GraphEdge[VERTEX] {

  def undirected: Boolean = direction == GraphEdgeDirectionUndirected

  def directed: Boolean = direction == GraphEdgeDirectionDirected

  def canEqual(that: Any): Boolean = that.getClass == classOf[GraphEdgeSimple[VERTEX]] 

  override def toString = vertices.mkString( if(undirected) "-" else "->")

  override def equals(that: Any): Boolean =
    that match {
      case that: GraphEdgeSimple[VERTEX] => canEqual(that) && that.direction == direction && (if(undirected) vertices.toSet == that.vertices.toSet else vertices == that.vertices)
      case _                                => false
    }
  
  override def hashCode: Int = vertices.map(_.hashCode*31).sum // FIXME this can be much better
  
  def predecessor = if(direction==GraphEdgeDirectionDirected) vertices.head else throw new GraphUndirectedEdgeNotSupported

  def successor = if(direction==GraphEdgeDirectionDirected) vertices(1) else throw new GraphUndirectedEdgeNotSupported
}