package eu.eyan.util.io

import java.io.File
import java.io.IOException
import java.io.PrintWriter

import org.fest.assertions.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith

import eu.eyan.testutil.ScalaEclipseJunitRunner
import eu.eyan.testutil.TestPlus
import java.io.Closeable
import eu.eyan.util.io.CloseablePlus.CloseablePlusImplicit
import scala.language.reflectiveCalls

@RunWith(classOf[ScalaEclipseJunitRunner])
class CloseablePlusTest extends TestPlus {

  val closeable = new Closeable{
    var closed = false
    def close = closed = true 
  }
  
  @Test
  def closeQuietlyImplicit = {
    closeable.closed ==> false
    closeable.closeQuietly
		closeable.closed ==> true
  }

  @Test
  def closeNullQuietlyImplicit = expectNoException{null.asInstanceOf[Closeable].closeQuietly}

  @Test
  def closeNull_NPE = expect(new NullPointerException, {null.asInstanceOf[Closeable].close})
}