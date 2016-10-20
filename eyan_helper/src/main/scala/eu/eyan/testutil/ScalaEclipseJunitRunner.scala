package eu.eyan.testutil

import org.junit.runners.BlockJUnit4ClassRunner

class ScalaEclipseJunitRunner(klass: Class[_]) extends BlockJUnit4ClassRunner(klass) {
  override def validateTestMethods(errors: java.util.List[Throwable]) = {}
  override def validateInstanceMethods(errors: java.util.List[Throwable]) = {}
  override def collectInitializationErrors(errors: java.util.List[Throwable]) = {}
}