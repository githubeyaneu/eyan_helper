package eu.eyan.gol

import eu.eyan.testutil.TestPlus
import org.junit.Test
import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories
import javax.swing.JPanel
import java.awt.Graphics
import javax.swing.JFrame
import eu.eyan.util.swing.JFramePlus.JFramePlusImplicit
import eu.eyan.util.swing.JPanelPlus.JPanelImplicit
import java.awt.event.MouseEvent
import java.awt.Point

case class Gol(aliveCells: Set[Cell]) {
  def nextGeneration = Gol(aliveCellsWithTwoAliveNeighbours ++ aliveCellsAndCandidatesWithThreeNeighbours)
  def toggle(cell: Cell) = if (aliveCells.contains(cell)) Gol(aliveCells - cell) else Gol(aliveCells + cell)

  private def aliveCellsWithTwoAliveNeighbours = aliveCells filter aliveNeighboursCount(2)
  private def aliveCellsAndCandidatesWithThreeNeighbours = cellsWithAllNeighbours filter aliveNeighboursCount(3)

  private def aliveNeighboursCount(expectedCount: Int)(cell: Cell) = cell.neighboursIntersection(aliveCells).size == expectedCount

  private def cellsWithAllNeighbours = aliveCells.flatMap(_.neighbours)
}

case class Cell(x: Int, y: Int) {
  def neighboursIntersection(cells: Set[Cell]) = cells intersect neighbours

  def neighbours = (for (dx <- -1 to 1; dy <- -1 to 1 if !(dx == 0 && dy == 0)) yield Cell(x + dx, y + dy)).toSet
}

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class GolTest extends TestPlus {
  @Test def cellExists = Cell(2, 3)
  @Test def cellExistsX = Cell(2, 3).x === 2
  @Test def cellExistsY = Cell(2, 3).y === 3
  @Test def cellNeighbours = Cell(2, 2).neighbours === Set(Cell(1, 1), Cell(2, 1), Cell(3, 1), Cell(1, 2), Cell(3, 2), Cell(1, 3), Cell(2, 3), Cell(3, 3))

  @Test def golExists = Gol(Set())
  @Test def golHasAliveCells = Gol(Set(Cell(2, 2), Cell(2, 2), Cell(1, 1))).aliveCells === Set(Cell(1, 1), Cell(2, 2))

  @Test def cellDies = Gol(Set(Cell(2, 2))).nextGeneration.aliveCells.isEmpty === true
  @Test def survives2 = Gol(Set(Cell(2, 3), Cell(3, 2), Cell(4, 1))).nextGeneration.aliveCells === Set(Cell(3, 2))
  @Test def survives3 = Gol(Set(Cell(2, 2), Cell(3, 1), Cell(3, 3), Cell(1, 1))).nextGeneration.aliveCells.contains(Cell(2, 2)) === true
  @Test def reproduction = Gol(Set(Cell(2, 2), Cell(3, 2), Cell(4, 2))).nextGeneration.aliveCells === Set(Cell(3, 1), Cell(3, 2), Cell(3, 3))
  @Test def overPopulate = Gol(Set(Cell(2, 2), Cell(1, 1), Cell(3, 1), Cell(3, 3), Cell(1, 3))).nextGeneration.aliveCells.contains(Cell(2, 2)) === false
}

class GolPanel(var gol: Gol = Gol(Set())) extends JPanel {
  def next() = { this.gol = gol.nextGeneration; repaint() }
  def toggle(p: Point) = { this.gol = gol.toggle(Cell((p.x - getWidth / 2) / 10 + (if (p.x - getWidth / 2 < 0) -1 else 0), (p.y - getHeight / 2) / 10 + (if (p.y - getHeight / 2 < 0) -1 else 0))); repaint() }

  override def paint(g: Graphics) = {
    g.clearRect(0, 0, getWidth, getHeight)
    gol.aliveCells foreach drawCell
    def drawCell(cell: Cell) = g.fillRect(getWidth / 2 + cell.x * 10, getHeight / 2 + cell.y * 10, 10, 10)
  }
}

object GolReferenceImplementation extends App {
  val oscillator = Set(Cell(-1, 0), Cell(0, 0), Cell(1, 0))
  val glider = Set(Cell(5, 5), Cell(6, 6), Cell(7, 6), Cell(7, 5), Cell(7, 4))
  def mirror(dx: Int, dy: Int)(cell: Cell) = Cell(cell.x * dx, cell.y * dy)
  val startingGoL = Gol(oscillator ++ glider ++ glider.map(mirror(1, -1)) ++ glider.map(mirror(-1, 1)) ++ glider.map(mirror(-1, -1)))
  val golPanel = new GolPanel(startingGoL)
  golPanel.onMouseReleasedEvent(e => if (e.getButton == MouseEvent.BUTTON1) golPanel.toggle(e.getPoint))
  golPanel.onMouseRightReleased(golPanel.next())
  new JFrame().withComponent(golPanel).onCloseExit.packAndSetVisible.maximize
}