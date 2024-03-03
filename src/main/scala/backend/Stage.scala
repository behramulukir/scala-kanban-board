package backend

import java.time.LocalDate
import scala.collection.mutable._

//In my implementation, stage means columns at the kanban board
class Stage(board: Board) {
  
  //Unique identifier for stage
  private val random = scala.util.Random
  val identifier: String = "2/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
  
  //List of all cards in the stage
  var allCards: Buffer[Card] = Buffer()

  //Dimensions of a stage, they are dependent on the window size
  var width = ???
  var height = ???
  
  //Adding a card to this stage
  def addCard = {
    val card = new Card(this.board, this)
    allCards = allCards.addOne(card)
  }

  //Removing a card from the stage and deleting it completely
  def removeCard(card: Card): Unit = {
    val indexOfStage = allCards.indexOf(card)
    allCards.remove(indexOfStage)
  }
}
