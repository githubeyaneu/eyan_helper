package eu.eyan.log

import java.awt.Component
import java.awt.Dialog.ModalExclusionType

import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.JFrame
import javax.swing.WindowConstants
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import eu.eyan.util.swing.LimitLinesDocumentListener
import java.io.OutputStream
import eu.eyan.util.io.OutputStreamPlus
import java.util.Timer
import java.util.TimerTask
import eu.eyan.util.io.PrintStreamPlus.PrintStreamImplicit
import rx.lang.scala.Subscription
import eu.eyan.util.swing.JButtonPlus.JButtonImplicit

// FIXME: too many lines in textarea -> goes bad. show only configurable amount!
object LogWindow {
  private lazy val window = new LogWindow
  private var logsObservableSubscription: Subscription = _
  private var levelObservableSubscription: Subscription = _

  def show(origin: Component = null) = {
    window.frame.packAndSetVisible
    window.frame.positionToRight
    if (origin != null) origin.positionToLeft

    def onErrorLogLevel(msg: String)(t: Throwable) = Log.error(s"$msg error", t)
    def onCompletedLogLevel(msg: String) = Log.error(s"$msg onCompleted")

    // FIXME subscribe only once!! show can be started more times!!!
    def onNextLog(log: Log) = LogWindow.add(Log.logToConsoleText(log))
    if (logsObservableSubscription == null)
      logsObservableSubscription = Log.logsObservable.subscribe(onNextLog, onErrorLogLevel("logsObservable"), () => onCompletedLogLevel("logsObservable")) // TODO why map does not work?

    def onNextLogLevel(level: LogLevel) = LogWindow.setLevel(level)
    if (levelObservableSubscription == null)
      levelObservableSubscription = Log.levelObservable.subscribe(onNextLogLevel, onErrorLogLevel("levelObservable"), () => onCompletedLogLevel("levelObservable"))
  }

  def add(text: String) = if (window != null && window.frame != null) window.textArea.append(text + "\n")

  def outStreamAppender = OutputStreamPlus.carriageReturnReplacer(OutputStreamPlus.timebuffered(s => window.outTextArea.append(s)))
  def errStream = OutputStreamPlus.carriageReturnReplacer(OutputStreamPlus.timebuffered(s => window.errTextArea.append(s)))

  def setLevel(level: LogLevel) = if (window != null) window.level(level)

  def close() = window.frame.dispose

  def logs = window.textArea.getText
  def logsOut = window.outTextArea
  def logsErr = window.errTextArea.getText

  //TODO refactor: special logic to exchange system.out and system.err with the copy algo do it for Console.out too
  // then refact to more fp way: replaceSystemOut(outStreamAppender.carriageReturnReplacer.timebuffered)
  private def redirectSystemOut = { System.setOut(System.out.copyToStream(outStreamAppender)); Log.logToConsole = false; this }
  private def redirectSystemError = { System.setErr(System.err.copyToStream(errStream)); Log.errToConsole = false; this }
  def redirectSystemOutAndErrToLogWindow = { redirectSystemOut; redirectSystemError }

  def getAllLogs = {
    val loggerLogs = "Logger logs:\r\n" + logs
    val out = "System.out:\r\n" + logsOut
    val err = "System.err:\r\n" + logsErr
    val logSum = List(loggerLogs, out, err).mkString("\r\n\r\n")
    logSum
  }
}

class LogWindow {
  val DEFAULT_WIDTH = 800
  val DEFAULT_HEIGHT = 600
  def level(level: LogLevel) = frame.title("Log " + level)

  val content = new JPanelWithFrameLayout().withBorders.withSeparators.newColumn("f:1px:g")

  val buttons = content.addPanelWithFormLayout().withSeparators.newColumn()
  buttons.addButton("Clear").onAction_disableEnable({ textArea.setText(""); outTextArea.setText(""); errTextArea.setText("") })

  List(None, Fatal, Error, Warn, Info, Debug, Trace).foreach(level => buttons.newColumn.addButton(level.toString).onAction_disableEnable(Log.activate(level)))

  buttons.newColumn.addLabel("max rows:")
  val maxRowsTf = buttons.newColumn.addTextField("10000", 6).rememberValueInRegistry("LogWindowMaxRows").onTextChanged(limitLines.setLimitLines(maxRows))

  def maxRows: Int = try { maxRowsTf.getText.toInt } catch { case _: Throwable => 1000 }

  content.newRow.addSeparatorWithTitle("Logs")
  val textArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console out")
  val outTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console err")
  val errTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown

  val limitLines = new LimitLinesDocumentListener(maxRows)
  textArea.getDocument.addDocumentListener(limitLines)
  outTextArea.getDocument.addDocumentListener(limitLines)
  errTextArea.getDocument.addDocumentListener(limitLines)

  val frame = new JFrame("").withComponent(content).size(DEFAULT_WIDTH, DEFAULT_HEIGHT).onCloseDispose.modalExclusionType_ApplicationExclude.name(classOf[LogWindow].getName)
}



/*
// TODO: too many lines in textarea -> goes bad. show only configurable amount!

package eu.eyan.log

import java.awt.Component
import java.awt.Dialog.ModalExclusionType

import eu.eyan.util.awt.ComponentPlus.ComponentPlusImplicit
import eu.eyan.util.swing.JPanelWithFrameLayout
import javax.swing.JFrame
import javax.swing.WindowConstants
import eu.eyan.util.swing.JTextAreaPlus.JTextAreaImplicit
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JTextFieldPlus.JTextFieldPlusImplicit
import scala.collection.mutable.MutableList
import javax.swing.JTextArea
import java.util.Timer
import java.util.TimerTask
import eu.eyan.util.swing.SwingPlus

object LogWindow {
  val window = new LogWindowImpl

  val logList = MutableList[String]()
  val outList = MutableList[String]("")
  val errList = MutableList[String]("")
  val outTimer = new Timer("outTimer")
  val errTimer = new Timer("errTimer")

  val outTask = new TimerTask() { override def run() = { window.updateOut(outList) } }
  val errTask = new TimerTask() { override def run() = { window.updateErr(errList) } }
  outTimer.scheduleAtFixedRate(outTask,300, 300) 
  errTimer.scheduleAtFixedRate(errTask, 300, 300) 
 
  def show(origin: Component = null) = {
    window.frame.packAndSetVisible
    window.frame.positionToRight
    if (origin != null) origin.positionToLeft
  }

  def windowOK = window != null && window.frame != null
  def add(text: String) = if (windowOK && logList != null) { logList += text; window.updateLogs(logList) }
  def addToOut(c: Int) = if (windowOK && outList != null) { addChar(c.toChar, outList); /*outTimer.schedule(outTask, 1000)*/ }
  def addToErr(c: Int) = if (windowOK && errList != null) { addChar(c.toChar, errList);/* errTimer.cancel; errTimer.schedule(errTask, 1000)*/ }

  def addChar(c: Character, list: MutableList[String]) = {
    if (list.last.endsWith("\n")) list += ""
    list.update(list.length - 1, list.last + c)
  }

  def setLevel(level: LogLevel) = if (window != null) window.level(level)

  def close() = window.frame.dispose

  def logs = logList.mkString("\r\n")
  def logsOut = outList.mkString("\r\n")
  def logsErr = errList.mkString("\r\n")
}

class LogWindowImpl {
  val DEFAULT_WIDTH = 800
  val DEFAULT_HEIGHT = 600
  def level(level: LogLevel) = frame.title("Log " + level)

  val content = new JPanelWithFrameLayout().withBorders.withSeparators.newColumn("f:1px:g")

  val buttons = content.addPanelWithFormLayout().withSeparators.newColumn()
  buttons.addButton("Clear").onAction_disableEnable({ textArea.setText(""); outTextArea.setText(""); errTextArea.setText("") })
  List(None, Fatal, Error, Warn, Info, Debug, Trace).foreach(
    level => buttons.newColumn.addButton(level.toString).onAction_disableEnable(Log.activate(level)))
  buttons.newColumn.addLabel("max rows:")
  val maxRowsTf = buttons.newColumn.addTextField("1000", 6).rememberValueInRegistry("LogWindowMaxRows")

  def maxRows = try { maxRowsTf.getText.toInt } catch { case _: Throwable => 1000 }

  def updateTextArea(ta: JTextArea, list: Seq[String]) = {
    SwingPlus.invokeLater(textArea.setText(list.takeRight(maxRows).mkString("\n")))
//    textArea.setText("")
    //    textArea.append(text + "\n")
    //    textArea.invalidate()
    //    textArea.repaint()
    //    textArea.validate()
  }
  def updateLogs(list: Seq[String]) = updateTextArea(textArea, list)
  def updateOut(list: Seq[String]) = updateTextArea(outTextArea, list)
  def updateErr(list: Seq[String]) = updateTextArea(errTextArea, list)

  content.newRow.addSeparatorWithTitle("Logs")
  private val textArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console out")
  private val outTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown
  content.newRow.addSeparatorWithTitle("Console err")
  private val errTextArea = content.newRow("f:1px:g").addTextArea().alwaysScrollDown

  val frame = new JFrame("").withComponent(content).size(DEFAULT_WIDTH, DEFAULT_HEIGHT).onCloseDispose.modalExclusionType_ApplicationExclude
}
*/
