package eu.eyan.util.awt

import eu.eyan.util.awt.WindowPlus.WindowPlusImplicit
import java.awt.Frame
import java.awt.MenuBar
import java.awt.Shape
import java.awt.Rectangle
import java.awt.Color
import eu.eyan.log.Log
import rx.lang.scala.subjects.BehaviorSubject
import rx.lang.scala.Observable

object FramePlus {
  implicit class FramePlusImplicit[TYPE <: Frame](frame: TYPE) extends WindowPlusImplicit(frame) {
    override def background(color: Color) = { frame.setBackground(color); frame }
    def extendedState(state: Int) = { frame.setExtendedState(state); frame }
    def maximizedBounds(rect: Rectangle) = { frame.setMaximizedBounds(rect); frame }
    def menuBar(menuBar: MenuBar) = { frame.setMenuBar(menuBar); frame }
    override def opacity(opacity: Float) = { frame.setOpacity(opacity); frame }
    def resizeable = withResizeable(true)
    def notResizeable = withResizeable(false)
    def withResizeable(resizeable: Boolean) = { frame.setResizable(resizeable); frame }
    override def shape(shape: Shape) = { frame.setShape(shape); frame }
    def state(state: Int) = { frame.setState(state); frame }
    def state_Iconified = this.state(Frame.ICONIFIED)
    def state_Normal = this.state(Frame.NORMAL)
    def title(title: String) = { frame.setTitle(title); frame }
    def undecorated = withUndecorated(true)
    def notUndecorated = withUndecorated(false)
    def withUndecorated(undecorated: Boolean) = { frame.setUndecorated(undecorated); frame }

    override def maximize = extendedState(Frame.MAXIMIZED_BOTH)

    // TODO: remove subscription if not visible: panel.addComponentListener ( new ComponentAdapter () { public void componentShown ( ComponentEvent e ) { System.out.println ( "Component shown" ); } public void componentHidden ( ComponentEvent e ) { System.out.println ( "Component hidden" ); } } );
    def title(titleObservable: Observable[String]) = {
      titleObservable.subscribe(title => frame.setTitle(title), Log.error, () => { Log.warn("should ever end?...") })
      frame
    }
  }
}