package eu.eyan.util.swing

import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.Component
import java.io.File
import eu.eyan.util.swing.JComponentPlus.JComponentImplicit

object JFileChooserPlus {

  implicit class JFileChooserImplicit[TYPE <: JFileChooser](jFileChooser: TYPE) extends JComponentImplicit(jFileChooser) {
    //accept(File)
    //addActionListener(ActionListener)
    //addChoosableFileFilter(FileFilter)
    //approveSelection()
    //cancelSelection()
    //changeToParentDirectory()
    //ensureFileIsVisible(File)
    //removeActionListener(ActionListener)
    //removeChoosableFileFilter(FileFilter)
    //rescanCurrentDirectory()
    //resetChoosableFileFilters()
    //setAcceptAllFileFilterUsed(boolean)
    //setAccessory(JComponent)
    //setApproveButtonMnemonic(char)
    //setApproveButtonMnemonic(int)
    //setApproveButtonText(String)
    //setApproveButtonToolTipText(String)
    //setControlButtonsAreShown(boolean)
    //setCurrentDirectory(File)
    //setDialogTitle(String)
    //setDialogType(int)
    //setDragEnabled(boolean)
    //setFileFilter(FileFilter)
    //setFileHidingEnabled(boolean)
    //setFileSelectionMode(int)
    //setFileSystemView(FileSystemView)
    //setFileView(FileView)
    //setMultiSelectionEnabled(boolean)
    //setSelectedFile(File)
    //setSelectedFiles(File[])
    //showDialog(Component, String)
    //showOpenDialog(Component)
    //showSaveDialog(Component)
    //updateUI()



    def withCurrentDirectory(path: String) = { jFileChooser.setCurrentDirectory(new File(path)); jFileChooser }

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