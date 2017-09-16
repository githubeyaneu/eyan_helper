package eu.eyan.util.swing

import javax.swing.JEditorPane
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit
import javax.swing.event.HyperlinkEvent
import javax.swing.text.EditorKit
import java.io.InputStream
import java.net.URL

object JEditorPanePlus {
  implicit class JEditorPaneImplicit[TYPE <: JEditorPane](jEditorPane: TYPE) extends JTextComponentImplicit(jEditorPane) {
    def onHyperlinkUpdateEvent(action: HyperlinkEvent => Unit) = { jEditorPane.addHyperlinkListener(SwingPlus.onHyperlinkUpdate(action)); jEditorPane }
    def onHyperlinkUpdate(action: => Unit) = onHyperlinkUpdateEvent { e => action }

    def scrollToReference(reference: String) = { jEditorPane.scrollToReference(reference); jEditorPane }
    def contentType(typ: String) = { jEditorPane.setContentType(typ); jEditorPane }
    def editorKit(kit: EditorKit) = { jEditorPane.setEditorKit(kit); jEditorPane }
    def editorKitForContentType(typ: String, k: EditorKit) = { jEditorPane.setEditorKitForContentType(typ, k); jEditorPane }
    def page(url: String) = { jEditorPane.setPage(url); jEditorPane }
    def page(page: URL) = { jEditorPane.setPage(page); jEditorPane }
    override def text(t: String) = { jEditorPane.setText(t); jEditorPane }
  }
}