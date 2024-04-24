package gui

import backend.*

import scalafx.scene.layout.VBox
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ChoiceDialog, Menu, MenuBar, MenuItem, TextInputDialog}
import scala.language.implicitConversions

//Top menu bar for some settings such as changing to other boards, import, and export
class TopBarUI(parentNode: VBox) extends MenuBar:
    prefHeight <== parentNode.height * 0.05
    prefWidth <== parentNode.width

    //Menu - Settings. There are menu items for things such as changing board name
    val setting = new Menu("Settings") {

      //Menu item to change the name of the current board
      //If there isn't any board to change name, then it gives an alert
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
            App.currentBoardOption.get.changeName(result.getOrElse(App.currentBoardOption.get.name))
            App.start()
    }

      //Menu item to add a new board to the app
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
            App.currentBoardOption = Some(newBoard)
            App.start()
        }

      //Menu item to delete boards
      //If there isn't any board to delete, then it gives an alert
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
                App.currentBoardOption = KanbanApp.allBoards.headOption
              App.start()
        }

      //Menu item to save current status of the application
      val saving = new MenuItem("Save"):
        onAction = (event) => {
          App.folder.listFiles().foreach(_.delete())
          for board <- KanbanApp.allBoards do 
            KanbanApp.exportBoard(board)
        }
      items = List(nameChange, addBoard, deleteBoard, saving)
    }

    //Menu Boards. There is a menu item for each board of KanbanApp
    val boards = new Menu("Boards")

    //Function to add boards to the menu bar button
    def addBoardUI(board: Board) = {
      boards.items += new BoardItem(board):
        onAction = (event) => {
          App.currentBoardOption = KanbanApp.allBoards.find(_.identifier == board.identifier)
          App.start()
        }
    }

     //Adding boards to the top bar
     //The app switches to the board that user clicks on
      boards.items.clear()
      for i <- KanbanApp.allBoards do boards.items += new BoardItem(i):
        onAction = (event) => {
          App.currentBoardOption = KanbanApp.allBoards.find(_.identifier == i.identifier)
          App.start()
        }


      this.menus = List(setting, boards)