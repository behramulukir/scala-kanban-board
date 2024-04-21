package backend

import scalafx.scene.image.*
import java.time._
import scala.collection.mutable._

class Board(boardName: String) {
  //Creating a random identifier for each board
  private val random = scala.util.Random
  val identifier: String = "1/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString // How to assign each item a unique identifier was described detailedly in the technical plan.
  //Name of the board
  var name: String = boardName
  
  //Lists of subelements of board
  var allStages: Buffer[Stage] = Buffer()
  var allCards: Buffer[Card] = Buffer()
  var allTags: Buffer[Tag] = Buffer()
  var archivedCards: Buffer[Card] = Buffer()
  var showableCards: Buffer[Card] = Buffer()
  
  //Background feature for board
  var background: Option[Image] = None
  
  //Adding a new card
  def addCard(card: Card) =
    allCards = allCards.addOne(card)
    card
  end addCard
  
  //Removing a card
  def removeCard(card: Card): Unit =
    val indexOfBoard = allCards.indexOf(card)
    allCards.remove(indexOfBoard)
  end removeCard

  //Adding a new stage (column)
  def addStage =
    val identifier: String = "2/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
    val stage = new Stage(this)
    allStages = allStages.addOne(stage)
    stage
  end addStage

  //Remove a stage
  def removeStage(stage: Stage) =
    for card <- stage.allCards do {
      this.removeCard(card)
    }
    val indexOfStage = allStages.indexOf(stage)
    allStages.remove(indexOfStage)
  end removeStage
  
  //Archive a card
  def archiveCard(cardToArchive: Card) =
    cardToArchive.archiveCard()
    archivedCards.addOne(cardToArchive)
  
  //Dearchive a card
  def dearchiveCard(cardToDearchive: Card) =
    cardToDearchive.dearchiveCard()  
    var cardIndex = archivedCards.indexOf(cardToDearchive)
    archivedCards.remove(cardIndex)
    showableCards += cardToDearchive
  
  //Filter cards based on the list of tags provided
  def filter(tag: Tag): Buffer[Card] =
    showableCards = showableCards.addAll(allCards.filter(_.tags.contains(tag)))
    showableCards
  end filter

  //Change name of the board
  def changeName(newName: String) = name = newName

  //Change background of the board with given image
  def changeBackground(image: Image) =
    background = Some(image)
  end changeBackground

  //Function that creates a tag if it doesn't exist before
  private def tagCreation(tagName: String) = {
    var newTag = new Tag(tagName)
    this.allTags.addOne(newTag)
  }

  //Function that is actually used while adding a tag to the card. 
  //It checks if that tag exists, if not creates the tag and adds to the card. 
  //If the tag exists, it adds the tag to the card
  def tagAddition(tagName: String): Boolean ={
    var tagIndex = -1
    
    if tagName != "" then
      if this.allTags.forall(_.name != tagName) then
        tagCreation(tagName)
        true
      else 
        false
    else
      false

  }

  def removeTag(tag: Tag) = {
    allTags.remove(allTags.indexOf(tag))
  }
  
}
