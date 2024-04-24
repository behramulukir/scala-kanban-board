package gui

import backend.*

import scala.collection.mutable.*
import scalafx.scene.layout.{StackPane, VBox}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ChoiceDialog, MenuButton, MenuItem, TextInputDialog}
import scala.collection.mutable
import scala.language.implicitConversions

//Pane that contains left-side settings such as archive, tags, add board, add list
class MenuBarUI(parentPane: KanbanUI, boardui: BoardUI) extends VBox{

  //Defining the current board
  var currentBoard = boardui.currentBoard

  //Variable to store if filtering is on
  var filterOption: Option[Tag] = None

  //Defining the preffered dimensions
  this.prefHeight <== parentPane.height
  this.prefWidth <== parentPane.width * 0.15

  //Archive button showing all archived cards with their descriptions
  //When user clicks on items, the cards will be returned to their original lists/stages
  val archiveList = Buffer[ArchivedCard]()
  val archiveButton: MenuButton = new MenuButton("Archive"):
    for i <- currentBoard.archivedCards do
      var archiveElement = new ArchivedCard(i)
      archiveElement.onAction = (event) => {
        var selectedCards = boardui.currentBoard.archivedCards.filter(_.identifier == archiveElement.identifier)
        var deArchiveCard = selectedCards.head
        if filterOption.isDefined && !deArchiveCard.tags.contains(filterOption.get) then
          boardui.currentBoard.dearchiveCard(deArchiveCard)
          var stageToReturn = deArchiveCard.stage.identifier
          var stageUIList = boardui.stageUIList.filter(_.currentStage.identifier == deArchiveCard.stage.identifier)
          var stageUIToReturn = stageUIList.head
          deArchiveCardButton(deArchiveCard)
        else
          boardui.currentBoard.dearchiveCard(deArchiveCard)
          var stageToReturn = deArchiveCard.stage.identifier
          var stageUIList = boardui.stageUIList.filter(_.currentStage.identifier == deArchiveCard.stage.identifier)
          var stageUIToReturn = stageUIList.head
          stageUIToReturn.addCardUI(deArchiveCard)
          deArchiveCardButton(deArchiveCard)
      }
      archiveList += archiveElement
    items = archiveList.toList

  //Container for archive button
  val archivePane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += archiveButton

  //Tags button to filter cards based on the tag
  //Only cards with a selected tag will be shown on the board
  val tagsList = Buffer[MenuItem]()
  val tagsButton = new MenuButton("Tags"):
    tagsList += new MenuItem("No filtering"):
      onAction = (event) => {
        filterOption = None
        for stageui <- boardui.stageUIList do
          stageui.removeFiltering()
      }

    //Adding tags to the menu
    for i <- currentBoard.allTags do tagsList += new MenuItem(i.name):
      onAction = (event) => {
        var filterTag = currentBoard.allTags.filter(_.name == i.name).head
        filterOption = Some(filterTag)
        var showCards = currentBoard.filter(filterTag)
        for stageui <- boardui.stageUIList do
          stageui.removeFiltering()
          stageui.filterCardUI(filterTag)
      }

    items = tagsList.toList

  //Container for tags button
  val tagsPane = new StackPane():
    prefHeight <== parentPane.height / 3
    children += tagsButton

  //Button to add lists/stages/columns
  val listButton = new Button("Add List"):
    onAction = (event) => {
      var newStage = boardui.currentBoard.addStage
      boardui.addStageUI(newStage)
    }
    
  //Container for the add list button
  val listButtonPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += listButton

  //Menu button to manage tags (create tag / delete tag)
  val manageTags = new MenuButton("Manage Tags")

  //Menu item for creating tags
  val createTag = new MenuItem("Create Tag"):
    onAction = (event) => {
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
          tagsList += new MenuItem(result.get):
            onAction = (event) => {
              var filterTag = currentBoard.allTags.filter(_.name == this.getText).head
              filterOption = Some(filterTag)
              var showCards = currentBoard.filter(filterTag)
              for stageui <- boardui.stageUIList do
                stageui.removeFiltering()
                stageui.filterCardUI(filterTag)
            }

      tagsButton.items = tagsList
    }

  //Menu item for deleting tags
  //If there aren't any tag to delete, it gives an alert
  val deleteTag = new MenuItem("Delete Tag"):
    onAction = (event) => {
      if currentBoard.allTags.isEmpty then
        new Alert(AlertType.Information) {
          initOwner(App.stage)
          title = "Tag Warning!"
          headerText = "There isn't any tag to delete."
          contentText = "You can create tags from the left-side bar."
        }.showAndWait()
      else
        val choices = currentBoard.allTags.map(_.name)
        var initialChoice = choices.head
        val dialog = new ChoiceDialog(defaultChoice = initialChoice, choices = choices) {
          initOwner(App.stage)
          title = "Pick a tag"
          headerText = "Select a tag to delete"
          contentText = "Selected tag:"
        }

        val result = dialog.showAndWait()
        if result.isDefined then
          var chosenTagName = result.get
          var chosenTag = currentBoard.allTags.filter(_.name == chosenTagName).head
          currentBoard.removeTag(chosenTag)
          removeTagButton(chosenTagName)
          filterOption = None
          for stageui <- boardui.stageUIList do
            stageui.removeFiltering()
            for cardui <- stageui.cardUIList do
              cardui.deleteTag(chosenTag)
    }

  manageTags.items = List(createTag, deleteTag)

  //Pane for tag management menu button
  val manageTagsPane = new StackPane():
    prefHeight <== parentPane.height / 6
    children += manageTags

  //Adding buttons to left pane and adjusting its location
  this.children += archivePane
  this.children += tagsPane
  this.children += manageTagsPane
  this.children += listButtonPane


  //Function to adding card uis to archive
  def addArchiveCard(cardui: CardUI) = {
    var archiveElement = new ArchivedCard(cardui.currentCard)
      archiveElement.onAction = (event) => {
        var selectedCards = boardui.currentBoard.archivedCards.filter(_.identifier == archiveElement.identifier)
        var deArchiveCard = selectedCards.head
        if filterOption.isDefined && !deArchiveCard.tags.contains(filterOption.get) then
          boardui.currentBoard.dearchiveCard(deArchiveCard)
          var stageToReturn = deArchiveCard.stage.identifier
          var stageUIList = boardui.stageUIList.filter(_.currentStage.identifier == deArchiveCard.stage.identifier)
          var stageUIToReturn = stageUIList.head
          deArchiveCardButton(deArchiveCard)
        else
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
    archiveButton.items = archiveList.toList
  }

  //Function to remove tags from the menu button list
  def removeTagButton(tagName: String) = {
    var tagButtonRemove = tagsList.filter(_.getText == tagName).head
    tagsList.remove(tagsList.indexOf(tagButtonRemove))
    tagsButton.items = tagsList
  }


}

