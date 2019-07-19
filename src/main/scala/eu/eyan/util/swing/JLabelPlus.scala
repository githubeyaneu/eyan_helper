package eu.eyan.util.swing

import javax.swing.JLabel
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import javax.swing.Icon
import javax.swing.plaf.LabelUI
import java.awt.Component
import javax.swing.SwingConstants
import javax.swing.ImageIcon
import eu.eyan.util.awt.ImagePlus
import java.awt.Color
import eu.eyan.util.text.Text
import rx.lang.scala.Observable

object JLabelPlus {
  implicit class JLabelImplicit[TYPE <: JLabel](jLabel: TYPE) extends JComponentImplicit(jLabel) {
    def disabledIcon(icon: Icon) = { jLabel.setDisabledIcon(icon); jLabel }
    def displayedMnemonic(char: Char) = { jLabel.setDisplayedMnemonic(char); jLabel }
    def displayedMnemonic(key: Int) = { jLabel.setDisplayedMnemonic(key); jLabel }
    def displayedMnemonicIndex(index: Int) = { jLabel.setDisplayedMnemonicIndex(index); jLabel }
    def horizontalAlignment(alignment: Int) = { jLabel.setHorizontalAlignment(alignment); jLabel }
    def horizontalAlignment_Left = horizontalAlignment(SwingConstants.LEFT)
    def horizontalAlignment_Center = horizontalAlignment(SwingConstants.CENTER)
    def horizontalAlignment_Right = horizontalAlignment(SwingConstants.RIGHT)
    def horizontalAlignment_Leading = horizontalAlignment(SwingConstants.LEADING)
    def horizontalAlignment_Trailing = horizontalAlignment(SwingConstants.TRAILING)
    def horizontalTextPosition(textPosition: Int) = { jLabel.setHorizontalTextPosition(textPosition); jLabel }
    def horizontalTextPosition_Left = horizontalTextPosition(SwingConstants.LEFT)
    def horizontalTextPosition_Center = horizontalTextPosition(SwingConstants.CENTER)
    def horizontalTextPosition_Right = horizontalTextPosition(SwingConstants.RIGHT)
    def horizontalTextPosition_Leading = horizontalTextPosition(SwingConstants.LEADING)
    def horizontalTextPosition_Trailing = horizontalTextPosition(SwingConstants.TRAILING)
    def icon(icon: Icon) = { jLabel.setIcon(icon); jLabel }
    def iconTextGap(iconTextGap: Int) = { jLabel.setIconTextGap(iconTextGap); jLabel }
    def labelFor(component: Component) = { jLabel.setLabelFor(component); jLabel }
    def text(text: String) = { if(jLabel.getText != text) jLabel.setText(text); jLabel }
    def ui(ui: LabelUI) = { jLabel.setUI(ui); jLabel }
    def verticalAlignment(alignment: Int) = { jLabel.setVerticalAlignment(alignment); jLabel }
    def verticalAlignment_Top = verticalAlignment(SwingConstants.TOP)
    def verticalAlignment_Center = verticalAlignment(SwingConstants.CENTER)
    def verticalAlignment_Bottom = verticalAlignment(SwingConstants.BOTTOM)
    def verticalTextPosition(textPosition: Int) = { jLabel.setVerticalTextPosition(textPosition); jLabel }
    def verticalTextPosition_Top = verticalTextPosition(SwingConstants.TOP)
    def verticalTextPosition_Center = verticalTextPosition(SwingConstants.CENTER)
    def verticalTextPosition_Bottom = verticalTextPosition(SwingConstants.BOTTOM)

    def iconFromChar(c:Char, color:Color=Color.BLACK) = { jLabel.setIcon(new ImageIcon(ImagePlus.imageFromChar(c, color, 12,12))); jLabel }
    
    def text(text: Observable[String]) = {
      text.subscribe(jLabel.setText _)
      jLabel
    }
  }
}