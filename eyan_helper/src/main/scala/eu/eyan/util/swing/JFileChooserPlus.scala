package eu.eyan.util.swing

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Component
import java.io.File

object JFileChooserPlus {

  implicit class JFileChooserImplicit(fileChooser: JFileChooser) {

    def withCurrentDirectory(path: String) = { fileChooser.setCurrentDirectory(new File(path)); fileChooser }

    def withDialogTitle(title: String) = { fileChooser.setDialogTitle(title); fileChooser }

    def withApproveButtonText(text: String) = { fileChooser.setApproveButtonText(text); fileChooser }

    def withFileFilter(extension: String, descriptionOfExtension: String) =
      { fileChooser.setFileFilter(new FileNameExtensionFilter(descriptionOfExtension, extension)); fileChooser }

    def showAndHandleResult(parent: Component, actionOnApprove: File => Unit, actionOnCancel: () => Unit = () => {}, actionOnError: () => Unit = () => {}) = {
      val result = fileChooser.showOpenDialog(parent)
      result match {
        case JFileChooser.APPROVE_OPTION => actionOnApprove(fileChooser.getSelectedFile)
        case JFileChooser.CANCEL_OPTION  => actionOnCancel()
        case JFileChooser.ERROR_OPTION   => actionOnError()
        case _                           => actionOnError()
      }
      this
    }
  }
}