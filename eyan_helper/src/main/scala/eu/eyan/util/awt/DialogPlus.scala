package eu.eyan.util.awt

import java.awt.Dialog
import eu.eyan.util.awt.WindowPlus.WindowPlusImplicit
import java.awt.Dialog.ModalityType
import java.awt.Shape

object DialogPlus {
  implicit class DialogPlusImplicit[TYPE <: Dialog](dialog: TYPE) extends WindowPlusImplicit(dialog) {
    def modal = withModal(true)
    def notModal = withModal(false)
    def withModal(modal:Boolean) = { dialog.setModal(modal); dialog }

    def modality_Modeless = { dialog.setModalityType(ModalityType.MODELESS); dialog }
    def modality_DocumentModal = { dialog.setModalityType(ModalityType.DOCUMENT_MODAL); dialog }
    def modality_ApplicationModal = { dialog.setModalityType(ModalityType.APPLICATION_MODAL); dialog }
    def modality_ToolkitModal = { dialog.setModalityType(ModalityType.TOOLKIT_MODAL); dialog }

    override def opacity(opacity: Float) = { dialog.setOpacity(opacity); dialog }

    def resizeable = withResizeable(true)
    def notResizeable = withResizeable(false)
    def withResizeable(resizeable:Boolean) = { dialog.setResizable(resizeable); dialog }

    override def shape(shape: Shape) = { dialog.setShape(shape); dialog }

    def title(title: String) = { dialog.setTitle(title); dialog }

    def undecorated = withUndecorated(true)
    def notUndecorated = withUndecorated(false)
    def withUndecorated(undecorated:Boolean) = { dialog.setUndecorated(undecorated); dialog }
  }
}