package eu.eyan.util.swing

import java.awt.Graphics
import javax.swing.plaf.basic.BasicTextFieldUI

/**
 * It supports a hint text function that is shown if no text is in the JTextFields
 *
 */
class HintTextFieldUI extends BasicTextFieldUI {

  var hint = ""

  override def paintSafely(g: Graphics) = {
    super.paintSafely(g)
    val comp = getComponent
    if (hint != null && comp.getText.length == 0) {
      g.setColor(comp.getForeground.brighter.brighter.brighter)
      val padding = (comp.getHeight - comp.getFont.getSize) / 2
      val hint_x_coord = 2
      g.drawString(hint, hint_x_coord, comp.getHeight - padding - 1)
    }
  }
}