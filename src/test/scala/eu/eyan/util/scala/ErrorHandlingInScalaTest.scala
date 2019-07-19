package eu.eyan.util.scala

import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunner
import org.junit.Test
import org.fest.assertions.Assertions._
import java.io.Closeable
import org.junit.Rule
import org.junit.internal.runners.statements.ExpectException
import org.junit.rules.ExpectedException
import org.fest.assertions.Assertions
import org.junit.Assert
import scala.util.Success
import scala.util.Failure
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import org.junit.experimental.theories.Theory
import eu.eyan.testutil.TestPlus
import eu.eyan.util.string.StringPlus.StringPlusImplicit

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class ErrorHandlingInScalaTest extends TestPlus {

  var log = ""
  val e1 = new Exception("err1")
  val e2 = new Exception("err2")
  val e3 = new Exception("err3")
  
  def ok1(in: String): String = { log += "ok1"; in + "1" }
  def ok2(in: String): String = { log += "ok2"; in + "2" }
  def ok3(in: String): String = { log += "ok3"; in + "3" }
  def err1(in: String): String = { log += "err1"; throw e1 }
  def err2(in: String): String = { log += "err2"; throw e2 }
  def err3(in: String): String = { log += "err3"; throw e3 }

  @Test def ok = {
		  val result = 
    //Try("0").map(ok1).map(ok2).get ==> "012"

        Try("0").map(ok1).map(err1)

    //    Try("0").map(err1).map(ok1).get ==> "012"

		  
      log ==> "ok1err1"
      result ==> Failure(e1)

  }
}