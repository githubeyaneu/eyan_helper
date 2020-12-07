package eu.eyan

import org.junit.Test
import org.fest.assertions.Assertions._

trait Field
trait Player
object O extends Field with Player
object X extends Field with Player
//object E extends Field

class RowOrCol(nr:Int)
case class Col(nr:Int) extends RowOrCol (nr)
case class Row(nr:Int) extends RowOrCol (nr)
case class Position(col:Col, row:Row)

case class Board(fields: Array[Array[Field]])

class Game {
  def step(position:Position, player: Player): Game = GameOver("initial")
}
case class GameOver(cause: String) extends Game
//case class Step(x:Int, y:Int, player:Player)

case class GamePlaying(board: Map[Position, Player] = Map()) extends Game {
  override def step(position:Position, player: Player) = {
    val newBoard = board.updated(position, player)
    
    if(board.contains(position)) GameOver("alreadyTaken")
    else if(newBoard.keySet.filter(_.col == position.col).size==3) GameOver("colWins")
    else if(newBoard.keySet.filter(_.row == position.row).size==3) GameOver("rowWins")
    else if(newBoard.keySet.filter(mapPos => mapPos.row.nr == mapPos.col.nr).size==3) GameOver("diagonalWins")
    else if(newBoard.keySet.filter(mapPos => mapPos.row.nr + mapPos.col.nr == 4).size==3) GameOver("diagonalWins")
    else GamePlaying(newBoard)
  }

}

class TicTacToeTest {
  val C1 = Col(1);val C2 = Col(2);val C3 = Col(3)
  val R1 = Row(1);val R2 = Row(2);val R3 = Row(3)
  @Test def newGameIsNotOver: Unit = assertThat(GamePlaying()).isInstanceOf(classOf[GamePlaying])
//  @Test def stepCanBeAdded:Unit = assertThat(GamePlaying().step(Position(1,1), X)).isInstanceOf(classOf[GamePlaying])
//  @Test def fieldAlreadyTaken:Unit = assertThat(GamePlaying().step(Position(1,1), X).step(Position(1,1), X)).isEqualTo(GameOver("alreadyTaken"))
//  @Test def coolumnTaken: Unit = assertThat(GamePlaying().step(Position(1,1), X).step(Position(1,2), X).step(Position(1,3), X)).isEqualTo(GameOver("colWins"))
//  @Test def rowTaken: Unit = assertThat(GamePlaying().step(Position(1,1), X).step(Position(2,1), X).step(Position(3,1), X)).isEqualTo( GameOver("rowWins"))
//  @Test def diagonal1Taken: Unit = assertThat(GamePlaying().step(Position(1,1), X).step(Position(2,2), X).step(Position(3,3), X)).isEqualTo(GameOver("diagonalWins"))
//  @Test def diagonal2Taken: Unit = assertThat(GamePlaying().step(Position(1,3), X).step(Position(2,2), X).step(Position(3,1), X)).isEqualTo(GameOver("diagonalWins"))
  
}