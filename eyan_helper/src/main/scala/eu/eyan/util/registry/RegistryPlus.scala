package eu.eyan.util.registry

import eu.eyan.log.Log
import eu.eyan.util.scala.TryCatch
import eu.eyan.util.string.StringPlus.StringPlusImplicit

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

  def write(name: String, value: String): Unit = write("", name, value)

  def read(name: String): String = read("", name)

  def write(key: String, name: String, value: String): Unit = {
    val valueHex = value.toHexEncode
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot, $name, $value, $valueHex")
    WinRegistry.createKey(hkey, s"$keyRoot\\$key")
    WinRegistry.writeStringValue(hkey, s"$keyRoot\\$key", name, valueHex, wow)
  }

  def read(key: String, name: String): String = {
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot\\$key, $name")
    val valueHex = WinRegistry.readString(hkey, s"$keyRoot\\$key", name, wow)
    val value = TryCatch(valueHex.toHexDecode, (t:Throwable) => { Log.error("error converting from hex"); valueHex })
    Log.debug(s"$valueHex $value")
    value
  }

  def clear(key: String) = {
    Log.debug(s"HKEY_CURRENT_USER, $keyRoot\\$key")
    val ret = WinRegistry.deleteKey(hkey, s"$keyRoot\\$key")
  }
}