package eu.eyan.graph

import org.junit.Test
import eu.eyan.graph.impl.GraphImplSimple

/** https://qubit.hu/2018/03/26/esz-ventura-fel-tudod-darabolni-virtualisan-a-legertekesebb-magyar-belyegivet 
 *  
 *   1           2  3  4  5 
 *   6  7  8  9 10 11 12 13 14 
 *  15 16 17 18 19 20 21 22 23 24 
 *  
 *  */


class Feladat {

    @Test def feladat() = {
    
    val vs =
    (1 to 1).map(i=> List(i, i+5)) ++
    (2 to 5).map(i=> List(i, i+8)) ++
    (6 to 14).map(i=> List(i, i+9))++
    (2 to 5).sliding(2, 1) ++
    (6 to 14).sliding(2, 1) ++
    (15 to 24).sliding(2, 1) 
    
    val graph24 = vs.foldLeft(GraphImplSimple[Int]())((g,e)=>g.addUndirectedEdge(e(0),e(1)))
    
    println(graph24)
    
//    e.addUndirectedEdge(,)
    val points = graph24.vertices.toList
    
    val verticesCombinations5 = points.combinations(5)
    
    var ct = 0
    for(vertices5 <- verticesCombinations5){
      val subGraph5 = Graphs.subGraph(graph24, vertices5.toSet)
      
      if(subGraph5.order==5)
        if(Graphs.isConnected(subGraph5))
        {
          val vertices19 = graph24.vertices.toSet -- subGraph5.vertices.toSet
          val subGraph19 = Graphs.subGraph(graph24, vertices19)
          if(Graphs.isConnected(subGraph19)){
            ct+=1
        	  println(ct+". "+vertices5+"  ---  "+subGraph19 )
          }
        }
      subGraph5
    }
    
    }
}