package backend

import scalafx.scene.paint._
import scalafx.scene.image._
import java.time._
import scala.collection.mutable._
import scala.util.Random

class Card(board: Board, initialStage: Stage) {

  //At what stage/column the card is at
  var stage = initialStage
  
  //Archive status of the card
  var archiveStatus = false
  
  //List of tags for the card
  var tags: Buffer[Tag] = Buffer()
  
  //Random ID value for the card
  private val random = scala.util.Random
  var identifier: String = "3/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
  
  //List of colors a card can be
  private val colors: Buffer[Color] = Buffer(Constants.darkGreen, Constants.green, Constants.yellow, Constants.rose, Constants.violet, Constants.paleBlue, Constants.red, Constants.orange, Constants.turquois)

  //Text content of the card
  var description = ""
  
  //Optional deadline for the card
  var deadline: Option[LocalDate] = None
  
  //Color of the card, it is randomly selected
  var color = Random.shuffle(colors).head

  //Function for changing textual content
  def changeDescription(newDesc: String) ={
    description = newDesc
  }

  //Changing the deadline for the card
  def changeDeadline(newDeadline: LocalDate): Unit = {
    deadline = Some(newDeadline)
  }

  //Removing the deadline of the card
  def removeDeadline() = deadline = None

  //Functino for changing the color of the card
  def changeColor(newColor: Color) = color = newColor
  
  //Function for simply a tag to this card
  def addTag(newTag: Tag) = {
    tags = tags.addOne(newTag)
    newTag.addCard(this)
  }

  //Function for removing a tag from the card
  def removeTag(tag: Tag): Unit = {
    tags.remove(tags.indexOf(tag))
    tag.removeCard(this)
  }

  //Function for changing the stage/column of the card
  def changeStage(newStage: Stage) = stage = newStage

  //Archiving functionality for the card
  def archiveCard() = {
    archiveStatus = true
  }
  
  //Dearchiving functionality for the card
  def dearchiveCard() = {
    archiveStatus = false
  }
}
