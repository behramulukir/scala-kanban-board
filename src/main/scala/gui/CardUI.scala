package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, FlowPane, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.{Menu, MenuBar, MenuButton, MenuItem, TextArea}
import scalafx.scene.paint.*
import scalafx.scene.text.{Text, TextAlignment, TextFlow}

import scala.collection.mutable
import scala.language.implicitConversions

class CardUI(parentNode: StageUI, card: Card) extends FlowPane{

  this.prefWidth <== parentNode.width - 20
  this.prefHeight = 150

  //Text area for displaying and changing the card description
  var descriptionUI = new TextArea("test material")
  descriptionUI.prefWidth <== this.width
  descriptionUI.prefHeight <== this.height * 0.4

  //Text to show time until deadline
  var deadlineUI = new TextFlow()
  var deadlineText = new Text("deadline")
  deadlineUI.children = deadlineText
  deadlineUI.textAlignment = TextAlignment.Center
  deadlineUI.prefWidth <== this.width * 0.75
  deadlineUI.prefHeight <== this.height * 0.2

  //Menu buttons for card actions
  var cardActions = new MenuButton()

  var removeButton = new MenuItem("Remove")
  var archiveButton = new MenuItem("Archive")
  var deadlineButton = new MenuItem("Add Deadline")
  var tagButton = new MenuItem("Add Tag")

  cardActions.items =  List(removeButton, archiveButton, deadlineButton, tagButton)

  cardActions.prefWidth <== this.width * 0.2
  cardActions.prefHeight <== this.height * 0.2

  //Text to show tags
  var tagsUI = new TextFlow()
  var tagsText = new Text("tags")
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