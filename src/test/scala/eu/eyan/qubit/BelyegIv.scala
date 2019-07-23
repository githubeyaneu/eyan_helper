package eu.eyan.qubit

import eu.eyan.graph.impl.GraphImplSimple
import eu.eyan.graph.Graphs

/**
 * https://qubit.hu/2018/03/26/esz-ventura-fel-tudod-darabolni-virtualisan-a-legertekesebb-magyar-belyegivet
 *   1           2  3  4  5
 *   6  7  8  9 10 11 12 13 14
 *  15 16 17 18 19 20 21 22 23 24
 */

class BelyegIv {

  /*@Test*/ def feladat() = {

    val vs =
      (1 to 1).map(i => List(i, i + 5)) ++
        (2 to 5).map(i => List(i, i + 8)) ++
        (6 to 14).map(i => List(i, i + 9)) ++
        (2 to 5).sliding(2, 1) ++
        (6 to 14).sliding(2, 1) ++
        (15 to 24).sliding(2, 1)

    val graph24 = vs.foldLeft(GraphImplSimple[Int]())((g, e) => g.addUndirectedEdge(e.head, e(1)))

    val points = graph24.vertices.toList

    for (darab <- 0 to 24) {
      val verticesCombinations5 = points.combinations(darab)
      val subGraphs5 = verticesCombinations5 map (vertices5 => Graphs.subGraph(graph24, vertices5.toSet))

      val result = subGraphs5.filter(subGraph5 => Graphs.isConnected(subGraph5) && Graphs.isConnected(Graphs.subGraph(graph24, graph24.vertices.toSet -- subGraph5.vertices.toSet)))

      val eredmény = result.map(_.vertices.toList.sorted).toList

      println(eredmény.mkString("\r\n"))
      println(darab+"->"+eredmény.size)
    }
  }
}