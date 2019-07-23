package eu.eyan.util.swing

import javax.swing.event.DocumentListener
import javax.swing.text.BadLocationException
import javax.swing.text.Element
import javax.swing.event.DocumentEvent
import javax.swing.text.Document


//FIXME: this is very slow if dbg level on and lot of logs come (convert files)
class LimitLinesDocumentListener(var maximumLines: Int, var isRemoveFromStart: Boolean = true) extends DocumentListener {

  def setLimitLines(max: Int) =
    if (maximumLines < 1)
      throw new IllegalArgumentException("Maximum lines must be greater than 0")
    else this.maximumLines = max

  def insertUpdate(e: DocumentEvent) = SwingPlus.invokeLater(removeLines(e))
  def removeUpdate(e: DocumentEvent) = {}
  def changedUpdate(e: DocumentEvent) = {}

  private def removeLines(e: DocumentEvent) = {
    val document = e.getDocument
    val root = document.getDefaultRootElement

    while (root.getElementCount > maximumLines)
      if (isRemoveFromStart) removeFromStart(document, root)
      else removeFromEnd(document, root)
  }

  private def removeFromStart(document: Document, root: Element) = {
    val line = root.getElement(0)
    val end = line.getEndOffset

    try { document.remove(0, end) } catch { case ble: BadLocationException => System.out.println(ble) }
  }

  private def removeFromEnd(document: Document, root: Element) = {
    val line = root.getElement(root.getElementCount - 1)
    val start = line.getStartOffset
    val end = line.getEndOffset

    try { document.remove(start - 1, end - start) } catch { case ble: BadLocationException => System.out.println(ble) }
  }
}