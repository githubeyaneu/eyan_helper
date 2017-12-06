package eu.eyan.graph.impl

import eu.eyan.graph.GraphEdge
import eu.eyan.graph.GraphEdgeDirectionDirected

/** create directed edge */
object GraphEdgeDirected {
  def apply[VERTEX](vertex1: VERTEX, vertex2: VERTEX) = GraphEdgeSimple(GraphEdgeDirectionDirected, vertex1, vertex2)
}
