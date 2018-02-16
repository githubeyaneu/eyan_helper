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

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class ErrorHandlingInScalaTest extends TestPlus{

  @Test def tcfc2_Closeable1Err_Closeable2Err_ActionErr_CatchErr_Close1Err_Close2Err = {}

  @Theory def closables2() = { }
}