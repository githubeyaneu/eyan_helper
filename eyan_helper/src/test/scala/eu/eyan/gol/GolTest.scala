package eu.eyan.gol

import eu.eyan.testutil.TestPlus
import org.junit.Test
import org.junit.runner.RunWith
import eu.eyan.testutil.ScalaEclipseJunitRunnerTheories

case class Gol(cells: Set[Cell] = Set()) {
  def add(cell: Cell) = new Gol(cells + cell)

  def neighbours(cell:Cell) = (for (dx <- -1 to 1; dy <- -1 to 1 if !(dx == 0 && dy == 0)) yield Cell(cell.x+dx, cell.y+dy)).toSet
  
  def survives(cell: Cell) = List(2, 3).contains(cells.intersect(neighbours(cell)).size)
  def reproduced(cell: Cell) = 3 == cells.intersect(neighbours(cell)).size
  
  def survivingCells = cells filter survives
  
  def reproductionCandidates = cells.map(neighbours).flatten -- cells 
  def reproduction = reproductionCandidates filter reproduced
  
  def nextGeneration = Gol(survivingCells ++ reproduction) 
}

case class Cell(x: Int, y: Int) {
}

@RunWith(classOf[ScalaEclipseJunitRunnerTheories])
class GolTest extends TestPlus {

  @Test def cellExists = Cell(2, 2)
  @Test def golExists = Gol()
  @Test def golCellCanBeAdded = Gol().add(Cell(2, 2))
  @Test def cellDies = Gol().add(Cell(2, 2)).nextGeneration.cells.isEmpty ==> true
  @Test def cellNeighbours = new Gol().neighbours(Cell(2, 2)) ==> Set(Cell(1, 1), Cell(2, 1), Cell(3, 1), Cell(1, 2), Cell(3, 2), Cell(1, 3), Cell(2, 3), Cell(3, 3))
  @Test def survives2 = Gol().add(Cell(2, 3)).add(Cell(3, 2)).add(Cell(4, 1)).nextGeneration.cells ==> Set(Cell(3, 2))
  @Test def survives3 = Gol().add(Cell(2, 2)).add(Cell(3, 1)).add(Cell(3, 3)).add(Cell(1, 1)).nextGeneration.cells.contains(Cell(2, 2)) ==> true
  @Test def reproduction = Gol().add(Cell(2, 2)).add(Cell(3, 2)).add(Cell(4, 2)).nextGeneration.cells ==> Set(Cell(3, 1),Cell(3, 2),Cell(3, 3))

}