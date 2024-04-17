package gui

import backend.*
import javafx.beans.value.{ChangeListener, ObservableValue}

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, FlowPane, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem, TextArea}
import scalafx.scene.paint.*
import scalafx.scene.text.{Text, TextAlignment, TextFlow}

import java.time.*
import java.time.temporal.ChronoUnit
import scala.collection.mutable
import scala.language.implicitConversions

class CardUI(parentNode: StageUI, card: Card) extends FlowPane{

  var currentParentNode = parentNode

  this.prefWidth <== currentParentNode.width - 20
  this.prefHeight = 150

  var currentCard = card
  var currentCardPane: Option[CardPane] = None

  //Text area for displaying and changing the card description
  var descriptionUI = new TextArea(card.description)
  descriptionUI.prefWidth <== this.width
  descriptionUI.prefHeight <== this.height * 0.4
  descriptionUI.textProperty().addListener(new ChangeListener[String]:
    override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit =
      card.changeDescription(t1)
   )

  //Text to show time until deadline
  var deadlineUI = new TextFlow()
  var deadlineText = new Text("No deadline")
  if card.deadline.isDefined then {
    var deadline = card.deadline.get
    var daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline)
    deadlineText = new Text(daysLeft.toString + " days left")
  }
  deadlineUI.children = deadlineText
  deadlineUI.textAlignment = TextAlignment.Center
  deadlineUI.prefWidth <== this.width * 0.75
  deadlineUI.prefHeight <== this.height * 0.2

  //Menu buttons for card actions
  var cardActions = new MenuButton()

  var removeButton = new MenuItem("Remove")
  removeButton.onAction = (event) => {
    currentParentNode.removeCardUI(this)
  }

  var archiveButton = new MenuItem("Archive")
  archiveButton.onAction = (event) => {
    currentParentNode.archiveCardUI(this)
  }

  var deadlineButton = new MenuItem("Add Deadline")

  var tagButton = new MenuItem("Add Tag")

  cardActions.items =  List(removeButton, archiveButton, deadlineButton, tagButton)

  cardActions.prefWidth <== this.width * 0.2
  cardActions.prefHeight <== this.height * 0.2

  //Text to show tags
  var tagsUI = new TextFlow()
  var tagsString = card.tags.mkString(", ")
  var tagsText = new Text(tagsString)
  tagsUI.children = tagsText
  tagsUI.textAlignment = TextAlignment.Center
  tagsUI.prefWidth <== this.width


  this.vgap = 5
  this.hgap = 5

  this.children += descriptionUI
  this.children += deadlineUI
  this.children += cardActions
  this.children += tagsUI

  //this.setBorder(new Border(new BorderStroke(card.color, BorderStrokeStyle.Solid, CornerRadii(2), BorderWidths(1))))

}

class CardPane(cardUI: CardUI) extends StackPane{
  cardUI.currentCardPane = Some(this)
  this.children = cardUI
  this.setAlignment(Center)
  this.setBorder(new Border(new BorderStroke(cardUI.currentCard.color, BorderStrokeStyle.Solid, CornerRadii(2), BorderWidths(3))))
}