package eu.eyan.util.string

import java.io.File
import eu.eyan.util.io.FileLinesReader
import eu.eyan.util.io.CachedFileLineReader
import java.text.NumberFormat
import eu.eyan.util.memory.Memory._
import eu.eyan.util.time.Timer._
import scala.collection.JavaConverters._
import eu.eyan.log.Log
import eu.eyan.util.awt.AwtHelper

object FileIndex {
  def main(args: Array[String]): Unit = {
        val file = new File("""C:\DEV\tickets\test_trace.txt""")
//    val file = new File("""C:\DEV\tickets\test_traces_big.txt""")
    val reader = new CachedFileLineReader()
    val INDEXING_DEPTH = 1
    //new Thread(AwtHelper.newRunnable { () => {while(true){Thread.sleep(1000); printmem}} }).start
    
    timerStart
    reader.load(file, null)
    println("loaded "+timerElapsed+" "+NumberFormat.getInstance.format(reader.size)+" lines.")
    printmem

    val lineIterator = reader.iterator
    val linesWithIndices = lineIterator.zipWithIndex
//    val lines = for (lineIndex <- 0 to reader.size-1) yield (lineIndex, reader.get(lineIndex))
    println("lines done "+timerElapsed)
    printmem
    
    
    val itemsOfLines = linesWithIndices.map(p => (StringsSearchTree.allSubStrings(p._1, INDEXING_DEPTH).map((_,p._2)))).flatten
    println("itemsOfLines done "+timerElapsed)
    printmem

    val tree = itemsOfLines.foldLeft(StringsSearchTree.newTree[Int])((tree, pair) => tree.add(pair._1, pair._2))
    println("tree done "+timerElapsed)
    printmem

    println(tree)
  }
}