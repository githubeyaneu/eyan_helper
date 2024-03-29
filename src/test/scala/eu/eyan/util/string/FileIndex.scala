package eu.eyan.util.string

import java.io.File
import java.text.NumberFormat

import scala.collection.TraversableOnce.flattenTraversableOnce

import eu.eyan.util.io.CachedFileLineReader
import eu.eyan.util.memory.Memory.printmem
import eu.eyan.util.time.Timer.timerStart
import eu.eyan.util.time.Timer.timerElapsed

object FileIndex {
  def main(args: Array[String]): Unit = {
    val file = new File("""C:\DEV\tickets\test_trace.txt""")
    //    val file = new File("""C:\DEV\tickets\test_traces_big.txt""")
    val reader = new CachedFileLineReader()
    val INDEXING_DEPTH = 1
    // new Thread(AwtHelper.newRunnable { () => {while(true){Thread.sleep(1000); printmem}} }).start

    val timer = timerStart
    reader.load(file, null)
    println("loaded " + timerElapsed + " " + NumberFormat.getInstance.format(reader.size) + " lines.")
    printmem

    val lineIterator = reader.lines
    val linesWithIndices = lineIterator.zipWithIndex
    //    val lines = for (lineIndex <- 0 to reader.size-1) yield (lineIndex, reader.get(lineIndex))
    println("lines done " + timerElapsed)
    printmem

    val itemsOfLines = linesWithIndices.flatMap(p => StringsSearchTree.allSubStrings(p._1, INDEXING_DEPTH).map((_, p._2)))
    println("itemsOfLines done " + timerElapsed)
    printmem

    val tree = itemsOfLines.foldLeft(StringsSearchTree.newTree[Int])((tree, pair) => tree.add(pair._1, pair._2))
    println("tree done " + timerElapsed)
    printmem

    println(tree)
  }
}