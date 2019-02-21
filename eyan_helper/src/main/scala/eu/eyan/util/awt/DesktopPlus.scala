package eu.eyan.util.awt

import java.io.FileWriter

object DesktopPlus {
  def getWindowsCurrentUserDesktopPath = System.getenv("userprofile") + "/Desktop"

  def createInternetShortcutOnDesktop(name: String, target: String) = {
    val path = getWindowsCurrentUserDesktopPath + "/" + name + ".URL"
    createInternetShortcut(name, path, target, "")
  }

  def createInternetShortcutOnDesktop(name: String, target: String, icon: String) = {
    val path = getWindowsCurrentUserDesktopPath + "/" + name + ".URL"
    createInternetShortcut(name, path, target, icon)
  }

  def createInternetShortcut(name: String, dir: String, url: String): Unit = {
    println(("createInternetShortcut", name, dir, url))
    val path = dir + "/" + name + ".url"
    createInternetShortcut(name, path, url, "")
  }

  private def createInternetShortcut(name: String, where: String, target: String, icon: String) = {
    println(("createInternetShortcut", name, where, target, icon))
    val fw = new FileWriter(where, false)
    fw.write("[InternetShortcut]\r\n")
    fw.write("URL=" + target + "\r\n")
    if (icon != "") fw.write("IconFile=" + icon + "\n")
    fw.flush
    fw.close
  }
}