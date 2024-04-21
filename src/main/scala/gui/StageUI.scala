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
import scalafx.scene.input.{DragEvent, TransferMode}
import scalafx.scene.paint.*
import scalafx.Includes.{jfxMouseEvent2sfx, jfxDragEvent2sfx}

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
  
  //CardPane list
  var cardUIList = Buffer[CardUI]()
  var cardPaneList = Buffer[CardPane]()

  //Function for adding CardUIs to the box
  def addCardUI(card: Card) = {
    if currentParentPane.cardUIList.filter(_.currentCard == card).nonEmpty then
      this.children += currentParentPane.cardUIList.filter(_.currentCard == card).head.currentCardPane.get
    else
      var cardui = new CardUI(this, card)
      cardUIList.addOne(cardui)
      var cardpane = new CardPane(cardui)
      cardPaneList.addOne(cardpane)
      currentParentPane.cardUIList.addOne(cardui)
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
    cardUIList.remove(cardUIList.indexOf(cardui))
    this.currentStage.removeCard(cardui.currentCard)
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
  }

  //Filtering cards based on tag
  var filteredCardUI = Buffer[CardUI]()
  def filterCardUI(tag: Tag) = {
    filteredCardUI = cardUIList.filterNot(_.currentCard.tags.contains(tag))
    for element <- filteredCardUI do
      this.children.remove(this.children.indexOf(element.currentCardPane.get))
  }

  //Removing tag filter from the view
  def removeFiltering = {
    for cardui <- filteredCardUI do
      this.children.add(cardui.currentCardPane.get)
    filteredCardUI.clear()
  }

  //Moving cards to this stage
  def moveInCardUI(cardUI: CardUI) = {
    cardUIList.addOne(cardUI)
    currentStage.allCards.addOne(cardUI.currentCard)
    this.children.add(cardUI.currentCardPane.get)
  }

  //Moving cards from this stage
  def moveOutCardUI(cardui: CardUI) = {
    cardUIList.remove(cardUIList.indexOf(cardui))
    currentStage.allCards.remove(currentStage.allCards.indexOf(cardui.currentCard))
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
  }


  //When a card is dragged over a stage
  this.onDragOver = (event) => {
    if event.getGestureSource() != this && event.getDragboard().hasString() then
      event.acceptTransferModes(TransferMode.Move)
    event.consume()
  }

  //When a card is dropped on a stage
  this.onDragDropped = (event) =>  {
    var status = false
    if event.getDragboard.hasString() then
      var cardid = event.getDragboard.getString
      for stageui <- currentParentPane.stageUIList do
        var carduiIndex = 0
          while !status && carduiIndex < stageui.cardUIList.length do
            var cardui = stageui.cardUIList(carduiIndex)
            if cardui.currentCard.identifier == cardid then
              cardui.moveCard(cardui.currentParentNode, this, cardui)
              status = true
            carduiIndex += 1

    event.setDropCompleted(true)
    event.consume()
  }

  //Adding border to stages to make it easy to distinguish
  this.setBorder(new Border(new BorderStroke(Color.Black, BorderStrokeStyle.Solid, CornerRadii.Empty, BorderWidths(2))))

}
