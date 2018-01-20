package eu.eyan.graph.impl

import eu.eyan.graph.GraphEdge
import eu.eyan.graph.GraphEdgeDirectionUndirected

/** create undirected edge */
object GraphEdgeUndirected {
  def apply[VERTEX](vertex1: VERTEX, vertex2: VERTEX) = GraphEdgeSimple(GraphEdgeDirectionUndirected, vertex1, vertex2)
}
