package gui

import backend.*
import javafx.beans.value.{ChangeListener, ObservableValue}

import scala.collection.mutable.*
import scalafx.scene.SnapshotParameters
import scalafx.scene.layout.{Border, BorderStroke, BorderStrokeStyle, BorderWidths, CornerRadii, FlowPane, StackPane}
import scalafx.scene.input.{ClipboardContent, DragEvent, MouseEvent, TransferMode}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ButtonBar.ButtonData
import scalafx.scene.control.{Alert, ButtonType, ChoiceDialog, DatePicker, Dialog, MenuButton, MenuItem, TextArea}
import scalafx.scene.text.{Text, TextAlignment, TextFlow}
import scalafx.Includes.{jfxMouseEvent2sfx, jfxDragEvent2sfx}
import scala.collection.mutable
import scala.language.implicitConversions
import java.time.*
import java.time.temporal.ChronoUnit

//CardUI class that represents cards in GUI
class CardUI(parentNode: StageUI, card: Card) extends FlowPane{

  //Defining current parent node and card
  var currentParentNode = parentNode
  var currentCard = card
  var currentCardPane: Option[CardPane] = None

  //Defining dimensions
  this.prefWidth <== currentParentNode.width - 20
  this.prefHeight = 150

  //Text area for displaying and changing the card description
  var descriptionUI = new TextArea(currentCard.description)
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

  //A button to delete cards
  var removeButton = new MenuItem("Delete")
  removeButton.onAction = (event) => {
    currentParentNode.removeCardUI(this)
  }

  //A button to add cards to archive
  var archiveButton = new MenuItem("Archive")
  archiveButton.onAction = (event) => {
    currentParentNode.archiveCardUI(this)
  }

  //Card deadline picker button
  var deadlineButton = new MenuItem("Add deadline")
  deadlineButton.onAction = (event) => {
    var datePickDialog = new Dialog[LocalDate]() {
      initOwner(App.stage)
      title = "Pick a deadline"
      headerText = "Please pick a deadline for this card"
    }
    var datePicker = new DatePicker()
    var okayButton = new ButtonType("Okay!", ButtonData.OKDone)
    datePickDialog.dialogPane().getButtonTypes.add(okayButton)
    datePickDialog.dialogPane().getButtonTypes.add(ButtonType.Cancel)
    datePickDialog.dialogPane().setContent(datePicker)
    datePickDialog.resultConverter = dialogButton => {
      if dialogButton == okayButton then datePicker.getValue else null
    }
    var newDeadlinePick = datePickDialog.showAndWait()
    var newDeadline: Option[LocalDate] = newDeadlinePick match
      case Some(date: LocalDate) => Some(date)
      case None => None
      case _ => throw new Exception("Unidentified deadline format")
      
    if newDeadline.isDefined then
      card.changeDeadline(newDeadline.get)
      var deadline = card.deadline.get
      var daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), deadline)
      deadlineText = new Text(daysLeft.toString + " days left")
    else
      card.deadline = None
      deadlineText = new Text("No deadline")

    deadlineUI.children = deadlineText
  }

  //Text field to show tags
  var tagsUI = new TextFlow()
  var tagsString = card.tags.map(_.name).mkString(", ")
  var tagsText = new Text(tagsString)
  tagsUI.children = tagsText
  tagsUI.textAlignment = TextAlignment.Center
  tagsUI.prefWidth <== this.width

  //A button to add tags to cards
  var addTagButton = new MenuItem("Add tag")
  addTagButton.onAction = (event) => {
    if currentParentNode.currentStage.currentBoard.allTags.isEmpty then
      new Alert(AlertType.Information) {
        initOwner(App.stage)
        title = "Tag Warning!"
        headerText = "There isn't any tag to add."
        contentText = "You can create tags from the left-side bar."
      }.showAndWait()
    else
      val choices = currentParentNode.currentStage.currentBoard.allTags.map(_.name)
      var initialChoice = choices.head
      val dialog = new ChoiceDialog(defaultChoice = initialChoice, choices = choices) {
        initOwner(App.stage)
        title = "Pick a tag"
        headerText = "Pick a tag for this card"
        contentText = "Chosen tag:"
      }

      val result = dialog.showAndWait()
      if result.isDefined then
        var chosenTagName = result.get
        var chosenTag = currentParentNode.currentStage.currentBoard.allTags.filter(_.name == chosenTagName).head
        if card.tags.forall(_.name != chosenTagName) then
          card.addTag(chosenTag)
          tagsString = card.tags.map(_.name).mkString(", ")
          var tagsText = new Text(tagsString)
          tagsUI.children = tagsText
  }

  //A button to remove tags from the cards
  //If there isn't any tag to remove, it gives an alert
  var removeTagButton = new MenuItem("Remove tag")
  removeTagButton.onAction = (event) => {
    if currentCard.tags.isEmpty then
      new Alert(AlertType.Information) {
        initOwner(App.stage)
        title = "Tag Warning!"
        headerText = "There isn't any tag to remove."
        contentText = "You can add tags from the menu button."
      }.showAndWait()
    else
      val choices = currentCard.tags.map(_.name)
      var initialChoice = choices.head
      val dialog = new ChoiceDialog(defaultChoice = initialChoice, choices = choices) {
        initOwner(App.stage)
        title = "Pick a tag"
        headerText = "Select a tag to remove from this card"
        contentText = "Selected tag:"
      }

      val result = dialog.showAndWait()
      if result.isDefined then
        var chosenTagName = result.get
        var chosenTag = currentCard.tags.filter(_.name == chosenTagName).head
        card.removeTag(chosenTag)
        tagsString = card.tags.map(_.name).mkString(", ")
        var tagsText = new Text(tagsString)
        tagsUI.children = tagsText
  }

  //A button that activates a choice dialog to change list/stage of the card
  //It creates an alert if there aren't any other list to move
  var changeStageButton = new MenuItem("Change list")
  changeStageButton.onAction = (event) => {
    if currentParentNode.currentParentPane.stageUIList.size == 1 then
      new Alert(AlertType.Information) {
        initOwner(App.stage)
        title = "List Warning!"
        headerText = "There isn't any other list to move this card."
        contentText = "You can add lists from the left-side bar."
      }.showAndWait()
    else
      var stageChoices = Buffer[String]()
      var stageNumber = 1
      for stageui <- currentParentNode.currentParentPane.stageUIList do
        stageChoices.addOne(stageui.currentStage.name + " (" + stageNumber.toString + ")")
        stageNumber += 1
      val dialog = new ChoiceDialog(defaultChoice = stageChoices.head, choices = stageChoices) {
        initOwner(App.stage)
        title = "Pick a list"
        headerText = "Select a list to move this card"
        contentText = "Selected list:"
      }

      val result = dialog.showAndWait()
      if result.isDefined then
        var chosenString = result.get
        var indexOfChosenStageUI = chosenString(chosenString.length - 2).asDigit
        var chosenStageUI = currentParentNode.currentParentPane.stageUIList(indexOfChosenStageUI - 1)
        moveCard(this.currentParentNode, chosenStageUI, this)
  }

  //Function to remove the deleted tags from the tag list
  def deleteTag(tag: Tag) = {
    if currentCard.tags.contains(tag) then currentCard.removeTag(tag)
    tagsString = card.tags.map(_.name).mkString(", ")
    var tagsText = new Text(tagsString)
    tagsUI.children = tagsText
  }

  //Function to change stage to new stage
  def changeStageUI(stage: Stage) = {
    currentCard.changeStage(stage)
    currentParentNode.removeCardUI(this)
    var newParentNode = currentParentNode.currentParentPane.stageUIList.filter(_.currentStage == stage).head
    currentParentNode = newParentNode
    currentParentNode.children += currentCardPane.get
  }

  //Moving card from one stage to another
  def moveCard(oldStage: StageUI, newStage: StageUI, cardui: CardUI) = {
    oldStage.moveOutCardUI(cardui)
    cardui.currentParentNode = newStage
    newStage.moveInCardUI(this)
    currentCard.changeStage(newStage.currentStage)
  }

  //Adding card actions to a menu button list
  cardActions.items =  List(removeButton, archiveButton, deadlineButton, addTagButton, removeTagButton, changeStageButton)

  //Defining dimensions for the card actions menu button
  cardActions.prefWidth <== this.width * 0.2
  cardActions.prefHeight <== this.height * 0.2

  //Defining the gap between elements of CardUI
  this.vgap = 5
  this.hgap = 5

  //Adding elements of CardUI
  this.children += descriptionUI
  this.children += deadlineUI
  this.children += cardActions
  this.children += tagsUI

}

//Wrapper pane for CardUI
//Mainly utilized in drag-drop interactions and placing cards correctly to the stage/list
class CardPane(cardUI: CardUI) extends StackPane{
  //Defining the critical variables of cardpane and adding CardUI as a child
  cardUI.currentCardPane = Some(this)
  this.children = cardUI
  this.setAlignment(Center)
  
  //Adding border to make it easier to distinguish
  //Color of border depends on randomly picked color of the card
  this.setBorder(new Border(new BorderStroke(cardUI.currentCard.color, BorderStrokeStyle.Solid, CornerRadii(2), BorderWidths(3))))

  //Drag event starts
  def dragStart(event: MouseEvent) = {
    var dragStatus = startDragAndDrop(TransferMode.Any:_*)
    var cardIDClip = new ClipboardContent()
    cardIDClip.putString(cardUI.currentCard.identifier)
    dragStatus.setContent(cardIDClip)
    this.setScaleX(0.8)
    this.setScaleY(0.8)
    var snapshotParams = new SnapshotParameters()
    dragStatus.setDragView(this.snapshot(snapshotParams, null), event.getX, event.getY)
    event.consume()
  }
  this.onDragDetected = (event) => dragStart(event)

  //Drag event is finished
  def dragDone(event: DragEvent) = {
    this.setScaleX(1)
    this.setScaleY(1)
  }
  this.onDragDone = (event) => dragDone(event)
}

//Helper class that is used while representing cards in archive menu as menu items
class ArchivedCard(card: Card) extends MenuItem(card.description) {
  var description = card.description
  var identifier = card.identifier
}