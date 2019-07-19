package eu.eyan.util.registry

import eu.eyan.log.Log
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.io.OutputStreamPlus
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import java.io.PrintStream
import eu.eyan.util.io.PrintStreamPlus
import java.util.prefs.Preferences
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import eu.eyan.util.scala.Try
import eu.eyan.util.scala.TryCatch

object RegistryGroup {
  def apply(groupName: String) = new RegistryGroup(groupName)
}

class RegistryGroup(val groupName: String) {
  def registryValue(parameterName: String) = new RegistryValue(this, parameterName)
}

class RegistryValue(registryGroup: RegistryGroup, parameterName: String) {
  def read = RegistryPlus.readOption(registryGroup.groupName, parameterName)
  def save(value: String) = RegistryPlus.write(registryGroup.groupName, parameterName, value)

  def readMore = RegistryPlus.readMoreOption(registryGroup.groupName, parameterName)
  def saveMore(values: Array[String]) = RegistryPlus.writeMore(registryGroup.groupName, parameterName, values)
}

object RegistryPlus extends App {

  // TODO why val does not work (logging)
  private def REGISTRY = """HKEY_CURRENT_USER\Software\JavaSoft\Prefs\""" + REGISTRY_PLUS
  // TODO why val does not work ????? arghhhh
  private def REGISTRY_PLUS = "RegistryPlus"

  def write(key: String, name: String, value: String): Unit = {
    val valueHex = value.toHexEncode
    Log.debug(s"$REGISTRY\\$key, $name, $value, $valueHex")

    filterJavaBugErrorLines { Preferences.userRoot().node(REGISTRY_PLUS).node(key).put(name, valueHex) }
  }

  def delete(key: String, name: String): Unit = {
    Log.debug(s"$REGISTRY\\$key, $name")
    filterJavaBugErrorLines { Preferences.userRoot().node(REGISTRY_PLUS).node(key).remove(name) }
  }

  def writeMore(key: String, name: String, values: Array[String]): Unit = {
    val valuesHex = values.map(_.toHexEncode)
    Log.debug(s"$REGISTRY\\$key, $name, ${values.mkString}, ${valuesHex.mkString("_")}")

    filterJavaBugErrorLines { Preferences.userRoot().node(REGISTRY_PLUS).node(key).put(name, valuesHex.mkString("_")) }
  }

  def readOption(key: String, name: String): Option[String] = {
    Log.debug(s"$REGISTRY\\$key, $name")
    filterJavaBugErrorLines {
      if (Preferences.userRoot().nodeExists(REGISTRY_PLUS) && Preferences.userRoot().node(REGISTRY_PLUS).nodeExists(key)) {
        val valueHex = Preferences.userRoot().node(REGISTRY_PLUS).node(key).get(name, null)

        Log.debug(s"valueHex=$valueHex")
        val value =
          if (valueHex == null) None
          else if (valueHex == "") Option(valueHex)
          else
            try { Option(valueHex.toHexDecode) }
            catch {
              case nfe: NumberFormatException => { Log.error(s"error converting from hex valueHex=$valueHex"); Option(valueHex) }
              case t: Throwable               => { Log.error(s"error converting from hex valueHex=$valueHex", t); Option(valueHex) }
            }
        Log.debug(s"value=$value")
        value
      } else {
        Log.debug(s"not exists")
        None
      }
    }
  }

  def readMoreOption(key: String, name: String): Option[List[String]] = {
    Log.debug(s"$REGISTRY\\$key, $name")

    filterJavaBugErrorLines {
      if (Preferences.userRoot().nodeExists(REGISTRY_PLUS) && Preferences.userRoot().node(REGISTRY_PLUS).nodeExists(key)) {
        val valuesHex = Preferences.userRoot().node(REGISTRY_PLUS).node(key).get(name, null)

        Log.debug(s"valueHex=valuesHex")
        val values =
          if (valuesHex == null) None
          else if (valuesHex == "") Option(List(valuesHex))
          else
            try { Option(valuesHex.split("_").map(_.toHexDecode).toList) }
            catch {
              case nfe: NumberFormatException => { Log.error(s"error converting from hex valueHex=$valuesHex"); Option(List(valuesHex)) }
              case t: Throwable               => { Log.error(s"error converting from hex valueHex=$valuesHex", t); Option(List(valuesHex)) }
            }
        Log.debug(s"values=${values.map(_.mkString)}")
        values
      } else {
        Log.debug(s"not exists")
        None
      }
    }
  }

  def clear(key: String) = {
    Log.debug(s"$REGISTRY\\$key")
    filterJavaBugErrorLines {
      if (Preferences.userRoot().nodeExists(REGISTRY_PLUS) && Preferences.userRoot().node(REGISTRY_PLUS).nodeExists(key))
        Preferences.userRoot().node(REGISTRY_PLUS).node(key).removeNode
    }
  }

  def export(key: String): Option[String] = {
    Log.debug(s"$REGISTRY\\$key")
    filterJavaBugErrorLines {
      if (Preferences.userRoot().nodeExists(REGISTRY_PLUS) && Preferences.userRoot().node(REGISTRY_PLUS).nodeExists(key)) {
        val os = new ByteArrayOutputStream();
        Preferences.userRoot().node(REGISTRY_PLUS).node(key).exportSubtree(os)
        Some(os.toString)
      } else
        None
    }
  }

  def importt(key: String, imp: String) = {
    Log.debug(s"$REGISTRY\\$key")
    Log.debug(imp)
    TryCatch({ Preferences.importPreferences(new ByteArrayInputStream(imp.getBytes)); true }, false)
  }

  private def filterJavaBugErrorLines[T](action: => T) = {
    PrintStreamPlus.filterErrorLines(
      List(
        """java.util.prefs.WindowsPreferences <init>""",
        """WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0x80000002. Windows RegCreateKeyEx(...) returned error code 5."""))(
        action)
  }

}