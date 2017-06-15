package eu.eyan.util.swing

import javax.swing.JComponent
import eu.eyan.util.awt.ContainerPlus.ContainerPlusImplicit

object JComponentPlus2 {
  implicit class JComponentImplicit2[TYPE <: JComponent](jComponent: TYPE) extends ContainerPlusImplicit(jComponent){
    
  }
}