package eu.eyan.graph.impl

import eu.eyan.graph.GraphEdge

/** create undirected edge */
object GraphEdgeUndirected {
  def apply[NODE](node1: NODE, node2: NODE) = new GraphEdgeUndirected(node1, node2)
}
/** Implementation for undirected graph edge*/
class GraphEdgeUndirected[NODE](node1: NODE, node2: NODE) extends GraphEdge[NODE] {
  def canEqual(that: Any): Boolean = that.isInstanceOf[GraphEdgeUndirected[NODE]]
  def _1 = node1
  def _2 = node2
  override def toString = node1 + "-" + node2
  override def equals(that: Any): Boolean =
    that match {
      case that: GraphEdgeUndirected[NODE] => (_1 == that._1 && _2 == that._2) || (_1 == that._2 && _2 == that._1)
      case _                               => false
    }
  override def hashCode: Int = 31 * _1.hashCode + 31 * _2.hashCode
  def undirected = true
  def directed = false
}