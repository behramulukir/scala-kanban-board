package backend

import scalafx.scene.paint._
import scalafx.scene.image._
import java.time._
import scala.collection.mutable._
import scala.util.Random

class Card(board: Board, initialStage: Stage) {
  
  //Dimesions of the card, dependent on the window size
  var width = 100
  var height = 100
  
  //At what stage/column the card is at
  var stage = initialStage
  
  //Archive status of the card
  var archiveStatus = false
  
  //List of tags for the card
  var tags: Buffer[Tag] = Buffer()
  
  //List of possible interactions linked to the card
  var interactionTypes: Buffer[Interaction] = Buffer()
  
  //List of attachments linked to the card 
  var attachments: Buffer[Attachment] = Buffer()
  
  //Random ID value for the card
  private val random = scala.util.Random
  var identifier: String = "3/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
  
  //List of colors a card can be
  private val colors: Buffer[Color] = Buffer(Constants.darkGreen, Constants.green, Constants.yellow, Constants.rose, Constants.violet, Constants.paleBlue, Constants.red, Constants.orange, Constants.turquois)

  //Text content of the card
  var description = ""
  
  //Optional deadline for the card
  var deadline: Option[LocalDate] = None
  
  //Color of the card, it is random in the beginning
  var color = Random.shuffle(colors).head
  
  //Optional template for the card
  var template: Option[Image] = None
  
  //Optional checklist for the card
  var checklist: Option[Checklist] = None
  
  //Distinguishing number of optional interaction attached to the card
  var interactionKey: Option[Interaction] = None

  //Progress of the checklist of the card
  private var progress: Option[Double] = if (checklist.nonEmpty){
    Some(checklist.get.uncompletedSteps.size / (checklist.get.uncompletedSteps.size + checklist.get.completedSteps.size))
  }else None

  //Function for changing textual content
  def changeDescription(newDesc: String) ={
    description = newDesc
  }

  //Changing the deadline for the card
  def changeDeadline(newDeadline: LocalDate): Unit = {
    deadline = Some(newDeadline)
  }

  //Removing the deadline of the card
  def removeDeadline = deadline = None

  //Function for adding a checklist to the card
  def addChecklist = {
    val newChecklist = new Checklist
    checklist = Some(newChecklist)
  }

  //Function for removing the checklist to the card
  def removeChecklist = checklist = None

  //Functino for changing the color of the card
  def changeColor(newColor: Color) = color = newColor
  
  //Function for simply a tag to this card
  def addTag(newTag: Tag) = {
    tags = tags.addOne(newTag)
    newTag.addCard(this)
  }
  
  //Function that creates a tag if it doesn't exist before
  private def tagCreation(tagName: String) = {
    var newTag = new Tag(tagName)
    this.board.allTags.addOne(newTag)
    addTag(newTag)
  }

  //Function that is actually used while adding a tag to the card. 
  //It checks if that tag exists, if not creates the tag and adds to the card. 
  //If the tag exists, it adds the tag to the card
  def tagAddition(tagName: String): Unit ={
    var tagIndex = -1

    for (i <- this.board.allTags){
      if (i.name == tagName) tagIndex = this.board.allTags.indexOf(i)
    }

    if (tagIndex == -1) tagCreation(tagName) else addTag(this.board.allTags(tagIndex))
  }

  //Function for removing a tag from the card
  def removeTag(tag: Tag): Unit = {
    tags.remove(tags.indexOf(tag))
    tag.removeCard(this)
  }

  //Function for adding a template to the card
  def addTemplate(image: Image) = template = Some(image)
  
  //Function for removing the template from the card
  def removeTemplate = template = None

  //Function adding an attachment to the card
  def addAttachment(attachment: Attachment) = attachments = attachments.addOne(attachment)

  //Function for removing an attachment from the card
  def removeAttachment(attachment: Attachment) = attachments.remove(attachments.indexOf(attachment))

  //Function for checking progress in checklist
  def calculateProgress(checklist: Checklist) = checklist.progress

  //Function for adding an interaction to the card.
  //Interaction types: 1 = Adding tag, 2 = Opening URL, 3 = Empyting the card
  def addInteraction(interactionType: Int, content: String) = {
    if (interactionType == 0){
      var tagIndex = -1
      for (i <- this.board.allTags){
        if (i.name == content) tagIndex = this.board.allTags.indexOf(i)
      }
      if (tagIndex == -1) {
        tagCreation(content)
        val tagInteraction = new AddTag(this.board.allTags.last, this)
        interactionKey = Some(tagInteraction)
      } else {
        val tagInteraction = new AddTag(this.board.allTags(tagIndex), this)
        interactionKey = Some(tagInteraction)
      }
    }else if (interactionType == 1){
      val urlInteraction = new OpenHomeURL(content)
      interactionKey = Some(urlInteraction)
    } else if(interactionType == 2){
      val emptyInteraction = new EmptyCard(this)
      interactionKey = Some(emptyInteraction)
    }
  }

  //Function for changing the stage/column of the card
  def changeStage(newStage: Stage) = stage = newStage

  //Archiving functionality for the card
  def archiveCard() = archiveStatus = true
  
  //Dearchiving functionality for the card
  def dearchiveCard() = archiveStatus = false
}
