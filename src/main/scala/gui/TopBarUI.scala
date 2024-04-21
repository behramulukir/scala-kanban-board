package gui

import backend.*

import scala.collection.mutable.*
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane, VBox}
import scalafx.geometry.Pos
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ChoiceDialog, Menu, MenuBar, MenuButton, MenuItem, TextInputDialog}
import scalafx.scene.paint.*

import scala.collection.mutable
import scala.language.implicitConversions

//Top menu bar for some settings such as changing to other boards, import, and export
class TopBarUI(parentNode: VBox) extends MenuBar:
    prefHeight <== parentNode.height * 0.05
    prefWidth <== parentNode.width

    //Menu 1 - Settings. There are menu items for things such as changing board name
    val setting = new Menu("Settings") {
      val nameChange = new MenuItem("Change name"):
        onAction = (event) => {
          if KanbanApp.allBoards.isEmpty then
          new Alert(AlertType.Information) {
            initOwner(App.stage)
            title = "Board Warning!"
            headerText = "There isn't any board to change name."
            contentText = "You can create boards from the top-side bar."
          }.showAndWait()
          else
            val dialog = new TextInputDialog(defaultValue = "Board Name") {
              initOwner(App.stage)
              title = "Change board name"
              headerText = "Enter the new name for the board."
              contentText = "New name for the current board:"
        }
            val result = dialog.showAndWait()
            App.currentBoard.changeName(result.getOrElse(App.currentBoard.name))
            App.start()
    }

      val addBoard = new MenuItem("Add board"):
        onAction = (event) => {
          val dialog = new TextInputDialog(defaultValue = "New Board") {
          initOwner(App.stage)
          title = "Create a new board"
          headerText = "Enter the board name."
          contentText = "Name of the new board:"
          }
          val result = dialog.showAndWait()
          if result.isDefined then
            var newBoard = KanbanApp.addBoard(result.getOrElse("New Board"))
            addBoardUI(newBoard)
            App.currentBoard = newBoard
            App.start()
        }

      //Button to delete boards
      val deleteBoard = new MenuItem("Delete board"):
        onAction = (event) => {
          if KanbanApp.allBoards.isEmpty then
          new Alert(AlertType.Information) {
            initOwner(App.stage)
            title = "Board Warning!"
            headerText = "There isn't any board to delete."
            contentText = "You can create boards from the top-side bar."
          }.showAndWait()
          else
            val choices = KanbanApp.allBoards.map(_.name)
            var initialChoice = choices.head
            val dialog = new ChoiceDialog(defaultChoice = initialChoice, choices = choices) {
              initOwner(App.stage)
              title = "Pick a board"
              headerText = "Select a board to remove from this card"
              contentText = "Selected board:"
            }

            val result = dialog.showAndWait()
            if result.isDefined then
              var chosenBoardName = result.get
              var chosenBoard = KanbanApp.allBoards.filter(_.name == chosenBoardName).head
              KanbanApp.deleteBoard(chosenBoard)
              if KanbanApp.allBoards.nonEmpty then
                App.currentBoard = KanbanApp.allBoards.head
              App.start()
        }
      items = List(nameChange, addBoard, deleteBoard)
    }

    //Menu 2 - Boards. There is a menu item for each board of KanbanApp
    val boards = new Menu("Boards")

    //Menu 3 - File Management. There are two menu items, one for import and one for export
    val fileManagement = new Menu("File Management") {
      val importing = new MenuItem("Import")
      val exporting = new MenuItem("Export")
      items = List(importing, exporting)
      }

    //Function to add boards to the menu bar button
    def addBoardUI(board: Board) = {
      boards.items += new BoardItem(board):
        onAction = (event) => {
          App.currentBoard = KanbanApp.allBoards.filter(_.identifier == board.identifier).head
          App.start()
        }
    }

     //Adding boards to the top bar
      boards.items.clear()
      for i <- KanbanApp.allBoards do boards.items += new BoardItem(i):
        onAction = (event) => {
          App.currentBoard = KanbanApp.allBoards.filter(_.identifier == i.identifier).head
          App.start()
        }


      this.menus = List(setting, boards, fileManagement)

