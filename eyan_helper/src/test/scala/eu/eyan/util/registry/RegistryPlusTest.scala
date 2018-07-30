package eu.eyan.util.registry

import org.junit.runner.RunWith
import eu.eyan.testutil.TestPlus
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import org.junit.Test

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class RegistryPlusTest extends TestPlus {

  @Test
  def writeReadClear_NoUnnededOutput = {
    collectOutputAndError(RegistryPlus.write("testKey", "a", "b")) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.read("testKey", "a")) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.clear("testKey")) ==> OutAndErr("", "")
  }

}