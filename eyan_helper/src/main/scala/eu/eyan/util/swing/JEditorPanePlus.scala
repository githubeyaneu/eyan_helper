package eu.eyan.util.swing

import javax.swing.JEditorPane
import eu.eyan.util.swing.JTextComponentPlus.JTextComponentImplicit

object JEditorPanePlus {
  implicit class JEditorPaneImplicit[TYPE <: JEditorPane](jEditorPane: TYPE) extends JTextComponentImplicit(jEditorPane){
    //addHyperlinkListener(HyperlinkListener)
    //fireHyperlinkUpdate(HyperlinkEvent)
    //getUIClassID()
    //read(InputStream, Object)
    //removeHyperlinkListener(HyperlinkListener)
    //replaceSelection(String)
    //scrollToReference(String)
    //setContentType(String)
    //setEditorKit(EditorKit)
    //setEditorKitForContentType(String, EditorKit)
    //setPage(String)
    //setPage(URL)
    //setText(String)    
  }
}