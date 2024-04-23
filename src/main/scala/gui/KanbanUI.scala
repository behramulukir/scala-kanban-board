package gui

import backend.*

import scalafx.scene.layout.{HBox, VBox}
import scala.language.implicitConversions

class KanbanUI(parentBox: VBox, board: Board) extends HBox() {
  
  //Defining the current board
  var currentBoard = board

  //Defining the dimensions
  this.prefHeight <== parentBox.height * 0.95
  this.prefWidth <== parentBox.width

  //Creating menu bar and board ui
  var boardui = new BoardUI(this, currentBoard)
  var menubarui = new MenuBarUI(this, boardui)

  //Adding menubarui and boardui
  this.children += menubarui
  this.children += boardui

  //Function to add cards to archive menu button
  def addToArchiveMenu(cardui: CardUI) = {
    menubarui.addArchiveCard(cardui)
  }
  
  //Function to remove items from the tag menu
  def removeTagMenu(string: String) = {
    menubarui.removeTagButton(string)
  }

}







 

  




