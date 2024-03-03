package backend

import scala.collection.mutable._

class Tag(tagName: String) {

  //Name of the tag
  val name = tagName

  //List of cards with this tag
  var cards: Buffer[Card] = Buffer()

  //Adding a card to this tag
  def addCard(card: Card): Unit = cards = cards.addOne(card)
  
  //Removing the card from this tag
  def removeCard(card: Card): Unit = cards.remove(cards.indexOf(card))


}
