package gui

import backend.*
import javafx.beans.value.{ChangeListener, ObservableValue}

import scala.collection.mutable.*
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, HBox, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.input.TransferMode
import scalafx.scene.paint.*
import scala.collection.mutable
import scala.language.implicitConversions

class StageUI(parentPane: BoardUI, board: Board, stage: Stage) extends VBox{

  //Defining current stage and parent pane
  var currentStage = stage
  val currentParentPane = parentPane

  //Variable to see if there is tag filtering on
  var tagFilter: Option[Tag] = None

  //Defining the dimensions and spacing between elements
  this.prefHeight <== parentPane.height
  this.prefWidth = 200
  this.setSpacing(6)

  //Container for stage/list name
  val stageText = new StackPane()
  stageText.minHeight = 30
  stageText.maxHeight = 30
  stageText.prefWidth <== this.width

  //Interactive text field to change name of stages/lists
  val stageNameUI = new TextField():
    text = stage.name
    prefHeight <== stageText.height
    prefWidth <== stageText.width
  stageNameUI.textProperty().addListener(new ChangeListener[String]:
    override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit =
      stage.changeName(t1)
   )
  stageText.children += stageNameUI
  stageText.setAlignment(Pos.Center)

  //Remove list and add card button container
  val stageControl = new HBox()
  stageControl.minHeight = 30
  stageControl.maxHeight = 30
  stageControl.prefWidth <== this.width

  //Button to add cards to list/stage
  val addCardButton = new Button("Add Card"):
    prefHeight <== stageControl.height
    prefWidth <== stageControl.width * 0.5
    onAction = (event) => {
      var card = stage.addCard
      addCardUI(card)
    }

  //Button to remove stage/list from the board
  val removeStage = new Button("Remove List"):
    prefHeight <== stageControl.height
    prefWidth <== stageControl.width * 0.5
  removeStage.onAction = (event) => {
    parentPane.removeStageUI(this)
  }

  stageControl.children += removeStage
  stageControl.children += addCardButton


  //Adding stage/list control elements to the stage
  this.children += stageText
  this.children += stageControl
  
  //List of CardUIs and CardPanes
  var cardUIList = Buffer[CardUI]()
  var cardPaneList = Buffer[CardPane]()

  //Function for adding CardUIs to the current list
  def addCardUI(card: Card) = {
    if currentParentPane.cardUIList.exists(_.currentCard == card) then
      this.children += currentParentPane.cardUIList.filter(_.currentCard == card).head.currentCardPane.get
    else
      var cardui = new CardUI(this, card)
      cardUIList.addOne(cardui)
      var cardpane = new CardPane(cardui)
      cardPaneList.addOne(cardpane)
      currentParentPane.cardUIList.addOne(cardui)
      this.children += cardpane
  }

  //Adding cards to the stage when the application is launched
  for i <- stage.allCards do {
    if !i.archiveStatus then {
      this.addCardUI(i)
    }
  }

  //Function for archiving carduis from view
  def archiveCardUI(cardui: CardUI): Unit = {
    currentStage.currentBoard.archiveCard(cardui.currentCard)
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
    parentPane.currentParentPane.addToArchiveMenu(cardui)
  }

  //Deleting a cardui from view
  def removeCardUI(cardui: CardUI): Unit = {
    cardUIList.remove(cardUIList.indexOf(cardui))
    this.currentStage.removeCard(cardui.currentCard)
    this.children.remove(this.children.indexOf(cardui.currentCardPane.get))
  }

  //Filtering cards based on tag
  var filteredCardUI = Buffer[CardUI]()
  def filterCardUI(tag: Tag) = {
    tagFilter = Some(tag)
    filteredCardUI = cardUIList.filterNot(_.currentCard.tags.contains(tag))
    for element <- filteredCardUI do
      if !element.currentCard.archiveStatus then
          this.children.remove(this.children.indexOf(element.currentCardPane.get))
  }

  //Removing tag filter from the view
  def removeFiltering() = {
    tagFilter = None
    for cardui <- filteredCardUI do
      if !cardui.currentCard.archiveStatus then
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
    if event.getGestureSource != this && event.getDragboard.hasString then
      event.acceptTransferModes(TransferMode.Move)
    event.consume()
  }

  //When a card is dropped on a stage
  this.onDragDropped = (event) =>  {
    var status = false
    if event.getDragboard.hasString then
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
