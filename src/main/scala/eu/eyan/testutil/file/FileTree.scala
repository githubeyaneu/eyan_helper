package eu.eyan.testutil.file

import eu.eyan.util.string.StringPlus.StringPlusImplicit
import scala.util.Random
import java.io.PrintWriter
import eu.eyan.util.io.FilePlus.FilePlusImplicit

object FileTree extends App{
  
  createTestFileTree("C:\\tmp\\big", 1000000)
  
  def createTestFileTree(path: String, nrOfFilesAndDirs: Int) = {
    path.asDir.mkdirs
    val random = new Random(2)
    var fileCt = 0
    
    def noFile = random.nextInt(100) < 20
    val maxFileNr = 100 * 1000
    val fileCoeff = -1.6
    
    def noDir = random.nextInt(100) < 70
    val maxDirNr = 40 * 1000
    val dirCoeff = -2.2
    
    def randomFileNr = if(noFile) 0 else maxFileNr * Math.pow(random.nextInt(maxFileNr).toDouble, fileCoeff) 
    def randomDirNr = if(noDir) 0 else maxDirNr * Math.pow(random.nextInt(maxDirNr).toDouble, dirCoeff)
    
    val nrOfDirs = nrOfFilesAndDirs * 15 /100
    
//    while (fileCt < nrOfFiles){
//      
//    }
    //println( (1 to 1000).toList.map(x => nr) )
    //(1 to 100000/(+1)).toList.map(path+"\\"+_+".txt").foreach(f=>writeFile(f, "a"))
    //val dirs = random.nextInt(50)

//    println("File+Ktár eloszlása","2,2%, 8277, -1,63",
//    "C:\\DEV"
//        .asFile
//        .fileTreeWithItself.filter(f=>f!=null && f.isDirectory)
//        .map(_.listFilesIfExists)
//        .filter(_!=null)
//        .map(_.length)
//        .groupBy(identity)
//        .mapValues(_.size)
//        .toList
//        .sorted
//        .map(t => t._1+"\t"+t._2+"\r\n")
//        .mkString
//    )

//    println("Ktár eloszlása","69,5%, 40000, -2,2",
//        "C:\\DEV"
//        .asFile
//        .fileTreeWithItself.filter(f=>f!=null && f.isDirectory)
//        .map(_.listFilesIfExists)
//        .filter(_!=null)
//        .map(_.filter(_.isDirectory))
//        .map(_.length)
//        .groupBy(identity)
//        .mapValues(_.size)
//        .toList
//        .sorted
//        .map(t => t._1+"\t"+t._2+"\r\n")
//        .mkString
//        )

//    println("File eloszlása","19,9%, 60000 -1,6",
//        "C:\\DEV"
//        .asFile
//        .fileTreeWithItself.filter(f=>f!=null && f.isDirectory)
//        .map(_.listFilesIfExists)
//        .filter(_!=null)
//        .map(_.filter(_.isFile))
//        .map(_.length)
//        .groupBy(identity)
//        .mapValues(_.size)
//        .toList
//        .sorted
//        .map(t => t._1+"\t"+t._2+"\r\n")
//        .mkString
//        )
  }
  
  def writeFile(filename: String, content: String) = new PrintWriter(filename) { write(content); close }
}