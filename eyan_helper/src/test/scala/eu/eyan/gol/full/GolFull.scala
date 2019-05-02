package eu.eyan.gol.full

import eu.eyan.testutil.TestPlus
import org.junit.Test
import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import javax.swing.JPanel
import java.awt.Graphics
import javax.swing.JFrame
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JPanelPlus.JPanelImplicit

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class GolFullTemplate extends TestPlus {
//  @Test def cellExists = Cell(2, 3)
//  @Test def cellExistsX = Cell(2, 3).x === 2
//  @Test def cellExistsY = Cell(2, 3).y === 3
//  @Test def cellNeighbours = Cell(2, 2).neighbours === Set(Cell(1, 1), Cell(2, 1), Cell(3, 1), Cell(1, 2), Cell(3, 2), Cell(1, 3), Cell(2, 3), Cell(3, 3))
//
//  @Test def golExists = Gol(Set())
//  @Test def golHasAliveCells = Gol(Set(Cell(2, 2), Cell(2, 2), Cell(1, 1))).aliveCells === Set(Cell(1, 1), Cell(2, 2))
//
//  @Test def cellDies = Gol(Set(Cell(2, 2))).nextGeneration.aliveCells.isEmpty === true
//  @Test def survives2 = Gol(Set(Cell(2, 3), Cell(3, 2), Cell(4, 1))).nextGeneration.aliveCells === Set(Cell(3, 2))
//  @Test def survives3 = Gol(Set(Cell(2, 2), Cell(3, 1), Cell(3, 3), Cell(1, 1))).nextGeneration.aliveCells.contains(Cell(2, 2)) === true
//  @Test def reproduction = Gol(Set(Cell(2, 2), Cell(3, 2), Cell(4, 2))).nextGeneration.aliveCells === Set(Cell(3, 1), Cell(3, 2), Cell(3, 3))
//  @Test def overPopulate = Gol(Set(Cell(2, 2), Cell(1, 1), Cell(3, 1), Cell(3, 3), Cell(1, 3))).nextGeneration.aliveCells.contains(Cell(2, 2)) === false
}


// GUI
//object Gol extends App {
//  class GolPanel(var gol: Gol = Gol(Set())) extends JPanel {
//    def next = { this.gol = gol.nextGeneration; repaint() }
//
//    override def paint(g: Graphics) = {
//      g.clearRect(0, 0, getWidth, getHeight)
//      gol.aliveCells.foreach(drawCell)
//      def drawCell(cell: Cell) = g.fillRect(getWidth / 2 + cell.x * 10, getHeight / 2 + cell.y * 10, 10, 10)
//    }
//  }
//  
//  val oscillator = Set(Cell(-1, 0), Cell(0, 0), Cell(1, 0))
//  val glider = Set(Cell(5, 5), Cell(6, 6), Cell(7, 6), Cell(7, 5), Cell(7, 4))
//  def mirror(dx: Int, dy: Int)(cell: Cell) = Cell(cell.x * dx, cell.y * dy)
//  val startingGoL = Gol(oscillator ++ glider ++ glider.map(mirror(1, -1)) ++ glider.map(mirror(-1, 1)) ++ glider.map(mirror(-1, -1)))

//  val golPanel = new GolPanel(startingGoL)
//  golPanel.onMouseReleased(golPanel.next)
//  new JFrame().withComponent(golPanel).onCloseExit.packAndSetVisible.maximize
//}