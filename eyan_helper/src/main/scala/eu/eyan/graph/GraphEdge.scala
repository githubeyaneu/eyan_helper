package eu.eyan.graph

trait GraphEdgeDirection
case object GraphEdgeDirectionUndirected extends GraphEdgeDirection
case object GraphEdgeDirectionDirected extends GraphEdgeDirection

/**
 * This is an edge of a graph, that connects vertices.
 *  The equals and the hashcode should be overriden!
 *  @param <VERTEX> the type of the nodes
 */
trait GraphEdge[VERTEX] {
  
	/** the vertices of the edge that it connects */
	def vertices: Iterable[VERTEX]
			
	/** the direction type */
	def direction: GraphEdgeDirection
	
  /** true if the edge is undirected */
  def undirected: Boolean

  /** true if the edge is directed */
  def directed: Boolean
  
  /** the predecessor of the edge */
  def predecessor: VERTEX

  /** the successor of the edge */
  def successor: VERTEX
}