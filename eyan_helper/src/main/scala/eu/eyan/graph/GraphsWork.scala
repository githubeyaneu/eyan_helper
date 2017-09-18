package eu.eyan.graph

import eu.eyan.util.string.StringPlus.StringPlusImplicit



object Graphs extends App {
  println("xx")

  new GraphValuedSets().addNode("A").addNode("B").addEdge("A", "B", 1).mkString.println
}















trait GraphEdgeValued[TYPE] { // TODO rename: colored??
  def mkString = s"$node1-$node2 $edge"
  def node1: Any
  def node2: Any
  def edge: TYPE
}

////////////////////////// refactor  /////////////////////////


trait GraphValued[NODE, EDGE] { // rename to colored???

  type GRAPH = GraphValued[NODE, EDGE]

  def addNode(node: NODE): GRAPH

  def addEdge(node1: NODE, node2: NODE, edge: EDGE): GRAPH

  def mkString = getNodes.mkString("Nodes:\r\n  ", "\r\n  ", "\r\n") + getEdges.map(_.mkString).mkString("Edges:\r\n  ", "\r\n  ", "\r\n")

  def getNodes: Iterable[NODE]

  def getEdges: Iterable[GraphEdgeValued[EDGE]]
}

class EdgeValued[EDGE](val edge: EDGE, val node1: Any, val node2: Any) extends GraphEdgeValued[EDGE] {
}

class GraphValuedSets[NODE, EDGE](nodes: Set[NODE] = Set[NODE](), edges: Set[EdgeValued[EDGE]] = Set[EdgeValued[EDGE]]()) extends GraphValued[NODE, EDGE] {
  def addNode(node: NODE) = new GraphValuedSets(nodes + node, edges)
  def addEdge(node1: NODE, node2: NODE, edge: EDGE) = new GraphValuedSets(nodes, edges + new EdgeValued(edge, node1, node2))
  def getNodes = nodes
  def getEdges = edges
}

