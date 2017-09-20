package eu.eyan.graph.impl

/** create directed edge */
object GraphEdgeDirected {
  def apply[NODE](node1: NODE, node2: NODE) = new GraphEdgeDirected(node1, node2)
}
/** Implementation for directed graph edge*/
class GraphEdgeDirected[NODE](node1: NODE, node2: NODE) extends GraphEdgeUndirected[NODE](node1, node2) {
  override def canEqual(that: Any): Boolean = that.isInstanceOf[GraphEdgeDirected[NODE]]
  override def equals(that: Any): Boolean =
    that match {
      case that: GraphEdgeDirected[NODE] => (_1 == that._1 && _2 == that._2)
      case _                             => false
    }
  override def toString = node1 + "->" + node2
  override def hashCode: Int = (31 * _1.hashCode) + _2.hashCode
  override def directed = true
  override def undirected = false
}