package backend

import java.awt.Desktop
import java.net._
import java.time.LocalDate
import scala.collection.mutable._

//Abstract class for interaction
abstract class Interaction {

  //Randomly created ID for interactions
  private val random = scala.util.Random
  val identifier: String = "5/" + LocalDate.now.toString + "/" + random.nextInt(100000).toString
}

//Interaction class for adding tags
//It adds the given tag to your card
//It is subject to change. New idea is making this as aa shortcut to adding any tag to the card
class AddTag(content: Tag, card: Card) extends Interaction {
  def addTag(content: Tag, card: Card): Unit ={
    card.addTag(content)
  }
}

//Interaction class for opening given URL
class OpenHomeURL(content: String) extends Interaction {
  def openLink(content: String): Unit ={
    if (Desktop.isDesktopSupported) {
      Desktop.getDesktop.browse(new URI(content))
    }
  }
}

//Interaction class for empyting the card, it resets all the variables to empty
class EmptyCard(card: Card) extends Interaction {
  def empty(card: Card) = {
    card.tags = Buffer()
    card.interactionTypes= Buffer()
    card.attachments = Buffer()

    card.description = ""
    card.deadline = None
    //var color: Color = ??? // Colors will be decided after the final design choices.
                           // Here, a random color will be assigned once again.
    card.template = None
    card.checklist = None
    card.interactionKey = None
  }
}
