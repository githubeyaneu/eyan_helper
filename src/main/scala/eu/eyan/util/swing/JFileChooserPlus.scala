package eu.eyan.util.swing

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Component
import java.io.File
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit
import eu.eyan.util.awt.AwtHelper
import java.awt.event.ActionEvent
import javax.swing.JComponent
import javax.swing.filechooser.FileSystemView
import javax.swing.filechooser.FileView
import javax.swing.filechooser.FileFilter

object JFileChooserPlus {

  implicit class JFileChooserImplicit[TYPE <: JFileChooser](jFileChooser: TYPE) extends JComponentImplicit(jFileChooser) {

    def onActionPerformedEvent(action: ActionEvent => Unit) = { jFileChooser.addActionListener(AwtHelper.onActionPerformed(action)); jFileChooser }
    def onActionPerformed(action: => Unit) = onActionPerformedEvent { e => action }

    def choosableFileFilter(filter: FileFilter): TYPE = { jFileChooser.addChoosableFileFilter(filter); jFileChooser }
    def acceptAllFileFilterUsed(b: Boolean) = { jFileChooser.setAcceptAllFileFilterUsed(b); jFileChooser }
    def accessory(newAccessory: JComponent) = { jFileChooser.setAccessory(newAccessory); jFileChooser }
    def approveButtonMnemonic(mnemonic: Char) = { jFileChooser.setApproveButtonMnemonic(mnemonic); jFileChooser }
    def approveButtonMnemonic(mnemonic: Int) = { jFileChooser.setApproveButtonMnemonic(mnemonic); jFileChooser }
    def approveButtonText(approveButtonText: String) = { jFileChooser.setApproveButtonText(approveButtonText); jFileChooser }
    def approveButtonToolTipText(toolTipText: String) = { jFileChooser.setApproveButtonToolTipText(toolTipText); jFileChooser }
    def controlButtonsAreShown(b: Boolean) = { jFileChooser.setControlButtonsAreShown(b); jFileChooser }
    def controlButtonsAreShown: TYPE = controlButtonsAreShown(true)
    def currentDirectory(dir: File) = { jFileChooser.setCurrentDirectory(dir); jFileChooser }
    def dialogTitle(dialogTitle: String) = { jFileChooser.setDialogTitle(dialogTitle); jFileChooser }
    def dialogType(dialogType: Int) = { jFileChooser.setDialogType(dialogType); jFileChooser }
    def dialogType_openDialog = dialogType(JFileChooser.OPEN_DIALOG)
    def dialogType_saveDialog = dialogType(JFileChooser.SAVE_DIALOG)
    def dialogType_customDialog = dialogType(JFileChooser.CUSTOM_DIALOG)
    def dragEnabled(b: Boolean) = { jFileChooser.setDragEnabled(b); jFileChooser }
    def dragEnabled: TYPE = dragEnabled(true)
    def dragDisabled: TYPE = dragEnabled(false)
    def fileFilter(filter: FileFilter) = { jFileChooser.setFileFilter(filter); jFileChooser }
    def fileHidingEnabled(b: Boolean) = { jFileChooser.setFileHidingEnabled(b); jFileChooser }
    def fileHidingEnabled: TYPE = fileHidingEnabled(true)
    def fileHidingDisabled = fileHidingEnabled(false)
    def fileSelectionMode(mode: Int) = { jFileChooser.setFileSelectionMode(mode); jFileChooser }
    def fileSelectionMode_filesOnly = fileSelectionMode(JFileChooser.FILES_ONLY)
    def fileSelectionMode_directoriesOnly = fileSelectionMode(JFileChooser.DIRECTORIES_ONLY)
    def fileSelectionMode_filesAndDirectories = fileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)
    def fileSystemView(fsv: FileSystemView) = { jFileChooser.setFileSystemView(fsv); jFileChooser }
    def fileView(fileView: FileView) = { jFileChooser.setFileView(fileView); jFileChooser }
    def multiSelectionEnabled(b: Boolean) = { jFileChooser.setMultiSelectionEnabled(b); jFileChooser }
    def multiSelectionEnabled: TYPE = multiSelectionEnabled(true)
    def multiSelectionDisabled: TYPE = multiSelectionEnabled(false)
    def selectedFile(file: File) = { jFileChooser.setSelectedFile(file); jFileChooser }
    def selectedFiles(files: Array[File]) = { jFileChooser.setSelectedFiles(files); jFileChooser }
    def showDialog(parent: Component, approveButtonText: String) = { jFileChooser.showDialog(parent, approveButtonText); jFileChooser }
    def showOpenDialog(parent: Component) = { jFileChooser.showOpenDialog(parent); jFileChooser }
    def showSaveDialog(parent: Component) = { jFileChooser.showSaveDialog(parent); jFileChooser }
    def updateUI = { jFileChooser.updateUI(); jFileChooser }

    def withCurrentDirectory(path: String) = { jFileChooser.setCurrentDirectory(new File(path)); jFileChooser }
    def withCurrentDirectory(path: File) = { jFileChooser.setCurrentDirectory(path); jFileChooser }

    def withDialogTitle(title: String) = { jFileChooser.setDialogTitle(title); jFileChooser }

    def withApproveButtonText(text: String) = { jFileChooser.setApproveButtonText(text); jFileChooser }

    def withFileFilter(extension: String, descriptionOfExtension: String) =
      { jFileChooser.setFileFilter(new FileNameExtensionFilter(descriptionOfExtension, extension)); jFileChooser }

    def showAndHandleResult(parent: Component, actionOnApprove: File => Unit, actionOnCancel: () => Unit = () => {}, actionOnError: () => Unit = () => {}) = {
      val result = jFileChooser.showOpenDialog(parent)
      result match {
        case JFileChooser.APPROVE_OPTION => actionOnApprove(jFileChooser.getSelectedFile)
        case JFileChooser.CANCEL_OPTION  => actionOnCancel()
        case JFileChooser.ERROR_OPTION   => actionOnError()
        case _                           => actionOnError()
      }
      this
    }
  }
}