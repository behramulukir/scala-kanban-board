package gui

import backend.*
import javafx.geometry

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, HBox, Pane, StackPane, VBox}
import scalafx.geometry.Pos
import scalafx.geometry.Pos.Center
import scalafx.scene.control.{Button, ComboBox, Menu, MenuBar, MenuButton, MenuItem, TextInputDialog}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

//Pane that contains left-side settings such as archive, tags, add board, add list
class MenuBarUI(parentPane: KanbanUI, boardui: BoardUI) extends VBox{


  var currentBoard = boardui.currentBoard

  this.prefHeight <== parentPane.height
  this.prefWidth <== parentPane.width * 0.15

  //Archive button showing all archived cards with their descriptions
  val archiveList = Buffer[ArchivedCard]()
  val archiveButton: MenuButton = new MenuButton("Archive"):
    for i <- currentBoard.archivedCards do
      var archiveElement = new ArchivedCard(i)
      archiveElement.onAction = (event) => {
        var selectedCards = boardui.currentBoard.archivedCards.filter(_.identifier == archiveElement.identifier)
        var deArchiveCard = selectedCards.head
        boardui.currentBoard.dearchiveCard(deArchiveCard)
        var stageToReturn = deArchiveCard.stage.identifier
        var stageUIList = boardui.stageUIList.filter(_.currentStage.identifier == deArchiveCard.stage.identifier)
        var stageUIToReturn = stageUIList.head
        stageUIToReturn.addCardUI(deArchiveCard)
        deArchiveCardButton(deArchiveCard)
      }
      archiveList += archiveElement
    items = archiveList.toList

  val archivePane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += archiveButton

  //Tags button showing all archived cards with their descriptions
  val tagsList = Buffer[MenuItem]()
  val tagsButton = new MenuButton("Tags"):
    for i <- currentBoard.allTags do tagsList += new MenuItem(i.name)
    items = tagsList.toList

  val tagsPane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += tagsButton

  //List button to add lists
  val listButton = new Button("Add List"):
    onAction = (event) => {
      var newStage = boardui.currentBoard.addStage
      boardui.addStageUI(newStage)
    }
  val listButtonPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += listButton

  //Board button to add boards
  val addTagButton = new Button("Add Tag")
  addTagButton.onAction = (event) => {
    val dialog = new TextInputDialog(defaultValue = "New Tag") {
      initOwner(App.stage)
      title = "Create a new tag"
      headerText = "Enter the tag name."
      contentText = "Name of the tag:"
    }

    val result = dialog.showAndWait()
    var newTagStatus = currentBoard.tagAddition(result.getOrElse(""))
    if newTagStatus then
      if result.getOrElse("") != "" then
        tagsList += new MenuItem(result.get)
      
    tagsButton.items = tagsList
  }
  
  
  val addTagButtonPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += addTagButton



  //Adding buttons to left pane and adjusting its location
  this.children += archivePane
  this.children += tagsPane
  this.children += listButtonPane
  this.children += addTagButtonPane


  //Function to add cards to archive
  def addArchiveCard(cardui: CardUI) = {
    var archiveElement = new ArchivedCard(cardui.currentCard)
      archiveElement.onAction = (event) => {
        var selectedCards = boardui.currentBoard.archivedCards.filter(_.identifier == archiveElement.identifier)
        var deArchiveCard = selectedCards.head
        boardui.currentBoard.dearchiveCard(deArchiveCard)
        var stageToReturn = deArchiveCard.stage.identifier
        var stageUIList = boardui.stageUIList.filter(_.currentStage.identifier == deArchiveCard.stage.identifier)
        var stageUIToReturn = stageUIList.head
        stageUIToReturn.addCardUI(deArchiveCard)
        deArchiveCardButton(deArchiveCard)
      }
      archiveList += archiveElement
    archiveButton.items = archiveList.toList
  }

  
  //Function to dearchive cards
  def deArchiveCardButton(card: Card) = {
    archiveList.remove(archiveList.indexOf(archiveList.filter(_.identifier == card.identifier).head))
    print(archiveList)
    archiveButton.items = archiveList.toList
  }

  //Function to remove tags from the menu button list
  def removeTagButton(tagName: String) = {
    var tagButtonRemove = tagsList.filter(_.text.toString == tagName).head
    tagsList.remove(tagsList.indexOf(tagButtonRemove))
    tagsButton.items = tagsList
  }


}

