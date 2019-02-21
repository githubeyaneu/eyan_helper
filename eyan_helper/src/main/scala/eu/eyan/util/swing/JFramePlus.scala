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
import javax.swing.ImageIcon
import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.Font
import java.awt.Color
import javax.swing.JFrame
import eu.eyan.util.awt.ImagePlus
import eu.eyan.util.swing.JMenuBarPlus.JMenuBarImplicit
import javax.swing.JMenuItem
import java.awt.event.MouseEvent
import javax.swing.Icon
import eu.eyan.util.text.Text
import rx.lang.scala.subjects.BehaviorSubject
import java.awt.event.WindowEvent
import rx.lang.scala.Observable
import java.awt.TrayIcon.MessageType
import eu.eyan.log.Log

object JFramePlus {
  implicit class JFramePlusImplicit[TYPE <: JFrame](jFrame: TYPE) extends FramePlusImplicit(jFrame) {
    def onCloseDoNothing = defaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    def onCloseHide = defaultCloseOperation(WindowConstants.HIDE_ON_CLOSE)
    def onCloseDispose = defaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    def onCloseExit = defaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    def onCloseDisposeWithCondition(condition: TYPE => Boolean) = {
      onCloseDoNothing
      onWindowClosing(if (condition(jFrame)) jFrame.dispose)
    }

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

    def close = jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING))

    //TODO -> make it better...
    def iconFromChar(c: Char, color: Color = Color.GREEN.darker.darker) = {
      //      val off_Image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB)
      //      val g2 = off_Image.createGraphics.asInstanceOf[Graphics2D]
      //      g2.setFont(new Font(null, Font.BOLD, 256))
      //      g2.setColor(color)
      //      g2.drawString(c+"", 42, 219)
      //      //			ImageIO.write(off_Image.asInstanceOf[RenderedImage], "png", new File("""C:\temp\A.png"""))
      //      iconImage(off_Image)
      iconImage(ImagePlus.imageFromChar(c, color))
    }

    def iconImageFromResources(path: String) = iconImage(new ImageIcon(getClass.getResource(path)).getImage)

    def withComponent(c: Component) = component(c)
    def addFluent(c: Component) = { jFrame.add(c); jFrame }
    def addFluent(c: Component, o: Any) = { jFrame.add(c, o); jFrame }

    private def systemTray = SystemTray.getSystemTray

    private def removeAllSystemTrayIcons = systemTray.getTrayIcons.foreach(systemTray.remove(_))

    def createOpenExitPopup = {
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
    val NO_IMAGE: Image = null
    val NO_ICON: Icon = null
    def addToSystemTray(icon: Image = jFrame.getIconImage, toolTip: String = jFrame.getTitle, popupMenu: PopupMenu = createOpenExitPopup, minimizeMessageTitle: String = "", minimizeMessageText: String = "" ) = {
      if (SystemTray.isSupported()) {
        val iconToUse = if (icon != NO_IMAGE) icon else Toolkit.getDefaultToolkit().getImage("/dontexists.jpg")

        //TODO TrayIcon Implicit
        val trayIcon = new TrayIcon(iconToUse, toolTip, popupMenu)
        trayIcon.setImageAutoSize(true)
        trayIcon.addMouseListener(AwtHelper.onClicked(e => if (e.getButton == MouseEvent.BUTTON1) (if (jFrame.isVisible) jFrame.invisible else { jFrame.visible; jFrame.setState(Frame.NORMAL); jFrame.toFront })))
        systemTray.add(trayIcon)
        
        var userNotifiedOfMinimize = false
        def notifyUserOfMinimized = {
        		if (!userNotifiedOfMinimize && (minimizeMessageTitle.length >0 || minimizeMessageText.length >0)) trayIcon.displayMessage(minimizeMessageTitle, minimizeMessageText, MessageType.INFO)
        		userNotifiedOfMinimize = true
        }
        jFrame.onWindowIconified(notifyUserOfMinimized)
        jFrame.onWindowClosing(notifyUserOfMinimized)
        jFrame.onWindowStateChanged_ICONIFIED({ jFrame.invisible; notifyUserOfMinimized })
        jFrame.onWindowStateChanged_NORMAL(jFrame.visible)
        jFrame.onWindowStateChanged_MAXIMIZED_BOTH(jFrame.visible)
      }
      jFrame
    }

    val NO_JMENU_BAR: JMenuBar = null
    private def getOrCreateJMenuBar = { if (jFrame.getJMenuBar == NO_JMENU_BAR) jMenuBar(new JMenuBar()); jFrame.getJMenuBar }

    private def getOrCreateMenu(menuText: String) = getOrCreateJMenuBar.getOrCreateMenu(menuText)

    def menuItemSeparator(menuText: String) = { getOrCreateMenu(menuText).addSeparator; jFrame }

    def menuItems(menuText: String, menuItemTexts: Seq[String], action: String => Unit, icon: Icon = NO_ICON) = {
      def createMenuItem(menuItemText: String) = menuItemEvent(menuText, menuItemText, (frame: TYPE) => action(menuItemText), icon)
      menuItemTexts foreach createMenuItem
      jFrame
    }

    def menuItem(menuText: String, menuItemText: String, action: => Unit) = menuItemEvent(menuText: String, menuItemText, (frame: TYPE) => action, NO_ICON)

    def menuItemEvent(menuText: String, menuItemText: String, action: TYPE => Unit, icon: Icon) = {
      val menuItem = new JMenuItem(menuItemText)
      if (icon != NO_ICON) menuItem.setIcon(icon)
      getOrCreateMenu(menuText).add(menuItem)
      menuItem.onAction(action(jFrame))
      jFrame
    }

    val ALWAYS_ENABLED = BehaviorSubject(true)
    def menuItem(menuText: Text, menuItemText: Text, action: => Unit, enabledObservable: Observable[Boolean] = ALWAYS_ENABLED) = menuItemEvent(menuText, menuItemText, (frame: TYPE) => action, enabledObservable)
    def menuItemEvent(menuText: Text, menuItemText: Text, action: TYPE => Unit, enabledObservable: Observable[Boolean] = ALWAYS_ENABLED) = {
      // TODO JMenuItemPlus
      val menuItem = new JMenuItem(menuItemText.get)
      menuItem.setName(menuItemText.get)
      menuItemText.subscribe(menuItem.setText(_))
      //      if (icon != null) menuItem.setIcon(icon) // TODO
      getOrCreateMenu(menuText).add(menuItem)
      menuItem.onAction(action(jFrame))
      enabledObservable.subscribe(enabled => menuItem.setEnabled(enabled))
      jFrame
    }
    def menuItems(menuText: Text, menuItemTexts: Seq[String], action: String => Unit) = {
      def createMenuItem(menuItemText: String) = menuItemEvent(menuText, new Text(menuItemText) {}, (frame: TYPE) => action(menuItemText))
      menuItemTexts foreach createMenuItem
      jFrame
    }
    def menuItemSeparator(menuText: Text) = { getOrCreateMenu(menuText.get).addSeparator; jFrame }
    private def getOrCreateMenu(menuText: Text) = getOrCreateJMenuBar.getOrCreateMenu(menuText)

  }

  def close(frame: JFrame) = frame.close
}