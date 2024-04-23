package backend

import java.time.LocalDate
import scala.collection.mutable._

//In my implementation, stage means columns/lists at the kanban board
class Stage(board: Board) {
  
  //Unique identifier for stage
  private val random = scala.util.Random
  var identifier: String = "2/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
  
  //List of all cards in the stage/list
  var allCards: Buffer[Card] = Buffer()
  
  //Board of stage/list
  var currentBoard = board
  
  //Description of the stage/list
  var name = "List name"
  
  //Changing the stage/list name
  def changeName(newName: String) = {
    name = newName
  }
  
  //Adding a card to this stage/list
  def addCard = {
    val card = new Card(this.board, this)
    allCards = allCards.addOne(card)
    board.addCard(card)
    card
  }

  //Removing a card from the stage and deleting it completely
  def removeCard(card: Card): Unit = {
    val indexOfStage = allCards.indexOf(card)
    allCards.remove(indexOfStage)
    board.removeCard(card)
  }
}
