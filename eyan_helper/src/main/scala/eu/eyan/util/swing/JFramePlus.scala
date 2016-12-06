package eu.eyan.util.swing

import javax.swing.JFrame
import com.jgoodies.forms.layout.FormLayout
import java.awt.Component
import com.jgoodies.forms.factories.CC
import eu.eyan.util.awt.AwtHelper
import java.awt.Toolkit

class JFramePlus(title: String, component: Component) extends JFrame {
  setTitle(title)
  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  setLayout(new FormLayout("fill:pref:grow", "fill:pref:grow"))
  add(component, CC.xy(1, 1))
  
  def packAndSetVisible = {pack; setVisible(true); this}
  def positionToCenter = AwtHelper.positionToCenter(this)
}