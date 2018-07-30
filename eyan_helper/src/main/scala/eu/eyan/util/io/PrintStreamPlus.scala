package eu.eyan.util.io

import java.io.PrintStream
import java.io.OutputStream
import eu.eyan.util.string.StringPlus.StringPlusImplicit

object PrintStreamPlus {
  implicit class PrintStreamImplicit[T <: PrintStream](printStream: T) {
    def copyToStream(other: OutputStream) = new PrintStream(OutputStreamPlus(i => { printStream.write(i); other.write(i) }, { printStream.flush; other.flush }, { printStream.close; other.close }))
  }

  def filterErrorLines[T](linesToFilter: List[String])(action: => T) = {
    val originalSystemErr = System.err

    var output = ""

    def filterErrorLines(i: Int) = {
      val newChar = i.toChar + ""
      output += newChar

      if (newChar == "\n") {
        if (!output.containsAny(linesToFilter)) originalSystemErr.append(output)
        output = ""
      }
    }

    // TODO implement flush and close...
    System.setErr(new PrintStream(OutputStreamPlus(filterErrorLines, {}, {})))

    val ret = action

    // TODO check if the err was not changed! make sure no asynchronous error comes....
    System.setErr(originalSystemErr)
    originalSystemErr.append(output)

    ret
  }
}