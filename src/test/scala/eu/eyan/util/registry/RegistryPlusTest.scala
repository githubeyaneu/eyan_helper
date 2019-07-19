package eu.eyan.util.registry

import org.junit.runner.RunWith
import eu.eyan.testutil.TestPlus
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import org.junit.Test
import java.util.prefs.Preferences
import eu.eyan.log.Log

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class RegistryPlusTest extends TestPlus {

  @Test
  def writeReadClear_NoUnnededOutput = {
    Log.activateInfoLevel
    collectOutputAndError(RegistryPlus.writeMore("testKey", "a", Array("b", "c"))) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.readMoreOption("testKey", "a")) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.write("testKey", "a", "b")) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.readOption("testKey", "a")) ==> OutAndErr("", "")

    collectOutputAndError(RegistryPlus.clear("testKey")) ==> OutAndErr("", "")
  }

  @Test
  def default = {
    RegistryPlus.clear("newkey")
    RegistryPlus.readOption("newkey", "newname") ==> None
    RegistryPlus.write("newkey", "newname", "newvalue")
    RegistryPlus.readOption("newkey", "newname") ==> Some("newvalue")
    RegistryPlus.clear("newkey")
    RegistryPlus.readOption("newkey", "newname") ==> None
    RegistryPlus.clear("newkey")
  }

  @Test
  def specChars = {
    RegistryPlus.clear("keyÁ")
    RegistryPlus.readOption("keyÁ", "nameŰ") ==> None
    RegistryPlus.write("keyÁ", "nameŰ", "valueß")
    RegistryPlus.readOption("keyÁ", "nameŰ") ==> Some("valueß")
    RegistryPlus.clear("keyÁ")
    RegistryPlus.readOption("keyÁ", "nameŰ") ==> None
    RegistryPlus.clear("keyÁ")
  }

  @Test
  def more = {
    Log.activateDebugLevel
    RegistryPlus.clear("keyÁ")
    RegistryPlus.readMoreOption("keyÁ", "nameŰ") ==> None
    RegistryPlus.writeMore("keyÁ", "nameŰ", Array("valueß", "valueű"))
    RegistryPlus.readMoreOption("keyÁ", "nameŰ") ==> Some(List("valueß", "valueű"))
    RegistryPlus.clear("keyÁ")
    RegistryPlus.readMoreOption("keyÁ", "nameŰ") ==> None
    RegistryPlus.clear("keyÁ")
  }
}