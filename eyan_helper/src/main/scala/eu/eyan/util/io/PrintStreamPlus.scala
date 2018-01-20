package eu.eyan.util.io

import java.io.PrintStream
import java.io.OutputStream

object PrintStreamPlus {
  implicit class PrintStreamImplicit[T <: PrintStream](printStream: T) {
//    def copyTo(onWrite: Int => Unit) = new PrintStream(OutputStreamPlus(i => { printStream.write(i); onWrite(i) }, printStream.flush, printStream.close))
    def copyToStream(other: OutputStream) = new PrintStream(OutputStreamPlus(i => { printStream.write(i); other.write(i) }, { printStream.flush; other.flush }, { printStream.close; other.close }))
  }
}