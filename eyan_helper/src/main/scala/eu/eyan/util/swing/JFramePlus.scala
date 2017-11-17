package eu.eyan.util.swing

import java.awt.Component

import com.jgoodies.forms.factories.CC
import com.jgoodies.forms.layout.FormLayout

import eu.eyan.util.awt.FramePlus.FramePlusImplicit
import javax.swing.JFrame
import javax.swing.JMenuBar
import javax.swing.WindowConstants
import java.awt.LayoutManager
import javax.swing.TransferHandler
import java.awt.Container
import javax.swing.JLayeredPane
import java.awt.Image
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.awt.Frame
import java.awt.PopupMenu
import java.awt.MenuItem
import eu.eyan.util.swing.JMenuItemPlus.JMenuItemImplicit
import eu.eyan.util.swing.AbstractButtonPlus.AbstractButtonImplicit
import eu.eyan.util.awt.AwtHelper

object JFramePlus {
  implicit class JFramePlusImplicit[TYPE <: JFrame](jFrame: TYPE) extends FramePlusImplicit(jFrame) {
    def onCloseDoNothing = defaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = defaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = defaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    def onCloseExit = defaultCloseOperation(JFrame.EXIT_ON_CLOSE)

    def defaultCloseOperation(operation: Int) = { jFrame.setDefaultCloseOperation(operation); jFrame }
    def jMenuBar(menubar: JMenuBar) = { jFrame.setJMenuBar(menubar); jFrame }
    def component(component: Component) = {
      jFrame.setLayout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
      jFrame.add(component, CC.xy(1, 1))
      jFrame
    }

    def contentPane(contentPane: Container) = { jFrame.setContentPane(contentPane); jFrame }
    def glassPane(glassPane: Component) = { jFrame.setGlassPane(glassPane); jFrame }
    def layeredPane(layeredPane: JLayeredPane) = { jFrame.setLayeredPane(layeredPane); jFrame }
    override def layout(manager: LayoutManager) = { jFrame.setLayout(manager); jFrame }
    def transferHandler(newHandler: TransferHandler) = { jFrame.setTransferHandler(newHandler); jFrame }
    override def iconImage(image: Image) = { jFrame.setIconImage(image); jFrame }

    def withComponent(c: Component) = component(c)

    private def systemTray = SystemTray.getSystemTray

    private def removeAllSystemTrayIcons = systemTray.getTrayIcons.foreach(systemTray.remove(_))

    private def createOpenExitPopup = {
      /* TODO create and use implicits for popupmenu and menuitem*/
      val popup = new PopupMenu()

      val open = new MenuItem("Open")
      open.addActionListener(AwtHelper.onActionPerformed(e => { jFrame.visible; jFrame.setExtendedState(Frame.NORMAL) }))
      popup.add(open)

      val exit = new MenuItem("Exit")

      exit.addActionListener(AwtHelper.onActionPerformed(e => { removeAllSystemTrayIcons; System.exit(0) }))
      popup.add(exit)

      popup
    }

    def addToSystemTray(icon: Image = Toolkit.getDefaultToolkit().getImage("/dontexists.jpg"), toolTip: String = jFrame.getTitle, popupMenu: PopupMenu = createOpenExitPopup) = {
      if (SystemTray.isSupported()) {
        //TODO TrayIcon Implicit
        val trayIcon = new TrayIcon(icon, toolTip, popupMenu)
        trayIcon.setImageAutoSize(true)
        trayIcon.addMouseListener(AwtHelper.onDoubleClick(e => jFrame.visible))
        systemTray.add(trayIcon)
        jFrame.onWindowStateChanged_ICONIFIED(jFrame.invisible)
        jFrame.onWindowStateChanged_NORMAL(jFrame.visible)
        jFrame.onWindowStateChanged_MAXIMIZED_BOTH(jFrame.visible)
      }
      jFrame
    }
  }
}