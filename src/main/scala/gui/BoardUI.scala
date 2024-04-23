package gui

import backend.*

import scala.collection.mutable.*
import scalafx.scene.layout.HBox
import scalafx.scene.control.{MenuItem, ScrollPane, TitledPane}
import scala.collection.mutable
import scala.language.implicitConversions

class BoardUI(parentPane: KanbanUI, board: Board) extends TitledPane{

  //Defining the title of titled pane
  var currentParentPane = parentPane
  var currentBoard = board
  this.text = currentBoard.name

  //List of stage uis
  var stageUIList = ListBuffer[StageUI]()

  //List of card uis
  var cardUIList = ListBuffer[CardUI]()

  //Defining the dimensions
  this.prefHeight <== parentPane.height
  this.prefWidth <== parentPane.width * 0.85

  //Creating ScrollPane
  var boardScroll = new ScrollPane()

  //Creating BoardHBox
  var boardHBox = new HBox()
  boardHBox.setSpacing(10)

  //Adding Hbox to ScrollPane
  boardScroll.setContent(boardHBox)

  //Setting Board Pane as the content of titled pane
  this.setContent(boardScroll)

  //Function to add StageUI to BoardHBox
  def addStageUI(stage: Stage) = {
    stageUIList += new StageUI(this, board, stage)
    boardHBox.children = stageUIList
  }

  //Adding all stages to board initially
  for i <- board.allStages do
    addStageUI(i)

  //Remove stage for UI
  def removeStageUI(stageui: StageUI): Unit = {
    for card <- stageui.currentStage.allCards do
      if card.archiveStatus then
        parentPane.menubarui.deArchiveCardButton(card)
    board.removeStage(stageui.currentStage)
    stageUIList.remove(stageUIList.indexOf(stageui))
    boardHBox.children = stageUIList
  }
  
  //Removing tags from the tag menu if they are deleted
  def removeTagUI(string: String) = {
    parentPane.removeTagMenu(string)
  }
}

//Helper class that is used while listing the boards in the top bar as menu items
class BoardItem(board: Board) extends MenuItem(board.name) {
  var currboard = board
  var description = board.name
  var identifier = board.identifier
}

