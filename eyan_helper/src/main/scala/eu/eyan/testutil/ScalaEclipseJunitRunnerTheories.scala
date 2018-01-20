package eu.eyan.testutil

import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.experimental.theories.Theories

class ScalaEclipseJunitRunnerTheories(klass: Class[_]) extends Theories(klass) {
  override def validateTestMethods(errors: java.util.List[Throwable]) = {}
  override def validateInstanceMethods(errors: java.util.List[Throwable]) = {}
  override def collectInitializationErrors(errors: java.util.List[Throwable]) = {}
}