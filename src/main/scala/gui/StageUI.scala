package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.event.ActionEvent
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, Border, BorderPane, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, ButtonBar, Menu, MenuBar, MenuButton, MenuItem, TextField, TitledPane}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

class StageUI(parentPane: BoardUI, board: Board, stage: Stage) extends VBox{

  var currentStage = stage
  val currentParentPane = parentPane

  this.prefHeight <== parentPane.height
  this.prefWidth = 200

  this.setSpacing(6)

  //Stage name
  val stageText = new StackPane()
  stageText.minHeight = 30
  stageText.maxHeight = 30
  stageText.prefWidth <== this.width

  val stageNameUI = new TextField():
    text = stage.name
    prefHeight <== stageText.height
    prefWidth <== stageText.width
    onAction = (event) => {
      var newStageName = this.getText
      stage.changeName(newStageName)
    }
  stageText.children += stageNameUI
  stageText.setAlignment(Pos.Center)

  //Remove list and add card buttons
  val stageControl = new HBox()
  stageControl.minHeight = 30
  stageControl.maxHeight = 30
  stageControl.prefWidth <== this.width

  val addCardButton = new Button("Add Card"):
    prefHeight <== stageControl.height
    prefWidth <== stageControl.width * 0.5
    onAction = (event) => {
      var card = stage.addCard
      addCardUI(card)
    }

  val removeStage = new Button("Remove List"):
    prefHeight <== stageControl.height
    prefWidth <== stageControl.width * 0.5
  removeStage.onAction = (event) => {
    parentPane.removeStageUI(this)
  }

  stageControl.children += removeStage
  stageControl.children += addCardButton


  this.children += stageText
  this.children += stageControl

  //Function for adding CardUIs to the box
  def addCardUI(card: Card) = {
    var cardui = new CardUI(this, card)
    var cardpane = new CardPane(cardui)
    this.children += cardpane
  }

  //Adding cards in stage initially
  for i <- stage.allCards do {
    if !i.archiveStatus then {
      this.addCardUI(i)
    }
  }

  //Archive card from view
  def archiveCardUI(cardui: CardUI): Unit = {
    currentStage.currentBoard.archiveCard(cardui.currentCard)
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
    parentPane.currentParentPane.addToArchiveMenu(cardui)
  }

  //Delete a card from view
  def removeCardUI(cardui: CardUI): Unit = {
    this.currentStage.removeCard(cardui.currentCard)
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
  }

  //Adding border to stages to make it easy to distinguish
  this.setBorder(new Border(new BorderStroke(Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(2))))

}