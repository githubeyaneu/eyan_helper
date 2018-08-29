package eu.eyan.util.registry

import eu.eyan.log.Log
import eu.eyan.util.string.StringPlus.StringPlusImplicit
import eu.eyan.util.io.OutputStreamPlus
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import java.io.PrintStream
import eu.eyan.util.io.PrintStreamPlus

object RegistryPlus extends App {
  /* TODO implement sg to read/write from/to win registry. Or even better platform independently... */
  //WinRegistry.writeStringValue(WinRegistry.HKEY_LOCAL_MACHINE, "Software\\eyan.eu", "kej", "valju", WinRegistry.KEY_WOW64_64KEY)

  private def wow = WinRegistry.KEY_WOW64_64KEY
  private def hkey = WinRegistry.HKEY_CURRENT_USER
  private def keyRoot = """Software\JavaSoft\Prefs\eyan.eu"""

  //  WinRegistry.createKey(hkey, keyRoot)
  //  WinRegistry.writeStringValue(hkey, s"$keyRoot", "kej", "valju2", wow)
  //  WinRegistry.readString(hkey, s"$keyRoot", "kej", wow)
  //  WinRegistry.deleteValue(hkey, keyRoot, "kej", wow)
  //  WinRegistry.deleteKey(hkey, keyRoot)

  @deprecated("might be buggy at clearing the value")
  def write(name: String, value: String): Unit = write("", name, value)

  def read(name: String): String = read("", name)

  def write(key: String, name: String, value: String): Unit = {
    val valueHex = value.toHexEncode
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot, $name, $value, $valueHex")

    filterJavaBugErrorLines {
      WinRegistry.createKey(hkey, s"$keyRoot\\$key")
      WinRegistry.writeStringValue(hkey, s"$keyRoot\\$key", name, valueHex, wow)
    }
  }

  def readOption(key: String, name: String): Option[String] = {
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot\\$key, $name")
    val valueHex = filterJavaBugErrorLines { WinRegistry.readString(hkey, s"$keyRoot\\$key", name, wow) }
    Log.debug(s"valueHex=$valueHex")
    val value =
      if (valueHex == null) None
      else if (valueHex== "") Option(valueHex)
      else
        try { Option(valueHex.toHexDecode) }
        catch { case t: Throwable => { Log.error(s"error converting from hex valueHex=$valueHex", t); Option(valueHex) } }
    Log.debug(s"value=$value")
    value
  }
  
  //TODO remove and use only readOption
  @deprecated
  def read(key: String, name: String): String = {
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot\\$key, $name")
    val valueHex = filterJavaBugErrorLines { WinRegistry.readString(hkey, s"$keyRoot\\$key", name, wow) }
    Log.debug(s"valueHex=$valueHex")
    val value =
      if (valueHex == null || valueHex== "") valueHex
      else
        try { valueHex.toHexDecode }
        catch { case t: Throwable => { Log.error(s"error converting from hex valueHex=$valueHex", t); valueHex } }
    Log.debug(s"value=$value")
    value
  }

  def clear(key: String) = {
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot\\$key")
    val ret = WinRegistry.deleteKey(hkey, s"$keyRoot\\$key")
  }

  def filterJavaBugErrorLines[T](action: => T) =
    PrintStreamPlus.filterErrorLines(
      List(
        """java.util.prefs.WindowsPreferences <init>""",
        """WARNING: Could not open/create prefs root node Software\JavaSoft\Prefs at root 0x80000002. Windows RegCreateKeyEx(...) returned error code 5."""))(
        action)

}