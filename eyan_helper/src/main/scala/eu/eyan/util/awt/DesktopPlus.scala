package eu.eyan.util.awt

import java.io.FileWriter
import eu.eyan.log.Log

object DesktopPlus {
  def getWindowsCurrentUserDesktopPath = System.getenv("userprofile") + "/Desktop"

  def createInternetShortcutOnDesktop(name: String, target: String) = {
    Log.info((name, target))
    val path = getWindowsCurrentUserDesktopPath + "\\" + name + ".URL"
    createInternetShortcut(name, path, target, "")
  }

  def createInternetShortcutOnDesktop(name: String, target: String, icon: String) = {
    Log.info((name, target, icon))
    val path = getWindowsCurrentUserDesktopPath + "\\" + name + ".URL"
    createInternetShortcut(name, path, target, icon)
  }

  def createInternetShortcut(name: String, dir: String, url: String): Unit = {
    Log.info((name, dir, url))
    val path = dir.trim + "\\" + name.trim + ".url"
    createInternetShortcut(name, path, url, "")
  }

  private def createInternetShortcut(name: String, where: String, target: String, icon: String) = {
    Log.info((name, where, target, icon))
    val fw = new FileWriter(where, false)
    fw.write("[InternetShortcut]\r\n")
    fw.write("URL=" + target + "\r\n")
    if (icon != "") fw.write("IconFile=" + icon + "\n")
    fw.flush
    fw.close
  }
}