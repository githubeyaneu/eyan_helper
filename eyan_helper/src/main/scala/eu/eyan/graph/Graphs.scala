package eu.eyan.graph

class GraphUndirectedNotSupported extends Exception("This function is not supported for undirected graphs.")
class GraphCycledException extends Exception("The graph should not contain cycles.")

object Graphs {

  //  def isForest[VERTEX, EDGE](graph: Graph[VERTEX, EDGE]) = {
  //    findDirectedForestVerticesInLevels(graph, graph.vertices.toSet)
  //  }

  /** Find the dependency levels of a directed graph. On the first level(0) comes the vertices that have no directed edges starting from it.
   *  The next level is that have edges ending only in higher level vertices.
   *  Example: 
   *      Graph: A<-B, B<-C, A<-D, B<-D, E
   *      Result: 0 - A,E
   *              1 - B
   *              2 - C, D
   **/
  def findDirectedForestVerticesInLevels[VERTEX, EDGE](graph: Graph[VERTEX, EDGE], alreadySelectedVertices: Set[VERTEX] = Set[VERTEX](), level: Int = 0): Stream[Tuple2[Int, Set[_]]] = {
    //    println("findDirectedForestVerticesInLevels", graph, alreadySelectedVertices, level)
    val remainingVertices = graph.vertices.toSet -- alreadySelectedVertices

    if (graph.undirected) throw new GraphUndirectedNotSupported //TODO: if(graph.loops) throw new GraphLoopsNotSupported
    else if (remainingVertices.isEmpty) Stream()
    else {
      //      println("  alreadySelectedVertices " + alreadySelectedVertices)
      //      println("  remainingVertices " + remainingVertices)

      val nextVertices = remainingVertices filter { remainingVertex =>
        {
          //          println("  remainingVertex " + remainingVertex)
          val edges = graph.edges(remainingVertex) filter (e => e.predecessor == remainingVertex)
          //          println("    edges " + edges)
          val edgesIntoReminingVertices = edges.filter(e => remainingVertices.contains(e.vertices.toList(1)))
          //          println("    edgesIntoReminingVertices " + edgesIntoReminingVertices)
          edgesIntoReminingVertices.isEmpty
        }
      }
      //      println("  nextVertices " + nextVertices)
      if (nextVertices.isEmpty) throw new GraphCycledException
      Stream((level, nextVertices)) #::: findDirectedForestVerticesInLevels(graph, alreadySelectedVertices ++ nextVertices, level + 1)
    }
  }
}