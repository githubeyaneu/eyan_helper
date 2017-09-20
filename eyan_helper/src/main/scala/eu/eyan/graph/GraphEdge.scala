package eu.eyan.graph

/**
 * This is a simple edge of a graph, that connects two nodes.
 *  The equals and the hashcode are overriden since it is a simple (undirected) edge.
 *  @param <NODE> the type of the nodes
 */
trait GraphEdge[NODE] extends Product2[NODE, NODE]{
  
  /** true if the edge is undirected */
  def undirected: Boolean

  /** true if the edge is directed */
  def directed: Boolean
}